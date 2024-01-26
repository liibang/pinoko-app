package cn.liibang.pinoko.ui.screen.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.ui.component.DashedDivider
import cn.liibang.pinoko.ui.component.DashedDividerVertical
import java.time.LocalTime

@Composable
fun ScheduleContent() {
    val rowHeight = 80.dp // 这里设置你想要的高度
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight * 24)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        for (i in 1..24) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$i",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .offset(y = 10.dp)
                        .width(20.dp), // 设置一个固定的宽度
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Column {
                    Box(
                        Modifier
                            .height(rowHeight)
                            .fillMaxWidth()
                    ) {
                        Row(modifier = Modifier) {
                            (1..7).forEach { number ->
                                val bili =
                                    (LocalTime.of(16, 30).minute - LocalTime.of(
                                        16,
                                        10
                                    ).minute) / 60

                                Column(modifier = Modifier.weight(1f)) {
//                                // 在这里添加你的内容
                                    //ScheduleItem()
                                }
                                if (number != 7) {
                                    DashedDividerVertical(
                                        dashWidth = 10f,
                                        strokeWidth = 2.5f,
                                        color = Color.Gray.copy(0.5f)
                                    )
                                }
                            }
                        }
                        // 中央线条
                        DashedDivider(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray.copy(0.5f)
                        )
                    }
                    DashedDivider(
                        dashWidth = 10f,
                        strokeWidth = 2.5f,
                        color = Color.Gray.copy(0.5f)
                    )
                }
            }

        }
    }
}

@Composable
private fun ScheduleItem() {
    Box(
        Modifier
            .offset(y = ((80 / 60.toFloat()) * 30).dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(10))
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Text(text = "1231")
    }
}