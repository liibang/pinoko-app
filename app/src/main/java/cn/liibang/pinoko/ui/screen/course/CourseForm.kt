package cn.liibang.pinoko.ui.screen.course

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.ui.component.OptButton
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.main.LocalNavController

@Composable
fun CourseForm(id: StringUUID?) {

    val navController = LocalNavController.current
    val enableOptButton = false

    val tipColor = Color.Gray.copy(0.5f)

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    "close",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = if (id == null) "添加课程" else "编辑课程",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            OptButton(
                enabled = enableOptButton,
                onClick = {
                    // TODO
                    navController.popBackStack()
                }
            )
        }
        // Header end...

        XTextField(
            value = "",
            onValueChange = {

            },
            placeholder = {
                Text(
                    text = "课程名称",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.5.sp,
                    color = MaterialTheme.colorScheme.outline.copy(0.66f),
                )
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 15.5.sp,
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray.copy(0.1f),
                focusedContainerColor = Color.Gray.copy(0.1f),
                cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier
//                .clip(CardDefaults.shape)
                .background(Color.Unspecified)
                .fillMaxWidth()
                .padding(start = 5.dp, top = 15.dp),
            leadingIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "",
                        modifier = Modifier.scale(1.0f)
                    )
                }
            },
            paddingValues = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
        )

        Column(Modifier.padding(top = 15.dp)) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceDim),
                elevation = CardDefaults.cardElevation(0.5.dp)
            ) {
                Row(
                    Modifier
                        .clickable { }
                        .padding(12.5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "周数")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "必填", color = tipColor)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "",
                        tint = tipColor
                    )
                }
                Row(
                    Modifier
                        .clickable { }
                        .padding(12.5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "时间")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "必填", color = tipColor)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "",
                        tint = tipColor
                    )
                }
                Row(
                    Modifier
                        .clickable { }
                        .padding(12.5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "教室")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "选填", color = tipColor)
                }
                Row(
                    Modifier
                        .clickable { }
                        .padding(12.5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "教室")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "选填", color = tipColor)
                }
            }
        }

    }
}
