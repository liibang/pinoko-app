package cn.liibang.pinoko.ui.screen.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun StatsScreen(statsViewModel: StatsViewModel = hiltViewModel()) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
    val dateRangeItems = statsViewModel.dateRangeItems
    val selectedTabIndex by statsViewModel._selectedTabIndex
    val customerDateRange by statsViewModel._customerDateRange
    var isShowMenu by remember {
        mutableStateOf(false)
    }
    var isShowDateRangeSelectorDialog by remember {
        mutableStateOf(false)
    }
    DateRangeSelectorDialog(
        isShow = isShowDateRangeSelectorDialog,
        onDismissRequest = { isShowDateRangeSelectorDialog = false },
        dateSelected = {
            statsViewModel.changeCustomerDateRange(it)
            statsViewModel.changeDateRangeItem(dateRangeItems.lastIndex)
            isShowMenu = false
        },
        initValue = customerDateRange
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(10.dp)
    ) {

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "时间报告", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))

            val selectedMenuIndex = statsViewModel._selectedMenuIndex

            TextButton(
                onClick = { isShowMenu = true }, enabled = selectedTabIndex != 0
            ) {
                if (selectedTabIndex != 0) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedMenuIndex == dateRangeItems.lastIndex) {
                            customerDateRange.run {
                                if (startAt.toLocalDate() == endAt.toLocalDate()) "今天"
                                else "${startAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))} - ${
                                    endAt.format(
                                        DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                    )
                                }"
                            }
                        } else dateRangeItems[selectedMenuIndex].desc,
//                        modifier = Modifier.width(75.dp),
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.SemiBold,
                    )
                    DropdownMenu(
                        expanded = isShowMenu,
                        onDismissRequest = { isShowMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim),
                    ) {
                        dateRangeItems.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = { Text(text = item.desc, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    if (index == dateRangeItems.lastIndex) {
                                        isShowDateRangeSelectorDialog = true
                                    } else {
                                        statsViewModel.changeDateRangeItem(index)
                                        isShowMenu = false
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
        // END....
        Spacer(modifier = Modifier.height(15.dp))

        // ==========================TAB=============
        data class TabItem(val title: String, val icon: ImageVector, val onClick: () -> Unit = {})

        val tabItems = listOf(
            TabItem(title = "数据概览", icon = Icons.Default.PendingActions, onClick = {}),
            TabItem(title = "事件统计", icon = Icons.Default.Task, onClick = {}),
            TabItem(title = "番茄统计", icon = Icons.Default.PunchClock, onClick = {}),
        )
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = backgroundColor) {
            tabItems.forEachIndexed { index, it ->
                Tab(
                    selected = false,
                    onClick = {
                        statsViewModel.changeTab(index)
                        it.onClick()
                    },
                    modifier = Modifier,
                    enabled = true,
                    text = { Text(text = it.title, color = MaterialTheme.colorScheme.outline) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = Color.Unspecified,
                    interactionSource = remember { MutableInteractionSource() }
                )
            }
        }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(15.dp))

            when (selectedTabIndex) {
                0 -> StatsForSimpleScreen(statsViewModel)
                1 -> StatsForTaskScreen(statsViewModel)
                2 -> StatsForFocusScreen(statsViewModel)
                else -> {}
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}