package cn.liibang.pinoko.ui.screen.main

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import de.charlex.compose.SpeedDialData
import de.charlex.compose.SpeedDialFloatingActionButton


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun XFab(currentRouter: String) {
    val navController = LocalNavController.current

    when (currentRouter) {
        Router.Agenda.route -> {
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
                        onClick = { navController.navigate(Router.TaskForm.route) }
                    ),
                    SpeedDialData(
                        label = "课程",
                        painter = rememberVectorPainter(image = Icons.Default.MenuBook),
                        onClick = {}
                    )
                ),
            )
        }
        Router.Task.route -> {
            androidx.compose.material.FloatingActionButton(
                onClick = { navController.navigate(Router.TaskForm.route) },
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        else -> {

        }
    }


}