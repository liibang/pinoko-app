package cn.liibang.pinoko.ui.screen.task

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.liibang.pinoko.ui.theme.XShape
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class TimeUnit(private val desc: String) {
    MINUTE("分钟"), HOUR("小时"), DAY("天"), WEEK("周"), ;

    override fun toString(): String {
        return desc
    }
}

@Composable
fun ReminderPicker(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    dueDateTime: LocalDateTime,
    updateReminderTime: (LocalDateTime) -> Unit
) {
    if (isShow) {
        // ====properties====
        val textSize = 15
        val width = 50
        val itemHeight = 70

        var timeUnit by remember {
            mutableStateOf(TimeUnit.MINUTE)
        }
        var number by remember {
            mutableIntStateOf(1)
        }
        val now = LocalDateTime.now()

        val numberOfLong = number.toLong()
        val selectedDateTime = when (timeUnit) {
            TimeUnit.MINUTE -> dueDateTime.minusMinutes(numberOfLong)
            TimeUnit.HOUR -> dueDateTime.minusHours(numberOfLong)
            TimeUnit.DAY -> dueDateTime.minusDays(numberOfLong)
            TimeUnit.WEEK -> dueDateTime.minusWeeks(numberOfLong)
        }

        val savable = selectedDateTime.isBefore(dueDateTime) && selectedDateTime.isAfter(now)

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

                Text(text = "自定义提醒", fontWeight = FontWeight.SemiBold)

                Column(Modifier.padding(horizontal = 10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InfiniteCircularList(
                            width = width.dp,
                            itemHeight = itemHeight.dp,
                            items = (1..99).toList(),
                            initialItem = number,
                            textStyle = TextStyle(fontSize = textSize.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                number = item
                            }
                        )
                        InfiniteCircularList(
                            width = width.dp,
                            itemHeight = itemHeight.dp,
                            items = TimeUnit.values().toList(),
                            initialItem = timeUnit,
                            textStyle = TextStyle(fontSize = textSize.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                timeUnit = item
                            }
                        )
                        Text(text = "前", fontSize = (textSize * 1.5).sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (selectedDateTime.toLocalDate() == now.toLocalDate()) {
                            "今天 ${selectedDateTime.toLocalTime()}"
                        } else {
                            selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clip(XShape.Card.copy(CornerSize(40)))
                            .background(MaterialTheme.colorScheme.outline.copy(0.1f))
                            .padding(vertical = 10.dp)
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "提醒时间已过",
                        color = MaterialTheme.colorScheme.error.copy(if (savable) 0f else 1f),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 13.sp
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
                    TextButton(onClick = {
                        updateReminderTime(selectedDateTime)
                        onDismissRequest()
                    }, enabled = savable) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> InfiniteCircularList(
    width: Dp,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
    items: List<T>,
    initialItem: T,
    itemScaleFact: Float = 1.5f,
    textStyle: TextStyle,
    textColor: Color,
    selectedTextColor: Color,
    onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> }
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val scrollState = rememberLazyListState(0)
    var lastSelectedIndex by remember {
        mutableStateOf(0)
    }
    var itemsState by remember {
        mutableStateOf(items)
    }
    LaunchedEffect(items) {
        var targetIndex = items.indexOf(initialItem) - 1
        targetIndex += ((Int.MAX_VALUE / 2) / items.size) * items.size
        itemsState = items
        lastSelectedIndex = targetIndex
        scrollState.scrollToItem(targetIndex)
    }
    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(itemHeight * numberOfDisplayedItems),
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = scrollState
        )
    ) {
        items(
            count = Int.MAX_VALUE,
            itemContent = { i ->
                val item = itemsState[i % itemsState.size]
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            val y = coordinates.positionInParent().y - itemHalfHeight
                            val parentHalfHeight =
                                (coordinates.parentCoordinates?.size?.height ?: 0) / 2f
                            val isSelected =
                                (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                            if (isSelected && lastSelectedIndex != i) {
                                onItemSelected(i % itemsState.size, item)
                                lastSelectedIndex = i
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toString(),
                        style = textStyle,
                        color = if (lastSelectedIndex == i) {
                            selectedTextColor
                        } else {
                            textColor
                        },
                        fontSize = if (lastSelectedIndex == i) {
                            textStyle.fontSize * itemScaleFact
                        } else {
                            textStyle.fontSize
                        }
                    )
                }
            }
        )
    }
}