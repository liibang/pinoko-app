package cn.liibang.pinoko.ui.screen.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.R
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.model.TaskCategoryVO
import cn.liibang.pinoko.ui.component.TaskCard
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.category.CategoryForm
import cn.liibang.pinoko.ui.screen.category.CategoryViewModel
import cn.liibang.pinoko.ui.screen.category.DEFAULT_CATEGORY_ALL
import cn.liibang.pinoko.ui.screen.category.DEFAULT_CATEGORY_NONE
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import cn.liibang.pinoko.ui.screen.main.TaskDisplayMode
import cn.liibang.pinoko.ui.screen.setting.SettingViewModel


@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    displayMode: TaskDisplayMode,
    settingViewModel: SettingViewModel
) {
    val setting by settingViewModel.setting.collectAsState()
    val taskCategories by categoryViewModel.taskCategories.collectAsState()
    val selectedCategoryID by taskViewModel.selectedCategoryID.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val searchValue by taskViewModel.searchValue.collectAsState()

    Column {
        ToolHeader(
            searchValue,
            taskViewModel::updateSearchValue,
            displayMode,
            setting,
            settingViewModel::saveOrUpdate
        )
        CategoryChips(
            taskCategories,
            selectedCategoryID,
            taskViewModel::changeSelectedCategory
        )

        // 显示清单模块
        AnimatedVisibility(
            visible = displayMode == TaskDisplayMode.LIST,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            if (displayMode == TaskDisplayMode.LIST) {
                if (tasks.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_task),
                            contentDescription = "",
                            modifier = Modifier.scale(1.75f)
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "无事了", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    }
                } else {
                    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                        item { Spacer(modifier = Modifier.height(10.dp)) }
                        itemsIndexed(tasks) { _, task ->
                            TaskCard(
                                task,
                                taskViewModel::updateCompletedStatus,
                                deleteTask = taskViewModel::delete,
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = displayMode != TaskDisplayMode.LIST,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            if (displayMode != TaskDisplayMode.LIST) {
                // 显示四象限模块
                QuadrantList(
                    tasks,
                    taskViewModel::updateTaskPriority,
                    taskViewModel::updateCompletedStatus
                )
            }
        }
    }
}

@Composable
private fun CategoryChips(
    categories: List<TaskCategoryVO>,
    selectedCategoryID: String,
    changeSelectedCategory: (String) -> Unit
) {

    var isShowCategoryAddForm by remember {
        mutableStateOf(false)
    }
    CategoryForm(
        show = isShowCategoryAddForm,
        onDismissRequest = { isShowCategoryAddForm = false },
        onConfirm = {}
    )

    Row {
        LazyRow(
            Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            itemsIndexed(
                categories.toMutableList().apply {  add(0, DEFAULT_CATEGORY_NONE); add(0, DEFAULT_CATEGORY_ALL); }
            ) { _, it ->
                FilterChip(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = { changeSelectedCategory(it.id) },
                    label = { Text(it.name) },
                    selected = selectedCategoryID == it.id,
                    colors = FilterChipDefaults.filterChipColors(
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = false, selected = true)
                )
            }
            item {
                FilterChip(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = { isShowCategoryAddForm = true },
                    label = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    selected = false,
                    colors = FilterChipDefaults.filterChipColors(
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = false, selected = true)
                )
            }
        }

    }
}


@Composable
private fun ToolHeader(
    searchValue: String,
    updateSearchValue: (String) -> Unit,
    displayMode: TaskDisplayMode,
    setting: SettingPO,
    updateSetting: (SettingPO) -> Unit
) {

    val focusRequester = remember { FocusRequester() }

    var isShowMenu by remember {
        mutableStateOf(false)
    }

    var isShowSearchBar by remember {
        mutableStateOf(false)
    }

    val navController = LocalNavController.current

    Row(Modifier.padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {

        if (isShowSearchBar) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            IconButton(onClick = {
                updateSearchValue("")
                isShowSearchBar = false
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            XTextField(
                value = searchValue,
                onValueChange = updateSearchValue,
                placeholder = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
        } else {
            Text(
                text = if (displayMode == TaskDisplayMode.LIST) "事件列表" else "四象限",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 13.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        if (!isShowSearchBar) {
            IconButton(onClick = { isShowSearchBar = true; }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
        IconButton(onClick = { isShowMenu = true }) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                "",
                tint = MaterialTheme.colorScheme.outline
            )
            DropdownMenu(
                expanded = isShowMenu,
                onDismissRequest = { isShowMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
            ) {
                DropdownMenuItem(
                    text = { Text("管理分类", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        navController.navigate(SubRouter.CategoryScreen.route)
                        isShowMenu = false
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            if (setting.taskShowCompleted) "隐藏已完成" else "展示已完成",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    onClick = { updateSetting(setting.copy(taskShowCompleted = !setting.taskShowCompleted)) },
                )

                // 设置任务排序、包含弹窗
                var isShowTaskSortSettingDialog by remember {
                    mutableStateOf(false)
                }
                TaskSortSettingDialog(
                    isShow = isShowTaskSortSettingDialog,
                    onDismissRequest = {
                        isShowTaskSortSettingDialog = false
                        isShowMenu = false
                    },
                    updateSetting = {
                        updateSetting(it)
                        isShowMenu = false
                    },
                    setting = setting
                )
                DropdownMenuItem(
                    text = { Text("排序方式", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        isShowTaskSortSettingDialog = true
                    }
                )
            }
        }
        Spacer(modifier = Modifier.width(2.dp))
    }
}







