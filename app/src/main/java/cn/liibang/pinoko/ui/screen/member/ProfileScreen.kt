package cn.liibang.pinoko.ui.screen.member

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.http.appApi
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.MainViewModel
import cn.liibang.pinoko.ui.support.showToast
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


data class MemberUpdateDTO(val avatar: String, val nickname: String)

@Composable
fun ProfileScreen() {

    var memberUpdateDTO by remember {
        mutableStateOf(MemberUpdateDTO(avatar = "", nickname = ""))
    }

//    mainViewModel.

    var uri by remember {
        mutableStateOf<Uri?>(null)
    }

    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        val member = DataStore.getMemberInfo()
        memberUpdateDTO = memberUpdateDTO.copy(avatar = member.avatar, nickname = member.nickname)
        println("member: $member   memberUpdateDTO: $memberUpdateDTO")
        uri = Uri.parse(member.avatar)
    }

    val context = LocalContext.current

    val singleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "个人资料",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        Box {
            SubcomposeAsyncImage(
                model = uri,
                loading = {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator()
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .heightIn(min = 140.dp)
                    .clip(MaterialTheme.shapes.small.copy(CornerSize(50)))
                    .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            )
            IconButton(
                onClick = {
                    singleLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(
                        253,
                        191,
                        30
                    ).copy(0.5f)
                ),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = memberUpdateDTO.nickname,
            onValueChange = {
                if (memberUpdateDTO.nickname.length <= 30) {
                    memberUpdateDTO = memberUpdateDTO.copy(nickname = it)
                }
            },
            label = { Text(text = "昵称") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                if (uri == null) {
                    context.showToast("头像不能为空")
                } else {
                    // do submit
                    scope.launch {
                        try {
                            val file = fileFromContentUri(context = context, uri!!)
                            val requestFile =
                                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

                            val part = MultipartBody.Part.createFormData(
                                "avatar",
                                file.name + ".png",
                                requestFile
                            )
                            val nicknameRequestBody =
                                memberUpdateDTO.nickname.toRequestBody("text/plain".toMediaTypeOrNull())

                            val result = appApi.updateProfile(part, nicknameRequestBody)
                            if (result.isSuccessful()) {
                                DataStore.putMember(result.data)
                                println("result.data ${result.data}")
                                context.showToast("更新成功")
                                navController.popBackStack()
                            } else {
                                context.showToast(result.message)
                            }
                        } catch (ex: Exception) {
                            ctx.showToast("网络异常，请重试~")
                        }
                    }
                }
            },
            enabled = memberUpdateDTO.nickname.length <= 30 && memberUpdateDTO.nickname.isNotEmpty()
        ) {
            Text(text = "保存")
        }

    }
}

private fun fileFromContentUri(context: Context, contentUri: Uri): File {

    val fileExtension = getFileExtension(context, contentUri)
    val fileName = "temporary_file" + if (fileExtension != null) ".$fileExtension" else ""

    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()

    try {
        val oStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)

        inputStream?.let {
            copy(inputStream, oStream)
        }

        oStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return tempFile
}

private fun getFileExtension(context: Context, uri: Uri): String? {
    val fileType: String? = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}