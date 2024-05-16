package cn.liibang.pinoko.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.ui.screen.agenda.calendar.displayText
import com.bang.composable_graphs.composables.bar.MyBarGraph
import com.jaikeerthick.composable_graphs.composables.bar.BarGraph
import com.bang.composable_graphs.composables.bar.model.BarData
import com.jaikeerthick.composable_graphs.composables.pie.PieChart
import com.jaikeerthick.composable_graphs.composables.pie.model.PieData
import com.jaikeerthick.composable_graphs.composables.pie.style.PieChartStyle
import com.jaikeerthick.composable_graphs.composables.pie.style.PieChartVisibility
import com.kizitonwose.calendar.core.daysOfWeek
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import kotlin.math.abs

@Composable
fun StatsForTaskScreen(statsViewModel: StatsViewModel) {
    val taskAchievement by statsViewModel.taskAchievement.collectAsState()

    val completedTaskCount by statsViewModel.completedTaskCount.collectAsState()
    val workloadOnWeek by statsViewModel.workloadOnWeek.collectAsState()


    Column {
        Row(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .height(110.dp)
                    .background(Color(251, 71, 119))
                    .padding(10.dp)
            ) {
                Text(text = "事件达成数", color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = taskAchievement.completedCount.toString(),
                        fontSize = 30.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    val completedCount =
                        taskAchievement.completedCount - taskAchievement.previousCompletedCount
                    Text(
                        text = if (completedCount >= 0) {
                            "比前一周期多${completedCount}个"
                        } else {
                            "比前一周期少${abs(completedCount)}个"
                        },
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(0.8f))
                    .padding(10.dp)
            ) {
                Text(text = "事件工作量", color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = taskAchievement.taskCount.toString(),
                        fontSize = 30.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    val taskCount =
                        taskAchievement.taskCount - taskAchievement.previousTaskCount
                    Text(
                        text = if (taskCount >= 0) {
                            "比前一周期多${taskCount}个"
                        } else {
                            "比前一周期少${abs(taskCount)}个"
                        },
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(Modifier.align(Alignment.Start)) {
                    Text(text = "事件完成率")
                    val consumablePercentage =
                        if (taskAchievement.taskCount != 0) {
                            BigDecimal((taskAchievement.completedCount.toFloat() / taskAchievement.taskCount.toFloat() * 100F).toDouble())
                                .setScale(2, RoundingMode.HALF_EVEN)
                                .toFloat().toString() + "%"
                        } else "100%"

                    val taskCompletionGrowRate = BigDecimal(taskAchievement.getCompletionGrowthRate().toString()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
                    Text(
                        text = "该周期您的事件完成率为$consumablePercentage, ${
                            if (taskCompletionGrowRate >= 0) {
                                "比前一周期增长${"$taskCompletionGrowRate%"}"
                            } else {
                                "比前一周期减少${abs(taskCompletionGrowRate).toString() + "%"}"
                            }
                        }",
                        fontSize = 11.sp
                    )
                }

                //  图表
                val pieChartData = listOf(
                    PieData(
                        value = if (taskAchievement.completedCount == 0) 1f else taskAchievement.completedCount.toFloat(),
                        label = "已完成",
                        color = MaterialTheme.colorScheme.primary.copy(0.8f),
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    PieData(
                        value = (taskAchievement.taskCount - taskAchievement.completedCount).toFloat(),
                        label = "未完成",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                )
                PieChart(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .size(220.dp),
                    data = pieChartData,
                    style = PieChartStyle(
                        visibility = PieChartVisibility(
                            isLabelVisible = true,
                            isPercentageVisible = true
                        )
                    ),
                    onSliceClick = { pieData -> }
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(Modifier.align(Alignment.Start)) {
                    Text(text = "周几最勤奋")
                    Text(text = "展示这段时间里，您在周一至周日的工作量分布", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(15.dp))
                val workloadOnWeekMap = workloadOnWeek.associateBy { it.weekValue }
                MyBarGraph(
                    data = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
                        .map {
                            workloadOnWeekMap[it.value]?.run {
                                BarData(x = it.displayText(), y = taskCount)
                            } ?: BarData(x = it.displayText(), 0)
                        },
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(251, 71, 119))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = completedTaskCount.toString(), fontSize = 30.sp, color = Color.White)
            Text(text = "历史累计达成的总事件数", color = Color.White)
        }
    }
}