package cn.liibang.pinoko.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.support.generateUUID
import cn.liibang.pinoko.ui.theme.CategoryColor
import cn.liibang.pinoko.ui.theme.XShape
import java.time.LocalDateTime

private const val MAX_TEXT_LENGTH = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAddForm(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onCreate: (String) -> Unit,
) {
    val viewModel = hiltViewModel<CategoryViewModel>()

    if (show) {
        var categoryName by remember {
            mutableStateOf("")
        }

        var selectedColor: CategoryColor? by remember {
            mutableStateOf(null)
        }

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                Modifier
                    .width(IntrinsicSize.Min)
                    .padding(horizontal = 20.dp)
                    .clip(XShape.Card)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(16.dp)
            ) {
                Text(text = "新建分类", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                // =============分类名称输入框=================
                Box {
                    XTextField(
                        value = categoryName,
                        onValueChange = {
                            categoryName =
                                if (it.length <= MAX_TEXT_LENGTH) it else it.substring(
                                    0,
                                    MAX_TEXT_LENGTH
                                )
                        },
                        placeholder = {
                            Text(
                                text = "在这里输入。",
                                color = MaterialTheme.colorScheme.outline
                            )
                        },
                        minLines = 3,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.outline
                                .copy(0.1f),
                            focusedContainerColor = MaterialTheme.colorScheme.outline
                                .copy(0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = XShape.Card,
                        paddingValues = PaddingValues(8.dp),
                    )
                    Text(
                        text = "${categoryName.length}/$MAX_TEXT_LENGTH",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .align(
                                Alignment.BottomEnd
                            )
                            .padding(end = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ========================颜色
                Column {
                    Row {
                        Text(text = "分类颜色", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        if (selectedColor == null) {
                            Text(text = "默认", fontSize = 12.sp)
                        }
                        Icon(
                            imageVector = Icons.Filled.FiberManualRecord,
                            contentDescription = "",
                            tint = selectedColor?.color ?: CategoryColor.DEFAULT_BLUE.color,
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(11.dp))
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        Row {
                            CategoryColor.values().forEach {
                                IconButton(
                                    onClick = { selectedColor = it },
                                    modifier = Modifier.background(Color.Transparent),
                                ) {
                                    Box {
                                        Icon(
                                            imageVector = Icons.Filled.FiberManualRecord,
                                            contentDescription = "",
                                            tint = it.color,
                                            modifier = Modifier
                                                .scale(2f)
                                        )
                                        if (it.code == selectedColor?.code || (selectedColor == null && it.code == CategoryColor.DEFAULT_BLUE.code)) {
                                            Icon(
                                                imageVector = Icons.Outlined.Lens,
                                                contentDescription = "",
                                                modifier = Modifier
                                                    .align(
                                                        Alignment.Center
                                                    )
                                                    .scale(1.4f),
                                                tint = Color.White
                                            )
                                        }

                                    }
                                }
                            }
                            // 清除按钮
                            IconButton(onClick = { selectedColor = null }) {
                                Icon(
                                    imageVector = Icons.Filled.CleaningServices,
                                    contentDescription = null,
                                    Modifier.rotate(45f),
                                    tint = MaterialTheme.colorScheme.primary.copy(0.5f)
                                )
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                // ==================按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(
                        onClick = {
                            // TODO 优化
                            val now = LocalDateTime.now()
                            val categoryPO = TaskCategoryPO(
                                id = generateUUID(),
                                name = categoryName,
                                color = selectedColor?.code ?: CategoryColor.DEFAULT_BLUE.code,
                                sort = 0,
                                createAt = now,
                                updatedAt = now
                            )
                            viewModel.save(categoryPO)
                            onDismissRequest()
                            onCreate(categoryPO.id)
                        }, enabled = categoryName.isNotEmpty()
                    ) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }

}