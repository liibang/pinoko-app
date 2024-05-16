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
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.collectAsState
import cn.liibang.pinoko.ui.screen.TestScreen
import cn.liibang.pinoko.ui.screen.agenda.AgendaScreen
import cn.liibang.pinoko.ui.screen.agenda.AgendaViewModel
import cn.liibang.pinoko.ui.screen.category.CategoryScreen
import cn.liibang.pinoko.ui.screen.course.CourseForm
import cn.liibang.pinoko.ui.screen.course.TermCheckDialog
import cn.liibang.pinoko.ui.screen.focus.FocusListScreen
import cn.liibang.pinoko.ui.screen.focus.FocusRecordForm
import cn.liibang.pinoko.ui.screen.focus.FocusScreen
import cn.liibang.pinoko.ui.screen.focus.FocusViewModel
import cn.liibang.pinoko.ui.screen.habit.HabitListScreen
import cn.liibang.pinoko.ui.screen.habit.HabitForm
import cn.liibang.pinoko.ui.screen.habit.HabitScreen
import cn.liibang.pinoko.ui.screen.habit.HabitViewModel
import cn.liibang.pinoko.ui.screen.member.ChangePasswordScreen
import cn.liibang.pinoko.ui.screen.member.ProfileScreen
import cn.liibang.pinoko.ui.screen.task.TaskForm
import cn.liibang.pinoko.ui.screen.stats.StatsScreen
import cn.liibang.pinoko.ui.screen.task.TaskScreen
import cn.liibang.pinoko.ui.screen.task.TaskViewModel
import cn.liibang.pinoko.ui.screen.term.TermForm
import cn.liibang.pinoko.ui.screen.term.TermScreen
import cn.liibang.pinoko.ui.screen.setting.SettingViewModel
import cn.liibang.pinoko.ui.screen.term.TermViewModel
import cn.liibang.pinoko.ui.support.toTimestamp
import cn.liibang.pinoko.ui.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.time.LocalDate


val ROUTERS_FOR_MENUS = listOf(
    MainRouter.Agenda,
    MainRouter.Task,
    MainRouter.Focus,
    MainRouter.Stats,
//    MainRouter.SchoolTimeTable,
    MainRouter.Habit,
//    MainRouter.Test,
)

sealed interface Router {
    val route: String
}

sealed class MainRouter(override val route: String, val metadata: MetaData) : Router {
    object Agenda : MainRouter("Agenda", metadata = MetaData("我的日程", Icons.Filled.Splitscreen))
    object Task : MainRouter("Task", metadata = MetaData("事件清单", Icons.Default.TaskAlt))
    object Focus : MainRouter("Focus", metadata = MetaData("番茄专注", Icons.Outlined.Timer))
    object SchoolTimeTable : MainRouter(
        "SchoolTimeTable",
        metadata = MetaData(name = "学期管理", icon = Icons.Default.EditCalendar)
    )

    object Stats : MainRouter("Stats", metadata = MetaData("时间报告", Icons.Default.LegendToggle))

    object Test : MainRouter("Test", metadata = MetaData("测试页面", Icons.Default.TipsAndUpdates))

    object Habit : MainRouter("Habit", metadata = MetaData("我的习惯", Icons.Outlined.StarBorder))

    // 格式
    data class MetaData(val name: String, val icon: ImageVector?)
}


sealed class SubRouter(override val route: String) : Router {

    object TaskForm : SubRouter("TaskForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
        fun routeWithParam(value: String, dueDate: LocalDate) =
            "$route?id=$value&dueDate=${dueDate.toTimestamp()}"
    }

    object TermForm : SubRouter("TermForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
    }

    object CourseForm : SubRouter("CourseForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
    }

    object FocusList : SubRouter("FocusList")

    object FocusForm : SubRouter("FocusForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
    }

    object CategoryScreen : SubRouter("CategoryScreen")

    object ChangePasswordScreen : SubRouter("ChangePasswordScreen")
    object Profile : SubRouter("Profile")

    object HabitForm : SubRouter("HabitForm") {
        fun routeWithParam(value: String) = "$route?id=$value"
    }

    object HabitList : SubRouter("HabitList")

}


