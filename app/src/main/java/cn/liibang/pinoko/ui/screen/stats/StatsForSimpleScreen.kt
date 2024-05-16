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
import cn.liibang.pinoko.ui.support.formatToHM
import java.time.Duration
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun StatsForSimpleScreen(statsViewModel: StatsViewModel) {

    val totalTomatoCount by statsViewModel.totalTomatoCount.collectAsState()
    val completedTaskCount by statsViewModel.completedTaskCount.collectAsState()
    val todayTaskAchievement by statsViewModel.todayTaskAchievement.collectAsState()
    val todayTomatoFocusAchievement by statsViewModel.todayTomatoFocusAchievement.collectAsState()

    Column {
        Row(Modifier.fillMaxWidth()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(110.dp)
//                        .background(Color(234, 117, 22))
                        .padding(10.dp)
                ) {
                    Text(text = "今日已完成")
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = todayTaskAchievement.completedCount.toString(),
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = if (todayTaskAchievement.completedCount >= todayTaskAchievement.previousCompletedCount) "比昨天多${todayTaskAchievement.completedCount - todayTaskAchievement.previousCompletedCount}"
                            else "比昨天少${abs(todayTaskAchievement.completedCount - todayTaskAchievement.previousCompletedCount)}个",
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(110.dp)
//                        .background(Color(234, 117, 22))
                        .padding(10.dp)
                ) {
                    Text(text = "今日事件数")
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = todayTaskAchievement.taskCount.toString(), fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = if (todayTaskAchievement.taskCount >= todayTaskAchievement.previousTaskCount) "比昨天多${todayTaskAchievement.taskCount - todayTaskAchievement.previousTaskCount}"
                            else "比昨天少${abs(todayTaskAchievement.taskCount - todayTaskAchievement.previousTaskCount)}个",
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))


        Row(Modifier.fillMaxWidth()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(110.dp)
//                        .background(Color(234, 117, 22))
                        .padding(10.dp)
                ) {
                    Text(text = "今日番茄数")
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = todayTomatoFocusAchievement.tomatoCount.toString(),
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = if (todayTomatoFocusAchievement.tomatoCount >= todayTomatoFocusAchievement.previousTomatoCount) "比昨天多${todayTomatoFocusAchievement.tomatoCount - todayTomatoFocusAchievement.previousTomatoCount}"
                            else "比昨天少${abs(todayTomatoFocusAchievement.tomatoCount - todayTomatoFocusAchievement.previousTomatoCount)}个",
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            val focusDuration =
                todayTomatoFocusAchievement.focusDuration - todayTomatoFocusAchievement.previousFocusDuration
            val focusDurationTip =
                if (focusDuration >= Duration.ZERO) "多${focusDuration.formatToHM()}" else "少${focusDuration.formatToHM()}"

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(110.dp)
//                        .background(Color(234, 117, 22))
                        .padding(10.dp)
                ) {
                    Text(text = "专注时长")
                    Text(
                        text = "比昨天${focusDurationTip}",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = todayTomatoFocusAchievement.focusDuration.formatToHM(),
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(7.dp))

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .height(110.dp)
//                        .background(Color(234, 117, 22))
                    .padding(10.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = completedTaskCount.toString(), fontSize = 30.sp ,
                    color = MaterialTheme.colorScheme.primary)
                Text(text = "历史累计完成事件数")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .height(110.dp)
//                        .background(Color(234, 117, 22))
                    .padding(10.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = totalTomatoCount.toString(), fontSize = 30.sp ,
                    color = MaterialTheme.colorScheme.primary)
                Text(text = "历史累计获得的番茄总数")
            }
        }

    }
}