package cn.liibang.pinoko.ui.screen.main


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cn.liibang.pinoko.data.AppDatabase


class MainViewModel(val appDatabase: AppDatabase = AppDatabase.getDatabase()) : ViewModel() {

    var currentRoute by mutableStateOf(Router.Agenda.route)
        private set

    fun changeRoute(route: String) {
        currentRoute = route
    }

    init {

    }

    fun scanNotify() {
//        appDatabase.taskDao().selectJob()
    }

}