val LocalNavController = compositionLocalOf<NavController> { error("No NavController found!") }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BestScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    agendaViewModel: AgendaViewModel = hiltViewModel(),
    taskVieModel: TaskViewModel = hiltViewModel(),
    focusViewModel: FocusViewModel = hiltViewModel(),
    settingViewModel: SettingViewModel = hiltViewModel(),
    termViewModel: TermViewModel = hiltViewModel(),
    syncViewModel: SyncViewModel = hiltViewModel(),
    habitViewModel: HabitViewModel = hiltViewModel()
) {

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
//            cleanImePadding()
            destination.route?.let { mainViewModel.changeRoute(it.substringBefore("?")) }
        }

        CompositionLocalProvider(LocalNavController provides navController) {

            Scaffold(
                floatingActionButton = {
                    XFab(currentRouter = mainViewModel.currentRoute, taskVieModel)
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = when (mainViewModel.currentRoute) {
                    MainRouter.Agenda.route -> FabPosition.Center
                    MainRouter.Task.route -> FabPosition.Center
                    MainRouter.Focus.route -> FabPosition.Center
                    MainRouter.Habit.route -> FabPosition.Center
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
                    composable(MainRouter.Agenda.route) {
                        AgendaScreen(
                            mainViewModel.agendaDisplayMode,
                            agendaViewModel = agendaViewModel,
                            settingViewModel = settingViewModel,
                            termViewModel = termViewModel
                        )
                    }
                    composable(MainRouter.Task.route) {
                        TaskScreen(
                            taskViewModel = taskVieModel,
                            displayMode = mainViewModel.taskDisplayMode,
                            settingViewModel = settingViewModel
                        )
                    }
                    composable(MainRouter.SchoolTimeTable.route) {
                        TermScreen(termViewModel, settingViewModel = settingViewModel)
                    }
                    composable("${MainRouter.Focus.route}?taskId={taskId}") {
                        FocusScreen(
                            focusViewModel = focusViewModel,
                            taskViewModel = taskVieModel,
                            settingViewModel = settingViewModel,
                            taskID = it.arguments?.getString("taskId"),
                        )
                    }
                    composable(MainRouter.Stats.route) { StatsScreen() }

                    composable(MainRouter.Habit.route) {
                        HabitScreen()
                    }

                    composable(
                        route = "${SubRouter.TaskForm.route}?id={id}&date={date}&categoryId={categoryId}",
                        enterTransition = {
                            scaleIn(
                                animationSpec = tween(220, delayMillis = 90),
                                initialScale = 1.1f
                            ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
                        },
                        exitTransition = { ExitTransition.None },
                    ) {
                        val selectedDate by agendaViewModel.selectedDay.collectAsState()
                        val id = it.arguments?.getString("id")
                        val finalSelectDate = if (mainViewModel.currentRoute == MainRouter.Agenda.route) selectedDate else LocalDate.now()
                        TaskForm(
                            id,
                            dueDate = finalSelectDate,
                            categoryId = it.arguments?.getString("categoryId")
                        )
                    }
                    composable(
                        route = "${SubRouter.TermForm.route}?id={id}",
                    ) {
                        TermForm(
                            id = it.arguments?.getString("id"),
                            settingViewModel = settingViewModel
                        )
                    }

                    composable(
                        route = "${SubRouter.CourseForm.route}?id={id}",
                    ) {
                        val setting by settingViewModel.setting.collectAsState()
                        if (setting.termSetId != null) {
                            CourseForm(
                                id = it.arguments?.getString("id"),
                                setting = setting,
                                termViewModel = termViewModel
                            )
                        } else {
                            TermCheckDialog()
                        }
                    }
                    composable(
                        route = "${SubRouter.FocusForm.route}?id={id}",
                    ) {
                        val setting by settingViewModel.setting.collectAsState()
                        FocusRecordForm(id = it.arguments?.getString("id"), setting = setting)
                    }
                    composable(
                        route = SubRouter.FocusList.route,
                    ) {
                        FocusListScreen()
                    }
                    composable(
                        route = SubRouter.CategoryScreen.route,
                    ) {
                        CategoryScreen()
                    }
                    composable(
                        route = MainRouter.Test.route,
                    ) {
                        TestScreen()
                    }
                    composable(SubRouter.ChangePasswordScreen.route) {
                        ChangePasswordScreen()
                    }
                    composable(SubRouter.Profile.route) {
                        ProfileScreen()
                    }
                    composable("${SubRouter.HabitForm.route}?id={id}") {
                        HabitForm(id = it.arguments?.getString("id"), habitViewModel)
                    }

                    composable(SubRouter.HabitList.route) {
                        HabitListScreen(habitViewModel)
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
                        },
                        syncViewModel = syncViewModel
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






