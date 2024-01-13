package cn.liibang.pinoko.ui.screen.agenda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.liibang.pinoko.R
import cn.liibang.pinoko.ui.component.TaskCard
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.ui.theme.categoryColor

@Composable
fun AgendaScreen(
    agendaViewModel: AgendaViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {


    val tasks by agendaViewModel.tasks.collectAsState()
    val selectedDay by agendaViewModel.selectedDay.collectAsState()
    val inboxSize by agendaViewModel.inboxSize.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        XWeekCalendar(
            selectedDay,
            agendaViewModel::changeSelectedDay,
            agendaViewModel::isEventOnDayOfWeek,
            inboxSize
        )
        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.task_clear),
                    contentDescription = "",
                    modifier = Modifier.scale(1.1f)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = "无事了", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            }
        } else {
            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "事件",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 2.dp, start = 3.dp)
                )
                Surface(
                    shadowElevation = 1.dp,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Column {
                        val borderWidth = 5.5.dp
                        tasks.forEachIndexed { index, it ->
                            TaskCard(
                                task = it,
                                onCheckedChange = taskViewModel::updateCompletedStatus,
                                isShowCategoryColor = true,
                                bottomPadding = 0.dp,
                                shapeSize = 0.dp,
                                shadowElevation = 0.dp,
                                categoryColorBorderWidth = borderWidth,
                                deleteTask = taskViewModel::delete
                            )
                            if (index != tasks.lastIndex) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    val thickness = 0.75.dp
                                    Divider(
                                        thickness = thickness,
                                        color = categoryColor(it.categoryColor),
                                        modifier = Modifier.width(borderWidth)
                                    )
                                    Divider(
                                        thickness = thickness,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
