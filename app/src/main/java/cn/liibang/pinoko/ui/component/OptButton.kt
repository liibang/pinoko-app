package cn.liibang.pinoko.ui.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun OptButton(
    enabled: Boolean,
    onClick: () -> Unit,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )
) {
    FilterChip(
        selected = true,
        enabled = enabled,
        onClick = onClick,
        label = { Text(text = "保存", fontWeight = FontWeight.SemiBold) },
        colors = colors
    )
}