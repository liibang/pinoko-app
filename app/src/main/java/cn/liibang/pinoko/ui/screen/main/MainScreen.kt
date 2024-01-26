package cn.liibang.pinoko.ui.screen.main

import android.app.Activity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import cn.liibang.pinoko.ui.screen.agenda.AgendaScreen
import cn.liibang.pinoko.ui.screen.task.TaskForm
import cn.liibang.pinoko.ui.screen.stats.StatsScreen
import cn.liibang.pinoko.ui.screen.task.TaskScreen
import cn.liibang.pinoko.ui.screen.term.TermForm
import cn.liibang.pinoko.ui.screen.term.TermScreen
import cn.liibang.pinoko.ui.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


val ROUTERS_FOR_MENUS = listOf(
    MainRouter.Agenda,
    MainRouter.Task,
    MainRouter.SchoolTimeTable,
    MainRouter.Focus,
    MainRouter.Stats
)

sealed class MainRouter(val route: String, val metadata: MetaData) {
    object Agenda : MainRouter("Agenda", metadata = MetaData("我的日程", Icons.Filled.Splitscreen))
    object Task : MainRouter("Task", metadata = MetaData("事件清单", Icons.Default.TaskAlt))
    object Focus : MainRouter("Focus", metadata = MetaData("番茄专注", Icons.Outlined.Timer))
    object SchoolTimeTable : MainRouter(
        "SchoolTimeTable",
        metadata = MetaData(name = "学期管理", icon = Icons.Default.EditCalendar)
    )

    object Stats : MainRouter("Stats", metadata = MetaData("时间报告", Icons.Default.LegendToggle))

//    companion object  {
//        fun of(route: String) = ROUTERS_FOR_MENU.find { it.route ==  route}!!
//    }

    // 格式
    data class MetaData(val name: String, val icon: ImageVector?)
}


sealed class SubRouter(val route: String) {

    object TaskForm : SubRouter("TaskForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
    }

    object TermForm : SubRouter("TermForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
    }

}



val LocalNavController = compositionLocalOf<NavController> { error("No NavController found!") }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
@Preview
fun BestScreen(mainViewModel: MainViewModel = hiltViewModel()) {

    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(false)
    var isShowModalMenu by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val window = (LocalView.current.context as Activity).window


    // 允许用户通知
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }


    AppTheme {

        val surface = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp).toArgb()
        val surfaceDim = MaterialTheme.colorScheme.surfaceDim.toArgb()

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.route == MainRouter.Agenda.route) {
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
                floatingActionButtonPosition = when(mainViewModel.currentRoute) {
                    MainRouter.Agenda.route -> FabPosition.Center
                    MainRouter.Task.route -> FabPosition.Center
                    else -> FabPosition.End
                },
                bottomBar = {
                    XBottomBar(
                        showMenu = { isShowModalMenu = true },
                        viewModel = mainViewModel
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = MainRouter.Agenda.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding()),
//                enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                ) {
                    composable(MainRouter.Agenda.route) { AgendaScreen(mainViewModel.agendaDisplayMode) }
                    composable(MainRouter.Task.route) { TaskScreen() }
                    composable(MainRouter.SchoolTimeTable.route) {
                        TermScreen()
                    }
                    composable(MainRouter.Focus.route) { }
                    composable(MainRouter.Stats.route) { StatsScreen() }
                    composable(
                        route = "${SubRouter.TaskForm.route}?id={id}",
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
                    composable(
                        route = "${SubRouter.TermForm.route}?id={id}",
                    ) {
                        TermForm(id = it.arguments?.getString("id"))
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









