package cn.liibang.pinoko.ui.screen.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.liibang.pinoko.ui.screen.main.LocalNavController
import cn.liibang.pinoko.ui.screen.main.SubRouter
import java.time.format.DateTimeFormatter

@Composable
fun HabitListScreen(habitViewModel: HabitViewModel) {
    val navController = LocalNavController.current

    val habits by habitViewModel.allHabits.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.1.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "所有习惯", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }


        LazyColumn {
            items(habits) {
                Spacer(modifier = Modifier.height(5.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.padding(bottom = 14.dp, start = 10.dp, end = 10.dp),
                    onClick = { navController.navigate(SubRouter.HabitForm.routeWithParam(it.id)) }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Unspecified)
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (it.remindTime != null) {
                            Text(
                                text = it.remindTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 12.sp
                            )
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(13.dp)
                            )
                        } else {
                            Text(
                                text = "设定提醒",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { navController.navigate(SubRouter.HabitForm.routeWithParam(it.id)) }
                            )
                        }
                    }
                }
            }
        }

    }


}