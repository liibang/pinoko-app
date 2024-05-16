package cn.liibang.pinoko.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.BottomAppBar
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun XBottomBar(
    showMenu: () -> Unit,
    viewModel: MainViewModel
) {
    val navController = LocalNavController.current
    when (viewModel.currentRoute) {
        MainRouter.Agenda.route -> {
            BottomBarTemplate(showMenu) {
                IconButton(
                    onClick = viewModel::switchAgendaDisplayMode,
                    modifier = Modifier.padding(end = 3.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.agendaDisplayMode == AgendaDisplayMode.CALENDAR) Icons.Default.CalendarViewWeek else Icons.Default.Splitscreen,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        MainRouter.Task.route -> {
            BottomBarTemplate(showMenu = showMenu) {
                IconButton(
                    onClick = viewModel::switchTaskDisplayMode,
                    modifier = Modifier.padding(end = 3.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.taskDisplayMode == TaskDisplayMode.LIST) Icons.Rounded.GridView else Icons.Default.Splitscreen,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        MainRouter.SchoolTimeTable.route -> {
//            BottomBarTemplate(showMenu = showMenu)
        }

        MainRouter.Focus.route -> {
            BottomBarTemplate(showMenu = showMenu) {
                IconButton(
                    onClick = { navController.navigate(SubRouter.FocusList.route) },
                    modifier = Modifier.padding(end = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = "history",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        MainRouter.Stats.route -> {
            BottomBarTemplate(showMenu = showMenu)
        }
        MainRouter.Habit.route -> {
            BottomBarTemplate(showMenu = showMenu) {
                IconButton(
                    onClick = { navController.navigate(SubRouter.HabitList.route) },
                    modifier = Modifier.padding(end = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TableRows,
                        contentDescription = "全部",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        else -> {}
    }

}


@Composable
fun BottomBarTemplate(
    showMenu: () -> Unit,
    modeButton: @Composable () -> Unit = {}
) {
    BottomAppBar(
        cutoutShape = MaterialTheme.shapes.small.copy(CornerSize(50)),
        backgroundColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
    ) {
        IconButton(
            onClick = showMenu,
            modifier = Modifier.padding(start = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        modeButton()
    }
}