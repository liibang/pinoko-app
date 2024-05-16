package cn.liibang.pinoko.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import cn.liibang.pinoko.ui.support.formatToHM
import com.bang.composable_graphs.composables.bar.MyBarGraph
import com.bang.composable_graphs.composables.bar.model.BarData
import com.jaikeerthick.composable_graphs.composables.pie.model.PieData
import com.kizitonwose.calendar.core.daysOfWeek
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.Duration
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun StatsForFocusScreen(statsViewModel: StatsViewModel) {

    val tomatoFocusAchievement by statsViewModel.tomatoFocusAchievement.collectAsState()
    val totalTomatoCount by statsViewModel.totalTomatoCount.collectAsState()
    val focusDurationWeekStatVO by statsViewModel.focusDurationWeekStatVO.collectAsState()

    Column {
        Row(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .height(110.dp)
                    .background(Color(234, 117, 22))
                    .padding(10.dp)
            ) {
                Text(text = "番茄收成", color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = tomatoFocusAchievement.tomatoCount.toString(), fontSize = 30.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(7.dp))
                    val tomatoCount = tomatoFocusAchievement.tomatoCount - tomatoFocusAchievement.previousTomatoCount
                    val tomatoCountTip = if (tomatoCount >= 0) "多${tomatoCount}" else "少${abs(tomatoCount)}"
                    Text(text = "比前一周期\n${tomatoCountTip}个", fontSize = 12.sp, lineHeight = 15.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            val focusDuration = tomatoFocusAchievement.focusDuration - tomatoFocusAchievement.previousFocusDuration
            val focusDurationTip = if (focusDuration >= Duration.ZERO) "多${focusDuration.formatToHM()}" else "少${focusDuration.formatToHM()}"
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .height(110.dp)
                    .background(Color(215, 71, 62))
                    .padding(10.dp)
            ) {
                Text(text = "专注时长", color = Color.White)
                Text(text = "比前一周期${focusDurationTip}", fontSize = 11.sp, lineHeight = 15.sp, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = tomatoFocusAchievement.focusDuration.formatToHM(), fontSize = 30.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(7.dp))

                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        //  图表
        val pieChartData = listOf(
            PieData(value = 130F, label = "已完成", color = Color.Green),
            PieData(value = 260F, label = "未完成", labelColor = Color.Blue),
        )

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(Modifier.align(Alignment.Start)) {
                    Text(text = "最佳番茄专注日")
                    Text(text = "展示这段时间里，您在周一至周日的专注时长分布 (单位: 小时)", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                val workloadOnWeekMap = focusDurationWeekStatVO.associateBy { it.weekValue }
                MyBarGraph(
                    data = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
                        .map {
                            workloadOnWeekMap[it.value]?.run {
                                BarData(x = it.displayText(), y = focusDuration.toMinutes())
                            } ?: BarData(x = it.displayText(), 0)
                        }
                    , formatData = { convertMinutesToHoursString(it.toLong()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(234, 117, 22))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = totalTomatoCount.toString(), fontSize = 30.sp , color = Color.White)
            Text(text = "历史累计获得的番茄总数", color = Color.White)
        }
    }
}


fun convertMinutesToHoursString(minutes: Long): String {
    // 转换为小时的浮点数
    val hoursAsDouble = minutes / 60.0

    // 转换为BigDecimal
    val hoursAsBigDecimal = BigDecimal(minutes).divide(BigDecimal(60), 2, RoundingMode.HALF_UP)

    // 转换为字符串表示
    val hoursAsString = hoursAsDouble.toString()

    // 返回结果
    return hoursAsBigDecimal.toString()
}