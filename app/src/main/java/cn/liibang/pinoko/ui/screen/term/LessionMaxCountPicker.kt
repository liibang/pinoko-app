package cn.liibang.pinoko.ui.screen.term

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.component.InfiniteCircularList
import cn.liibang.pinoko.ui.theme.XShape
import java.time.format.DateTimeFormatter

@Composable
fun LessonMaxCountPicker(isShow: Boolean, onDismissRequest: () -> Unit, initValue: Int, onSelect: (Int) -> Unit) {
    if (isShow) {
        // ====properties====
        val textSize = 15
        val width = 50
        val itemHeight = 70

        var number by remember {
            mutableIntStateOf(initValue)
        }

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

                Text(text = "课表的最大节数", fontWeight = FontWeight.SemiBold)

                Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    InfiniteCircularList(
                        width = width.dp,
                        itemHeight = itemHeight.dp,
                        items = (1..30).toList(),
                        initialItem = number,
                        textStyle = TextStyle(fontSize = textSize.sp),
                        textColor = Color.LightGray,
                        selectedTextColor = Color.Black,
                        onItemSelected = { i, item ->
                            number = item
                        }
                    )
                }
                // =======OPT button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 9.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(
                        onClick = {
                            onSelect(number)
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

