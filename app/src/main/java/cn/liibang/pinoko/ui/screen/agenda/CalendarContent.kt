package cn.liibang.pinoko.ui.screen.agenda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.R
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.model.CourseVO
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.component.TaskCard
import cn.liibang.pinoko.ui.screen.course.CourseDeleteConfirmDialog
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.MainRouter
import cn.liibang.pinoko.ui.screen.main.SubRouter
import cn.liibang.pinoko.ui.theme.CategoryColor
import cn.liibang.pinoko.ui.theme.categoryColor
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    tasks: List<TaskVO>,
    updateCompletedStatus: (String, Boolean) -> Unit,
    delete: (String) -> Unit,
    courses: List<CourseVO>,
    deleteCourseByCourseId: (StringUUID) -> Unit,
    selectedDate: LocalDate
) {

    val navController = LocalNavController.current

    if (tasks.isEmpty() && courses.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.task_clean_2),
                contentDescription = "",
                modifier = Modifier.scale(1.5f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "无事了", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
    }

    LazyColumn(
        Modifier.padding(horizontal = 16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        if (courses.isNotEmpty()) {
            item {
                Text(
                    text = "课程",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 2.dp, start = 3.dp)
                )
            }
            val courseMap = courses.associateBy { it.id }
            itemsIndexed(courses.flatMap { it.details }
                .sortedBy { it.lessonStartAt }) { index, courseDetail ->
                val course = courseMap[courseDetail.courseId]!!

                var isShowCourseItemMenu by remember {
                    mutableStateOf(false)
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    shadowElevation = 1.dp,
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    SubRouter.CourseForm.routeWithParam(
                                        course.id
                                    )
                                )
                            }
                            .background(MaterialTheme.colorScheme.surfaceDim)
                            .padding(8.dp),
                        verticalAlignment = CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = courseDetail.lessonStartAt.toString(),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = courseDetail.lessonEndAt.toString(),
                                fontSize = 13.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .height(40.dp)
                                .width(1.6.dp)
                                .background(Color.Gray)
                        )
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                            Row(verticalAlignment = CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.5.dp))
                                Text(text = course.name, fontSize = 15.sp)
                            }
                            Row(verticalAlignment = CenterVertically) {
                                if (courseDetail.teacher.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.5.dp))
                                    Text(
                                        text = courseDetail.teacher,
                                        fontSize = 13.5.sp,
                                        color = Color.Gray,
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                                if (courseDetail.location.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = courseDetail.location,
                                        fontSize = 13.5.sp,
                                        color = Color.Gray,
                                    )
                                }
                                if (courseDetail.location.isEmpty() || courseDetail.teacher.isEmpty()) {
                                    Text(
                                        text = "-",
                                        fontSize = 13.5.sp,
                                        color = Color.Gray,
                                    )
                                }
                            }
                        }

                        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                            IconButton(onClick = { isShowCourseItemMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                DropdownMenu(
                                    expanded = isShowCourseItemMenu,
                                    onDismissRequest = { isShowCourseItemMenu = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "编辑",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        },
                                        onClick = {
                                            navController.navigate(
                                                SubRouter.CourseForm.routeWithParam(
                                                    course.id
                                                )
                                            )
                                        },
                                    )
                                    // 删除逻辑
                                    var isShowDeleteCourseConfirmDialog by remember {
                                        mutableStateOf(false)
                                    }
                                    CourseDeleteConfirmDialog(
                                        isShow = isShowDeleteCourseConfirmDialog,
                                        onDismissRequest = {
                                            isShowDeleteCourseConfirmDialog = false
                                        },
                                        onConfirm = {
                                            deleteCourseByCourseId(course.id)
                                            isShowDeleteCourseConfirmDialog = false

                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "删除",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        },
                                        onClick = { isShowDeleteCourseConfirmDialog = true }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (tasks.isNotEmpty()) {
            item {
                Text(
                    text = "事件",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 2.dp, start = 3.dp)
                )
            }

            val borderWidth = 5.5.dp
            itemsIndexed(tasks) { index, it ->
                val shape = when {
                    tasks.size == 1 -> RoundedCornerShape(10.dp) // 第一个元素的顶部设为圆角
                    index == 0 -> RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp
                    ) // 第一个元素的顶部设为圆角
                    index == tasks.lastIndex -> RoundedCornerShape(
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    ) // 最后一个元素的底部设为圆角
                    else -> RectangleShape // 中间元素的所有角都是直角
                }
                TaskCard(
                    task = it,
                    onCheckedChange = updateCompletedStatus,
                    isShowCategoryColor = true,
                    bottomPadding = 0.dp,
                    shape = shape,
                    shadowElevation = 0.dp,
                    categoryColorBorderWidth = borderWidth,
                    deleteTask = delete,
                    isShowDueDate = false,
                )

                if (index != tasks.lastIndex) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val thickness = 0.75.dp
                        Divider(
                            thickness = thickness,
                            color = if (it.categoryColor == null) Color.Gray else categoryColor(
                                it.categoryColor
                            ),
                            modifier = Modifier.width(borderWidth)
                        )
                        Divider(
                            thickness = thickness,
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }


}



