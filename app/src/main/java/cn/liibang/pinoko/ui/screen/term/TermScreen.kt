package cn.liibang.pinoko.ui.screen.term

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.R
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import cn.liibang.pinoko.ui.screen.setting.SettingViewModel
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.reflect.KFunction1

@Composable
fun TermScreen(termViewModel: TermViewModel = hiltViewModel(), settingViewModel: SettingViewModel) {

    val terms by termViewModel.termList.collectAsState()
    val navController = LocalNavController.current

    val setting by settingViewModel.setting.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "",)
            }
            Spacer(modifier = Modifier.width(5.5.dp))
            Text(text = "学期管理", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (terms.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.no_term),
                    contentDescription = "",
                    modifier = Modifier.scale(1.75f)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "暂无学期信息", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            }
        } else {
            LazyColumn {
                items(terms) {
                    TermCard(
                        setting = setting,
                        updateSetting = settingViewModel::saveOrUpdate,
                        termPO = it,
                        toEdit = { navController.navigate(SubRouter.TermForm.routeWithParam(it.id)) },
                        doDelete = termViewModel::delete
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermCard(
    termPO: TermPO,
    toEdit: () -> Unit,
    doDelete: (TermPO) -> Unit,
    setting: SettingPO,
    updateSetting: (SettingPO) -> Unit
) {

    var isShowOptMenu by remember {
        mutableStateOf(false)
    }

    val currentDate = LocalDate.now()


    // 假设学期开始日期和结束日期是通过以下方式计算得到的
    val startDate = termPO.startDate
    val endDate = startDate.plusWeeks(termPO.weekCount.toLong())

    // 判断当前日期是否在学期开始和结束日期之间
    val tip = if (currentDate.isBefore(startDate)) {
        "学期未开始"
    } else if (currentDate.isAfter(endDate)) {
        "学期已经结束"
    } else {
        // 计算学期总天数
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate)

        // 计算学期已过的天数
        val passedDays = ChronoUnit.DAYS.between(startDate, currentDate)

        // 计算学期已过的百分比
        val passedPercentage = (passedDays.toBigDecimal().divide(
            totalDays.toBigDecimal(),
            2,
            RoundingMode.UP
        ) * 100.toBigDecimal()).setScale(0, RoundingMode.UNNECESSARY).toString()

        // 计算剩余的天数
        val remainingDays = ChronoUnit.DAYS.between(currentDate, endDate)
        "学期已过：$passedPercentage%，剩余${remainingDays}天"
    }



    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.padding(bottom = 15.dp),
        onClick = toEdit
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
                        text = tip,
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
                tint = if (termPO.id == setting.termSetId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
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
                            onClick = {
                                updateSetting(setting.copy(termSetId = termPO.id))
                                isShowOptMenu = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "编辑", fontWeight = FontWeight.SemiBold) },
                            onClick = toEdit
                        )
                        var isShowTermDeleteConfirmDialog by remember {
                            mutableStateOf(false)
                        }
                        TermDeleteConfirmDialog(
                            isShow = isShowTermDeleteConfirmDialog,
                            onDismissRequest = {
                                isShowTermDeleteConfirmDialog = false
                                isShowOptMenu = false
                            },
                            onConfirm = {
                                doDelete(termPO)
                                isShowOptMenu = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "删除", fontWeight = FontWeight.SemiBold) },
                            onClick = { isShowTermDeleteConfirmDialog = true }
                        )
                    }
                }
            }
        }
    }
}