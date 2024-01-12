package cn.liibang.pinoko.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.liibang.pinoko.ui.theme.XShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XDialogCard(onDismissRequest: () -> Unit,content: @Composable () -> Unit) {

   Dialog(onDismissRequest = onDismissRequest) {
       Column(
           Modifier
               .fillMaxWidth()
               .background(DatePickerDefaults.colors().containerColor)
               .padding(16.dp)
       ) {
           content()
       }
   }


}