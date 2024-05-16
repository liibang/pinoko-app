package cn.liibang.pinoko.ui.screen.habit

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import cn.liibang.pinoko.ui.support.toDateMillis
import cn.liibang.pinoko.ui.support.toLocalDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDatePicker(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    initData: LocalDate
) {
    if (isShow) {
        val dateState = rememberDatePickerState(selectableDates = object : SelectableDates {}, initialSelectedDateMillis = initData.toDateMillis())
        DatePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
            Row {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "取消")
                }
                TextButton(onClick = {
                    onConfirm(dateState.selectedDateMillis.toLocalDate()!!)
                    onDismissRequest()
                }) {
                    Text(text = "确定")
                }
            }
        }) {
            DatePicker(state = dateState)
        }
    }
}