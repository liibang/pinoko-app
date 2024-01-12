package cn.liibang.pinoko.ui.screen.main

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LegendToggle
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.liibang.pinoko.ui.screen.agenda.AgendaScreen
import cn.liibang.pinoko.ui.screen.task.TaskForm
import cn.liibang.pinoko.ui.screen.stats.StatsScreen
import cn.liibang.pinoko.ui.screen.task.TaskScreen
import cn.liibang.pinoko.ui.screen.test.TaskModalForm
import cn.liibang.pinoko.ui.theme.AppTheme
import kotlinx.coroutines.launch


val ROUTERS_FOR_MENU = listOf(
    Router.Agenda,
    Router.Task,
    Router.SchoolTimeTable,
    Router.Focus,
    Router.Stats
)

sealed class Router(val route: String, val metadata: MetaData) {
    object Agenda : Router("Agenda", metadata = MetaData("我的日程", Icons.Filled.Splitscreen))
    object Task : Router("Task", metadata = MetaData("事件清单", Icons.Default.TaskAlt))
    object Focus : Router("Focus", metadata = MetaData("番茄专注", Icons.Default.EditCalendar))
    object SchoolTimeTable : Router(
        "SchoolTimeTable",
        metadata = MetaData(name = "课表管理", icon = Icons.Outlined.Timer)
    )

    object Stats : Router("Stats", metadata = MetaData("时间报告", Icons.Default.LegendToggle))

    object TaskForm :
        Router("TaskForm", metadata = MetaData("事件详情", Icons.Default.LegendToggle))

//    companion object  {
//        fun of(route: String) = ROUTERS_FOR_MENU.find { it.route ==  route}!!
//    }

    // 格式
    data class MetaData(val name: String, val icon: ImageVector?)
}


val LocalNavController = compositionLocalOf<NavController> { error("No NavController found!") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun BestScreen() {

    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(false)
    var isShowModalMenu by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    val mainViewModel: MainViewModel = viewModel()

    val window = (LocalView.current.context as Activity).window


//    WindowCompat.setDecorFitsSystemWindows(window, false)

    AppTheme {

        val surface = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp).toArgb()
        val surfaceDim = MaterialTheme.colorScheme.surfaceDim.toArgb()

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.route == Router.Agenda.route) {
                window.statusBarColor = surfaceDim
            } else {
                window.statusBarColor = surface
            }
            destination.route?.also { mainViewModel.changeRoute(it) }
        }

        CompositionLocalProvider(LocalNavController provides navController) {
            Scaffold(
                floatingActionButton = {
                    XFab(currentRouter = mainViewModel.currentRoute)
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = {
                    XBottomBar(
                        showMenu = { isShowModalMenu = true },
                        currentRoute = mainViewModel.currentRoute
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Router.Agenda.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding()),
//                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                ) {
                    composable(Router.Agenda.route) { AgendaScreen() }
                    composable(Router.Task.route) { TaskScreen() }
                    composable(Router.SchoolTimeTable.route) { }
                    composable(Router.Focus.route) { TaskModalForm() }
                    composable(Router.Stats.route) { StatsScreen() }
                    composable(
                        route = "${Router.TaskForm.route}?id={id}",
                        enterTransition = {
                            scaleIn(
                                animationSpec = tween(220, delayMillis = 90),
                                initialScale = 1.1f
                            ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
                        },
                        exitTransition = { ExitTransition.None },
                    ) {
                        TaskForm(it.arguments?.getString("id"))
                    }
                }
                // 导航菜单
                if (isShowModalMenu) {
                    ModalMenu(
                        currentRoute = mainViewModel.currentRoute,
                        modalBottomSheetState = modalBottomSheetState,
                        onDismissRequest = { isShowModalMenu = false },
                        navigateToNewPage = { router ->
                            navController.navTo(router.route)
                            scope.launch { modalBottomSheetState.hide() }.invokeOnCompletion {
                                if (!modalBottomSheetState.isVisible) {
                                    isShowModalMenu = false
                                }
                                // 关掉弹窗后修改当前router
//                                mainViewModel.onEvent()
                            }
                        }
                    )
                }
                // end
            }
        }


    }
}

fun NavHostController.navTo(route: String) {
    this.navigate(route) {
        popUpTo(this@navTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}









