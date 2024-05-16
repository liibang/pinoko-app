package cn.liibang.pinoko.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.support.showToast
import cn.liibang.pinoko.ui.support.timestampToDateTime
import cn.liibang.pinoko.ui.support.toTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelectorDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    dateSelected: (DateRange) -> Unit = {  },
    initValue: DateRange,
) {
    if (isShow) {
        val context = LocalContext.current
        Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier
                    .fillMaxSize()
//                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
//                val snackState = remember { SnackbarHostState() }
//                SnackbarHost(hostState = snackState, Modifier.zIndex(1f))

                val state = rememberDateRangePickerState(
                    initialSelectedStartDateMillis = initValue.startAt.toTimestamp(),
                    initialSelectedEndDateMillis = initValue.endAt.toTimestamp()
                )
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                    // Add a row with "Save" and dismiss actions.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { onDismissRequest() }) {
                            Icon(Icons.Filled.Close, contentDescription = "Localized description")
                        }
                        TextButton(
                            onClick = {
                                val startAt = timestampToDateTime(state.selectedStartDateMillis!!)
                                val endAt = timestampToDateTime(state.selectedEndDateMillis!!).run {
                                    LocalDateTime.of(toLocalDate(), LocalTime.MAX)
                                }
                                dateSelected(DateRange(startAt, endAt))
                                onDismissRequest()
                            },
                            enabled = state.selectedEndDateMillis?.let {
                                it <= LocalDate.now().atTime(LocalTime.MAX).toTimestamp()
                            } ?: false
                        ) {
                            Text(text = "保存")
                        }
                    }
                    DateRangePicker(state = state, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}