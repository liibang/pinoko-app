package cn.liibang.pinoko.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.liibang.pinoko.ui.screen.habit.HabitScreen
import cn.liibang.pinoko.ui.screen.member.ProfileScreen


@Composable
fun TestScreen() {
    Row(Modifier.fillMaxSize()) {
        HabitScreen()
    }
}

