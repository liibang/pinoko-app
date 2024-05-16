package cn.liibang.pinoko.ui.screen.main

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.liibang.pinoko.SignActivity
import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.ui.screen.feedback.FeedbackScreen
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalMenu(
    modalBottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    navigateToNewPage: (MainRouter) -> Unit,
    currentRoute: String,
    syncViewModel: SyncViewModel,
) {


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        containerColor = MaterialTheme.colorScheme.surfaceDim,
    ) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            // 固定项
            InteractiveItem(onDismissRequest, syncViewModel)
            Spacer(modifier = Modifier.height(5.dp))
            // 路由项
            ROUTERS_FOR_MENUS.forEachIndexed { index, it ->


                if (index == 4) {

                    // dialog
                    var isShowFeedbackScreen by remember {
                        mutableStateOf(false)
                    }

                    FeedbackScreen(
                        isShow = isShowFeedbackScreen,
                        onDismissRequest = { isShowFeedbackScreen = false })
                    ListItem(
                        colors = ListItemDefaults.colors(MaterialTheme.colorScheme.surfaceDim),
                        modifier = Modifier
                            .clip(
                                MaterialTheme.shapes.small.copy(
                                    CornerSize(20)
                                )
                            )
                            .clickable(true, onClick = {
                                isShowFeedbackScreen = true
                            }),
                        headlineContent = {
                            Text(
                                text = "反馈建议",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Feedback,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                    )

                }



                val isSelected = currentRoute == it.route
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceDim),
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.small.copy(
                                CornerSize(20)
                            )
                        )
                        .clickable(true, onClick = {
                            navigateToNewPage(it)
                        }),
                    headlineContent = {
                        Text(
                            text = it.metadata.name,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = it.metadata.icon!!,
                            contentDescription = "",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    },
                )
            }


            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InteractiveItem(onDismissRequest: () -> Unit, syncViewModel: SyncViewModel) {

    val ctx = LocalContext.current

    var isShowMenu by remember {
        mutableStateOf(false)
    }

    val memberInfo = DataStore.getMemberInfo()

    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape.copy(CornerSize(10.dp)))
            .clickable(
                enabled = true,
                onClick = { isShowMenu = true }, onClickLabel = null, role = null,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colorScheme.outline),
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .clip(CircleShape.copy(CornerSize(10.dp)))
                .weight(1f)
                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
        ) {
            SubcomposeAsyncImage(
                model = memberInfo.avatar,
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
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small.copy(CornerSize(50)))
                    .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            )
            DropdownMenu(
                expanded = isShowMenu,
                onDismissRequest = { isShowMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
            ) {
                DropdownMenuItem(
                    text = { Text("个人资料", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        navController.navigate(SubRouter.Profile.route)
                        onDismissRequest()
                    },
                )
                DropdownMenuItem(
                    text = { Text("修改密码", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        navController.navigate(SubRouter.ChangePasswordScreen.route)
                        onDismissRequest()
                    },
                )

                var isShowLogoutConfirmDialog by remember {
                    mutableStateOf(false)
                }
                LogoutConfirmDialog(
                    isShow = isShowLogoutConfirmDialog,
                    onDismissRequest = {
                        isShowLogoutConfirmDialog = false
//                        isShowMenu = false
                    },
                    onConfirm = {

                        // 先同步数据
                        onDismissRequest()
                        DataStore.clearToken()
                        ctx.startActivity(Intent(ctx, SignActivity::class.java))
                        (ctx as Activity).finish()
                    },
                    syncViewModel = syncViewModel
                )
                DropdownMenuItem(
                    text = { Text("注销", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        isShowLogoutConfirmDialog = true
                    }
                )
            }
            Spacer(modifier = Modifier.width(11.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 3.dp)
            ) {
                // title
                Text(
                    text = memberInfo.nickname,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // subtitle
                Text(
                    "已坚持计划${
                        ChronoUnit.DAYS.between(
                            memberInfo.created.toLocalDate(),
                            LocalDate.now()
                        ) + 1
                    }天", color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

