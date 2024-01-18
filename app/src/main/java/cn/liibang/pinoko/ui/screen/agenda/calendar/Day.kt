package cn.liibang.pinoko.ui.screen.agenda.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Day(
    dayText: String,
    isSelected: Boolean,
    clickable: Boolean,
    onClick: () -> Unit,
    isToday: Boolean,
    hasEvent: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(9.4.dp)
            .shadow(if (isSelected) 2.5.dp else 0.dp, CircleShape)
            .clip(CircleShape)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary else if (isToday) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .clickable(
                enabled = clickable,
                onClick = onClick,
            ),
    ) {
        Text(
            text = dayText,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
        )
        val markColor = if (hasEvent) {
            if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(markColor)
                .align(BiasAlignment(0f, 0.7f))
        )
    }
}