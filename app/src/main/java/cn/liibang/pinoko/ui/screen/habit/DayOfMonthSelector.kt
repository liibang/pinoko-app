package cn.liibang.pinoko.ui.screen.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.component.InfiniteCircularList
import cn.liibang.pinoko.ui.screen.agenda.calendar.displayText
import cn.liibang.pinoko.ui.theme.XShape
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate

private val items = (1..31).map { it }

@Composable
fun DayOfMonthSelector(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (Int) -> Unit,
    initValue: Int,
    title: String
) {
    if (isShow) {
        // ====properties====
        val textSize = 15.5
        val width = 85
        val itemHeight = 70

        var selected by remember { mutableStateOf(initValue) }

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .clip(XShape.Card)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(16.dp)
            ) {

                Text(text = title, fontWeight = FontWeight.SemiBold)

                Column(Modifier.padding(horizontal = 10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Unspecified)
                            .padding(horizontal = 45.dp)
                    ) {
                        InfiniteCircularList(
                            width = width.dp,
                            itemHeight = itemHeight.dp,
                            items = items,
                            initialItem = initValue,
                            textStyle = TextStyle(fontSize = textSize.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                selected = item
                            }
                        )
                    }
                }
                // =======OPT button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 9.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(
                        onClick = {
                            onSelect(selected)
                            onDismissRequest()
                        },
                    ) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}



