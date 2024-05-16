package cn.liibang.pinoko.ui.screen.main

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cn.liibang.pinoko.ui.screen.category.DEFAULT_CATEGORY_ALL
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.ui.support.toTimestamp
import de.charlex.compose.SpeedDialData
import de.charlex.compose.SpeedDialFloatingActionButton
import java.time.LocalDate


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun XFab(currentRouter: String, taskViewModel: TaskViewModel) {
    val navController = LocalNavController.current
    val categoryId by taskViewModel.selectedCategoryID.collectAsState()
    when (currentRouter) {
        MainRouter.Agenda.route -> {
            SpeedDialFloatingActionButton(
                modifier = Modifier,
                initialExpanded = false,
                animationDuration = 300,
                animationDelayPerSelection = 100,
                showLabels = true,
                fabBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                speedDialBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                speedDialContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                speedDialData = listOf(
                    SpeedDialData(
                        label = "事件",
                        painter = rememberVectorPainter(image = Icons.Default.TaskAlt),
                        onClick = { navController.navigate(SubRouter.TaskForm.route) }
                    ),
                    SpeedDialData(
                        label = "课程",
                        painter = rememberVectorPainter(image = Icons.Default.MenuBook),
                        onClick = { navController.navigate(SubRouter.CourseForm.route) }
                    )
                ),
            )
        }

        MainRouter.Task.route -> {
            FabTemplate {
                if (categoryId == DEFAULT_CATEGORY_ALL.id) {
                    navController.navigate(SubRouter.TaskForm.route)
                } else {
                    navController.navigate("${SubRouter.TaskForm.route}?categoryId=${categoryId}")
                }


            }
        }

        MainRouter.SchoolTimeTable.route -> {
            FabTemplate { navController.navigate(SubRouter.TermForm.route) }
        }

        MainRouter.Focus.route -> {
//            FabTemplate { navController.navigate(SubRouter.TermForm.route) }
        }

        MainRouter.Habit.route -> {
            FabTemplate { navController.navigate(SubRouter.HabitForm.route) }
        }

        SubRouter.HabitList.route -> {
            FabTemplate { navController.navigate(SubRouter.HabitForm.route) }
        }

        else -> {

        }
    }

}

@Composable
private fun FabTemplate(onClick: () -> Unit) {
    androidx.compose.material.FloatingActionButton(
        onClick = onClick,
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}