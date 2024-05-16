package cn.liibang.pinoko.ui.screen.course

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.support.border
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.util.Random

fun hexToColor(hex: String): Color {
    val r = Integer.valueOf(hex.substring(1, 3), 16)
    val g = Integer.valueOf(hex.substring(3, 5), 16)
    val b = Integer.valueOf(hex.substring(5, 7), 16)
    return Color(r / 255.0f, g / 255.0f, b / 255.0f)
}

@Composable
fun ColorPickerDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (Color, String) -> Unit,
    initColorHexCode: String
) {
    var currentColor by remember {
        mutableStateOf(hexToColor(initColorHexCode))
    }

    if (isShow) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            val controller = rememberColorPickerController()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(15.dp)
            ) {
                Text(text = "选择颜色", fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                            .background(currentColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = String.format("#%06X", (0xFFFFFF and currentColor.toArgb())),
                        modifier = Modifier.width(100.dp)
                    )
                }

                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                    controller = controller,
                    onColorChanged = {
                        currentColor = it.color
                    },
                    initialColor = currentColor
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        onConfirm(
                            currentColor,
                            String.format("#%06X", (0xFFFFFF and currentColor.toArgb()))
                        )
                        onDismissRequest()
                    }) {
                        Text(text = "确认")
                    }
                }
            }
        }
    }


}