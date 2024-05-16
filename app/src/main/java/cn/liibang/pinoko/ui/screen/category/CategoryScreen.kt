package cn.liibang.pinoko.ui.screen.category

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.model.TaskCategoryRecordVO
import cn.liibang.pinoko.model.TaskCategoryVO
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.support.showToast
import cn.liibang.pinoko.ui.theme.categoryColor
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun CategoryScreen(categoryViewModel: CategoryViewModel = hiltViewModel()) {
    val categories by categoryViewModel.taskCategories.collectAsState()

    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp))
            .padding(10.dp)
    ) {

        // header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = "管理分类", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))

            var isShowCategoryAddForm by remember {
                mutableStateOf(false)
            }
            CategoryForm(
                show = isShowCategoryAddForm,
                onDismissRequest = { isShowCategoryAddForm = false },
                onConfirm = {}
            )
            TextButton(onClick = { isShowCategoryAddForm = true }) {
                Text(text = "新建")
            }
        }


        Text(
            text = "显示在事件清单上的分类",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .background(MaterialTheme.colorScheme.primary.copy(0.2f))
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        // 可滑动列表
        VerticalReorderList(
            categories,
            categoryViewModel::updateCategorySort,
            categoryViewModel::delete
        )

        Text(
            text = "长按并拖动以重新排序", color = Color.Gray, fontSize = 13.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            textAlign = TextAlign.Center
        )

    }
}


@Composable
fun VerticalReorderList(
    categories: List<TaskCategoryVO>,
    doUpdateSort: (List<Pair<String, Int>>) -> Unit,
    doDelete: (StringUUID) -> Unit
) {

    val grayColor = MaterialTheme.colorScheme.outline
    val data = remember { mutableStateOf(categories) }

    LaunchedEffect(categories) {
        data.value = categories
    }

    val context = LocalContext.current

    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            data.value = data.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        },
        onDragEnd = { from, to ->
            if (from == to) return@rememberReorderableLazyListState
            val idAndSortList = if (from > to) {
                // 向上滑动
                (to..from).map { index ->
                    val newSort =
                        if (index == to) data.value[index + 1].sort else data.value[index].sort + 1
                    Pair(data.value[index].id, newSort)
                }
            } else {
                // 向下滑动
                // 向下滑动
                (from..to).map { index ->
                    val newSort =
                        if (index == to) data.value[index - 1].sort else data.value[index].sort - 1
                    Pair(data.value[index].id, newSort)
                }
            }
            doUpdateSort(idAndSortList)
        },

        )
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(data.value, key = { it.id }) { item ->
            ReorderableItem(state, key = item.id) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 2.dp else 0.dp)
                Row(
                    modifier = Modifier
                        .shadow(elevation.value)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 10.dp, horizontal = 5.dp),
//                        .background(MaterialTheme.colorScheme.surfaceDim)
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Filled.FiberManualRecord,
                            contentDescription = "",
                            tint = categoryColor(item.color),
                            modifier = Modifier
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(item.name, color = grayColor)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = item.taskCount.toString(), color = grayColor, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    var isShowMenu by remember {
                        mutableStateOf(false)
                    }
                    IconButton(onClick = { isShowMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "",
                            tint = grayColor
                        )
                        DropdownMenu(
                            expanded = isShowMenu,
                            onDismissRequest = { isShowMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim),
                        ) {
                            // 编辑

                            var isShowCategoryForm by remember {
                                mutableStateOf(false)
                            }
                            CategoryForm(
                                show = isShowCategoryForm,
                                onDismissRequest = { isShowCategoryForm = false },
                                onConfirm = { isShowMenu = false },
                                id = item.id
                            )

                            DropdownMenuItem(
                                text = { Text(text = "编辑", fontWeight = FontWeight.SemiBold) },
                                onClick = { isShowCategoryForm = true },
                            )

                            // 删除
                            var isShowCategoryDeleteConfirmDialog by remember {
                                mutableStateOf(false)
                            }
                            CategoryDeleteConfirmDialog(
                                isShow = isShowCategoryDeleteConfirmDialog,
                                onDismissRequest = {
                                    isShowCategoryDeleteConfirmDialog = false
                                    isShowMenu = false
                                },
                                onConfirm = {
                                    doDelete(item.id)
                                    context.showToast("删除成功", Toast.LENGTH_SHORT)
                                    isShowCategoryDeleteConfirmDialog = false
                                    isShowMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = "删除", fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    isShowCategoryDeleteConfirmDialog = true
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
