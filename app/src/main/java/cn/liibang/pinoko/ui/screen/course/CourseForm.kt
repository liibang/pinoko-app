package cn.liibang.pinoko.ui.screen.course

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.ClassTime
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.ui.support.toHexCode
import cn.liibang.pinoko.ui.component.OptButton
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.ui.screen.term.TermViewModel
import cn.liibang.pinoko.ui.theme.CategoryColor
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random


@SuppressLint("UnrememberedMutableState")
@Composable
fun CourseForm(
    id: StringUUID?,
    courseViewModel: CourseViewModel = hiltViewModel(),
    setting: SettingPO,
    termViewModel: TermViewModel
) {

    val navController = LocalNavController.current

    // 先检查是否绑定和 学期， 如果没有给他导航到学期表单界面

    var courseState by remember {
        mutableStateOf(
            CoursePO(
                id = id ?: "",
                termId = setting.termSetId!!,
                name = "",
                color = CategoryColor.values()[Random.nextInt(
                    0,
                    CategoryColor.values().size - 1
                )].color.let(Color::toHexCode),
                createdAt = LocalDateTime.MIN,
                updatedAt = LocalDateTime.MIN
            )
        )
    }

    var term by remember {
        mutableStateOf<TermPO?>(null)
    }


    val courseDetailListState = remember {
        mutableStateListOf(getNewCourseDetailPO())
    }

    val tipColor = Color.Gray.copy(0.5f)

    LaunchedEffect(Unit) {
        // 如果id不为空，那么就判断他是编辑模式，查询数据填充表单数据
        id?.let { courseViewModel.fetchById(id) }?.let {
            // 设置课程状态后
            courseState = it
            // 查询课程详情
            val fetchCourseDetails = courseViewModel.fetchCourseDetails(id)
            if (fetchCourseDetails.isNotEmpty()) {
                courseDetailListState.clear()
                courseDetailListState.addAll(fetchCourseDetails)
            }
        }
        term = termViewModel.fetchById(setting.termSetId!!)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(), verticalAlignment = CenterVertically
        ) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    "close",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = if (id == null) "添加课程" else "编辑课程",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // 如果是编辑操作、给页面显示删除按钮
            if (id != null) {
                var isShowDeleteCourseConfirmDialog by remember {
                    mutableStateOf(false)
                }
                CourseDeleteConfirmDialog(
                    isShow = isShowDeleteCourseConfirmDialog,
                    onDismissRequest = { isShowDeleteCourseConfirmDialog = false },
                    onConfirm = {
                        courseViewModel.deleteCourseByCourseId(courseState.id)
                        navController.popBackStack()
                    }
                )
                TextButton(onClick = { isShowDeleteCourseConfirmDialog = true }) {
                    Text(text = "删除", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))
            OptButton(
                //
                enabled = courseState.name.isNotEmpty() &&
                        courseDetailListState.count { it.weeks.isEmpty() || it.lessonStartAt == 0 || it.lessonEndAt == 0 } == 0,
                onClick = {

                    courseViewModel.saveOrUpdate(
                        courseState,
                        courseDetailListState
                    )
                    navController.popBackStack()
                }
            )
        }
        // Header end...

        var isShowColorPicker by remember {
            mutableStateOf(false)
        }
        // 颜色
        ColorPickerDialog(
            isShow = isShowColorPicker,
            onDismissRequest = { isShowColorPicker = false },
            onConfirm = { newColor: Color, hexCode: String ->
                courseState = courseState.copy(color = hexCode)
            },
            initColorHexCode = courseState.color
        )
        XTextField(
            value = courseState.name,
            onValueChange = {
                courseState = courseState.copy(name = it)
            },
            placeholder = {
                Text(
                    text = "课程名称",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.5.sp,
                    color = MaterialTheme.colorScheme.outline.copy(0.66f),
                )
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 15.5.sp,
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray.copy(0.1f),
                focusedContainerColor = Color.Gray.copy(0.1f),
                cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier
                .background(Color.Unspecified)
                .fillMaxWidth()
                .padding(start = 5.dp, top = 15.dp),
            leadingIcon = {
                IconButton(onClick = { isShowColorPicker = true }) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "",
                        modifier = Modifier.scale(1.0f),
                        tint = hexToColor(courseState.color)
                    )
                }
            },
            paddingValues = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
        )

        LazyColumn(Modifier.padding(top = 15.dp)) {
            itemsIndexed(courseDetailListState) { index, item ->

                if (courseDetailListState.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp, vertical = 5.dp),
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            text = "上课时间" + (index + 1),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                courseDetailListState.removeAt(index)
                            },
                            modifier = Modifier.size(17.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceDim),
                    elevation = CardDefaults.cardElevation(0.5.dp)
                ) {
                    // ==========周数选择======================
                    var isShowWeekNumberSelector by remember { mutableStateOf(false) }
                    WeekNumberSelector(
                        weekMaxCount = term?.weekCount ?: 0,
                        isShow = isShowWeekNumberSelector,
                        onDismissRequest = { isShowWeekNumberSelector = false },
                        onConfirm = { selectedWeekNumbers ->
                            courseDetailListState[index] =
                                item.copy(weeks = selectedWeekNumbers)
                        },
                        initValue = if (item.weeks == "") listOf() else item.weeks.split(",")
                            .map { it.toInt() }
                    )
                    // end....
                    Row(
                        Modifier
                            .clickable { isShowWeekNumberSelector = true }
                            .padding(12.5.dp), verticalAlignment = CenterVertically) {
                        Text(text = "周数")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = formatWeekNumbers(item.weeks).ifEmpty { "必填" },
                            color = if (item.weeks.isEmpty()) tipColor else Color.Black,
                            modifier = Modifier.padding(start = 30.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.weeks.isEmpty()) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "",
                                tint = tipColor
                            )
                        }
                    }
                    // ====================================================
                    var isShowWeekOfDaySelector by remember { mutableStateOf(false) }
                    WeekOfDaySelector(
                        isShow = isShowWeekOfDaySelector,
                        onDismissRequest = { isShowWeekOfDaySelector = false },
                        onSelect = {
                            courseDetailListState[index] =
                                courseDetailListState[index].copy(dayOfWeek = it)
                        },
                        initValue = item.dayOfWeek,
                        title = "设置星期"
                    )
                    Row(
                        Modifier
                            .clickable { isShowWeekOfDaySelector = true }
                            .padding(12.5.dp), verticalAlignment = CenterVertically) {
                        Text(text = "星期")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (courseDetailListState[index].dayOfWeek == 0) "必填" else WEEK_DAY_INFO_LIST[item.dayOfWeek]!!,
                            color = if (courseDetailListState[index].dayOfWeek == 0) tipColor else Color.Black
                        )
                        if (courseDetailListState[index].dayOfWeek == 0) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "",
                                tint = tipColor
                            )
                        }
                    }
                    // ==== 上课时间选择

                    var isShowLessonTimeRangePicker by remember { mutableStateOf(false) }
                    LessonTimeRangePicker(
                        isShow = isShowLessonTimeRangePicker,
                        onDismissRequest = { isShowLessonTimeRangePicker = false },
                        onSelect = {
                            courseDetailListState[index] = courseDetailListState[index].copy(
                                lessonStartAt = it.startAt.toSecondOfDay(),
                                lessonEndAt = it.endAt.toSecondOfDay(),
                            )
                        },
                        onClear = {},
                        initValue = if (item.lessonStartAt == 0 || item.lessonEndAt == 0) ClassTime(
                            LocalTime.of(8, 30),
                            LocalTime.of(9, 10)
                        ) else ClassTime(
                            LocalTime.ofSecondOfDay(item.lessonStartAt.toLong()),
                            LocalTime.ofSecondOfDay(item.lessonEndAt.toLong())
                        ),
                        title = "上下课时间"
                    )
                    Row(
                        Modifier
                            .clickable { isShowLessonTimeRangePicker = true }
                            .padding(12.5.dp), verticalAlignment = CenterVertically) {
                        Text(text = "时间")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (item.lessonStartAt == 0 || item.lessonEndAt == 0) "必填"
                            else "${LocalTime.ofSecondOfDay(item.lessonStartAt.toLong())} - ${
                                LocalTime.ofSecondOfDay(
                                    item.lessonEndAt.toLong()
                                )
                            }",
                            color = if (item.lessonStartAt == 0 || item.lessonEndAt == 0) tipColor else Color.Black
                        )
                        if (item.lessonStartAt == 0 || item.lessonEndAt == 0) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "",
                                tint = tipColor
                            )
                        }
                    }
                    //=======================================
                    val locationFocusRequester = remember { FocusRequester() }
                    Row(
                        Modifier
                            .clickable { locationFocusRequester.requestFocus() }
                            .padding(12.5.dp), verticalAlignment = CenterVertically) {
                        Text(text = "教室")
                        Spacer(modifier = Modifier.weight(1f))
                        XTextField(
                            value = courseDetailListState[index].location,
                            onValueChange = {
                                courseDetailListState[index] =
                                    courseDetailListState[index].copy(location = it)
                            },
                            placeholder = {
                                Text(
                                    text = "选填",
                                    color = tipColor,
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .background(Color.Unspecified)
                                .width(IntrinsicSize.Max)
                                .focusRequester(locationFocusRequester),
                            paddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        )
                    }
                    // ============================================
                    val teacherFocusRequester = remember { FocusRequester() }
                    Row(
                        Modifier
                            .clickable { teacherFocusRequester.requestFocus() }
                            .padding(12.5.dp), verticalAlignment = CenterVertically) {
                        Text(text = "老师")
                        Spacer(modifier = Modifier.weight(1f))
                        XTextField(
                            value = courseDetailListState[index].teacher,
                            onValueChange = {
                                courseDetailListState[index] =
                                    courseDetailListState[index].copy(teacher = it)
                            },
                            placeholder = {
                                Text(
                                    text = "选填",
                                    color = tipColor,
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .background(Color.Unspecified)
                                .width(IntrinsicSize.Max)
                                .focusRequester(teacherFocusRequester),
                            paddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        )
                    }
                }

                // 在末尾处加入  添加[其他时间按钮]
                if (courseDetailListState.lastIndex == index && courseDetailListState.size < 10) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp),
                        horizontalArrangement = Center
                    ) {
                        TextButton(onClick = {
                            courseDetailListState.add(
                                getNewCourseDetailPO().copy(
                                    teacher = item.teacher,
                                    location = item.location
                                )
                            )
                        }) {
                            Text(text = "+ 添加其他时间")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(if (courseDetailListState.size >= 10) 10.dp else 5.dp))
            }
        }
    }

}


private fun getNewCourseDetailPO(): CourseDetailPO {
    return CourseDetailPO(
        id = "",
        courseId = "",
        weeks = "",
        location = "",
        teacher = "",
        lessonStartAt = 0,
        lessonEndAt = 0,
        createdAt = LocalDateTime.MIN,
        updatedAt = LocalDateTime.MIN,
        dayOfWeek = 0
    )
}


private fun formatWeekNumbers(input: String): String {
    if (input.isEmpty()) return ""
    val numbers = input.split(",").map { it.toInt() }.sorted()
    val groups = mutableListOf<List<Int>>()
    var group = mutableListOf<Int>()
    for (n in numbers) {
        if (group.isEmpty() || n == group.last() + 1) {
            group.add(n)
        } else {
            groups.add(group)
            group = mutableListOf(n)
        }
    }
    if (group.isNotEmpty()) groups.add(group)
    return groups.joinToString(", ") {
        if (it.size == 1) "${it.first()}"
        else "${it.first()}-${it.last()}"
    }
}
