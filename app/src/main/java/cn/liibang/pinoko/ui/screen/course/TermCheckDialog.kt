package cn.liibang.pinoko.ui.screen.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.MainRouter

@Composable
fun TermCheckDialog() {
    val navController = LocalNavController.current
    val onDismissRequest: () -> Unit = { navController.popBackStack() }
    Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = true)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.surfaceDim)
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // body...
            Text(text = "您还没有添加或设置学期")
            // 操作区域
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = "取消")
                }
                TextButton(onClick = {
                    onDismissRequest()
                    navController.navigate(MainRouter.SchoolTimeTable.route)
                }) {
                    Text(text = "前往操作")
                }
            }
        }
    }
}