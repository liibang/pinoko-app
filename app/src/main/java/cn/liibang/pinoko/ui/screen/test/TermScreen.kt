package cn.liibang.pinoko.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TermScreen(termViewModel: TermViewModel = hiltViewModel()) {

    val terms by termViewModel.termList.collectAsState()
    val navController = LocalNavController.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "学期管理", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(terms) {
                TermCard(
                    termPO = it,
                    toEdit = { navController.navigate(SubRouter.TermForm.routeWithParam(it.id)) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermCard(termPO: TermPO, toEdit: () -> Unit) {

    var isShowOptMenu by remember {
        mutableStateOf(false)
    }

    val currentDate = LocalDate.now()


    // 假设学期开始日期和结束日期是通过以下方式计算得到的
    val startDate = termPO.startDate
    val endDate = startDate.plusWeeks(termPO.weeks.toLong())

    // 判断当前日期是否在学期开始和结束日期之间
    val tipOne = if (currentDate.isBefore(startDate)) {
        "学期未开始"
    } else if (currentDate.isAfter(endDate)) {
        "学期已经结束"
    } else {
        // 计算学期总天数
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate)

        // 计算学期已过的天数
        val passedDays = ChronoUnit.DAYS.between(startDate, currentDate)

        // 计算学期已过的百分比
        val passedPercentage = passedDays.toBigDecimal().divide(totalDays.toBigDecimal(), 1, RoundingMode.UP)

        "学期已过：$passedPercentage%"
    }

    // 计算剩余的天数
    val remainingDays = ChronoUnit.DAYS.between(currentDate, endDate)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.padding(bottom = 15.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "学期",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = termPO.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }
                Row {
                    Text(
                        text = "$tipOne，剩余${remainingDays}天",
                        color = Color.Gray,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "",
                modifier = Modifier.size(15.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(onClick = { isShowOptMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                    DropdownMenu(
                        expanded = isShowOptMenu,
                        onDismissRequest = { isShowOptMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "设为当前", fontWeight = FontWeight.SemiBold) },
                            onClick = { /*TODO*/ },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "编辑", fontWeight = FontWeight.SemiBold) },
                            onClick = toEdit)
                        DropdownMenuItem(
                            text = { Text(text = "删除", fontWeight = FontWeight.SemiBold) },
                            onClick = { /*TODO*/ })
                    }
                }
            }
        }
    }
}