package cn.liibang.pinoko.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.liibang.pinoko.ui.component.BottomSheetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen() {

    var  showable by remember {
        mutableStateOf(true)
    }

    BottomSheetDialog(
        visible = showable,
        onDismissRequest = {showable = false},
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
    ) {
        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
//                .fillMaxSize()
                .fillMaxWidth()
//                .statusBarsPadding()
//            .navigationBarsPadding()
                .imePadding()
                .padding(10.dp),
//            verticalArrangement = Arrangement.Bottom
        ) {
            TextField(value = "", onValueChange = {})
//            Spacer(Modifier.weight(1f))
            Button(onClick = {}) {
                Text(text = "Yeah Visible")
            }
        }
    }
//    ModalBottomSheet(sheetState = rememberModalBottomSheetState() ,onDismissRequest = { /*TODO*/ }) {
//
//    }

//    Dialog(onDismissRequest = { /*TODO*/ }, properties = DialogProperties(decorFitsSystemWindows = false)) {
//
//
//    }

}