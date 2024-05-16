package cn.liibang.pinoko.ui.screen.habit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.R
import cn.liibang.pinoko.data.entity.BasePO
import cn.liibang.pinoko.ui.screen.agenda.calendar.displayText
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import cn.liibang.pinoko.ui.screen.term.TermDeleteConfirmDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField


@Composable
fun HabitScreen(viewModel: HabitViewModel = hiltViewModel()) {

    val selectedDate by viewModel._selectedDate.collectAsState()
    val habits by viewModel.habits.collectAsState()

    val navController = LocalNavController.current

    Column(Modifier) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            Text(text = "我的习惯", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        WeekCalendarHeader(selectedDate, viewModel::changeSelectedDay)

        if (habits.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.habit),
                    contentDescription = "",
                    modifier = Modifier.scale(1.25f)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "暂无信息", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            }
        } else {
            LazyColumn {
                items(habits) {
                    Spacer(modifier = Modifier.height(3.5.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.padding(bottom = 14.dp, start = 10.dp, end = 10.dp),
                        onClick = { navController.navigate(SubRouter.HabitForm.routeWithParam(it.id)) }
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(Color.Unspecified)
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (it.remindTime != null) {
                                Text(
                                    text = it.remindTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 12.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(13.dp)
                                )
                            } else {
                                Text(
                                    text = "设定提醒",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.clickable { navController.navigate(SubRouter.HabitForm.routeWithParam(it.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}


@Composable
private fun WeekCalendarHeader(selectedDate: LocalDate, changeSelectedDate: (LocalDate) -> Unit) {
    // 获取本周数据
    val now = LocalDate.now()
    Row(Modifier.fillMaxWidth()) {
        getWeekDates(now).forEach {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it.dayOfWeek.displayText().replace("周", ""),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (it == selectedDate) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                        .clickable { changeSelectedDate(it) }
                ) {
                    Text(
                        text = if (it == now) "今" else it.dayOfMonth.toString(),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

    }
}

fun getWeekDates(givenDate: LocalDate = LocalDate.now()): List<LocalDate> {
    // 获取周一的日期
    val monday = givenDate.minusDays(givenDate.get(ChronoField.DAY_OF_WEEK).toLong() - 1)

    // 生成并返回从周一到周日的日期列表
    return (0..6).map { day ->
        monday.plusDays(day.toLong())
    }
}