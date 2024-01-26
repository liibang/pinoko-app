package cn.liibang.pinoko.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.toLocalDate
import cn.liibang.pinoko.ui.component.XTextField
import cn.liibang.pinoko.ui.screen.form.OptButton
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.task.EditMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//data class TermFromState(
//    val id: StringUUID? = null,
//    val name: String = "",
//    val startDate: LocalDate? = null,
//    val weeks: Int? = null
//)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermForm(id: String?, termViewModel: TermViewModel = hiltViewModel()) {

    val navController = LocalNavController.current
    val editMode = if (id == null) EditMode.CREATE else EditMode.UPDATE

    var formState by remember {
        mutableStateOf(
            TermPO(
                id = "",
                name = "",
                startDate = LocalDate.now(),
                weeks = 18,
                createdAt = LocalDateTime.MIN,
                updatedAt = LocalDateTime.MIN
            )
        )
    }



    LaunchedEffect(Unit) {
        id?.let {
            termViewModel.fetchById(it)?.run { formState = this }
        }
    }

    val enableOptButton by remember {
        derivedStateOf {
            editMode == EditMode.UPDATE || formState.name.isNotBlank()
        }
    }

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
                text = if (editMode == EditMode.CREATE) "添加学期" else "编辑学期",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            OptButton(
                enabled = enableOptButton,
                onClick = {
                    // TODO
                    val modified = LocalDateTime.now()
                    termViewModel.saveOrUpdate(
                        po = formState.copy(
                            createdAt = if (editMode == EditMode.CREATE) modified else formState.createdAt,
                            updatedAt = modified
                        )
                    )
                    navController.popBackStack()
                }
            )
        }
        // Header end...

        Spacer(Modifier.padding(top = 10.dp))

        XTextField(
            value = formState.name,
            onValueChange = {
                formState = formState.copy(name = it)
            },
            placeholder = {
                Text(
                    text = "添加一个名称",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            },
            supportingText = {
                Text(
                    text = "例如：2012-2013秋季学期",
                    color = Color.Gray.copy(0.5f),
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .offset(x = (-12).dp)
                )
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary.copy(0.8f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier
                .height(70.dp)
                .background(Color.Unspecified)
                .fillMaxWidth()
                .padding(start = 55.dp),
        )

        Spacer(modifier = Modifier.height(15.dp))

        var isShowDatePicker by remember { mutableStateOf(false) }
        TextButton(
            shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Unspecified,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
            ),
            onClick = { isShowDatePicker = true },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "开学日期",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formState.startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(CircleShape.copy(CornerSize(10.dp)))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }
            if (isShowDatePicker) {
                val dateState =
                    rememberDatePickerState(selectableDates = object : SelectableDates {})
                dateState.selectedDateMillis = formState.startDate.atStartOfDay(ZoneId.of("UTC"))
                    .toInstant().toEpochMilli()
                DatePickerDialog(onDismissRequest = { isShowDatePicker = false }, confirmButton = {
                    Row {
                        TextButton(onClick = { isShowDatePicker = false }) {
                            Text(text = "取消")
                        }
                        TextButton(onClick = {
                            formState =
                                formState.copy(startDate = dateState.selectedDateMillis.toLocalDate()!!)
                            isShowDatePicker = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                }) {
                    DatePicker(state = dateState)
                }
            }

        }


        var isShowWeekNumberPicker by remember {
            mutableStateOf(false)
        }
        WeekNumberPicker(
            isShow = isShowWeekNumberPicker,
            onDismissRequest = { isShowWeekNumberPicker = false },
            initWeeks = formState.weeks,
            startDate = formState.startDate,
            onSelect = { formState = formState.copy(weeks = it) }
        )
        TextButton(
            shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Unspecified,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp)
            ),
            onClick = { isShowWeekNumberPicker = true },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LineWeight,
                    contentDescription = "",
                    tint = Color.Transparent
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "学期周数",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formState.weeks.toString(),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .clip(CircleShape.copy(CornerSize(10.dp)))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }

        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "设置为当前学期", modifier = Modifier.weight(1f))
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false,
            ) {
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    modifier = Modifier.scale(0.65f),
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Yellow)
                )
            }
        }
    }

}