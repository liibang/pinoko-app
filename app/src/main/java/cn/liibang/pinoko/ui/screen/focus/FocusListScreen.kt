package cn.liibang.pinoko.ui.screen.focus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import cn.liibang.pinoko.ui.support.formatToHM
import cn.liibang.pinoko.ui.support.showToast
import com.androidpoet.dropdown.Space
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FocusListScreen(focusViewModel: FocusViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val records by focusViewModel.records.collectAsState()

    val recordGroup = records.groupBy { it.startAt.toLocalDate() }.toSortedMap(compareByDescending { it })
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp))
                .padding(10.dp)
        ) {
            // 专注列表 TODO
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "专注记录", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            LazyColumn {
                recordGroup.forEach { (date, items) ->
                    item {
                        Text(
                            text = date.toString(),
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 4.dp),
                        )
                    }
                    items(items.sortedByDescending { it.endAt }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            onClick = { navController.navigate(SubRouter.FocusForm.routeWithParam(item.id)) }
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Row {
                                    Text(
                                        text = "${
                                            item.startAt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                                        } - ${item.endAt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                        color = Color.Gray
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(text = item.focusDuration.formatToHM(), color = Color.Gray)
                                }
                                if (item.taskName != null) {
                                    Row(verticalAlignment = CenterVertically) {
                                        Icon(imageVector = Icons.Default.TaskAlt, contentDescription = "", tint = Color.Gray, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(text = item.taskName, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                if (item.note.isNotEmpty()) {
                                    Row(verticalAlignment = CenterVertically) {
                                        Icon(imageVector = Icons.Default.Note, contentDescription = "", modifier = Modifier.size(15.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(text = item.note, fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                item { Spacer(modifier = Modifier.height(50.dp)) }
            }
        }
        Button(
            onClick = { navController.navigate(SubRouter.FocusForm.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 15.dp, end = 15.dp)
                .align(Alignment.BottomCenter),
            elevation = ButtonDefaults.buttonElevation(2.dp)
        ) {
            Text(text = "补记")
        }
    }
}