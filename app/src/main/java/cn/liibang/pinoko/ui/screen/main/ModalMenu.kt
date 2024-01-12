package cn.liibang.pinoko.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalMenu(
    modalBottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    navigateToNewPage: (Router) -> Unit,
    currentRoute: String
) {

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        containerColor = MaterialTheme.colorScheme.surfaceDim,
    ) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            InteractiveItem()
            Spacer(modifier = Modifier.height(18.dp))
            ROUTERS_FOR_MENU.forEach {
                val isSelected = currentRoute == it.route
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceDim),
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.small.copy(
                                CornerSize(20)
                            )
                        )
                        .clickable(true, onClick = {
                            navigateToNewPage(it)
                        }),
                    headlineContent = {
                        Text(
                            text = it.metadata.name,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = it.metadata.icon!!,
                            contentDescription = "",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    },
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
private fun InteractiveItem() {

    var isShowMenu by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            Modifier
                .clip(CircleShape.copy(CornerSize(10.dp)))
                .clickable(
                    enabled = true,
                    onClick = { isShowMenu = true }, onClickLabel = null, role = null,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.outline),
                )
                .weight(1f)
                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)

        ) {
            SubcomposeAsyncImage(
                model = "https://foruda.gitee.com/avatar/1677053740056224121/5462387_i_tell_you_1618064317.png",
                loading = {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator()
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small.copy(CornerSize(50)))
                    .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            )
            DropdownMenu(expanded = isShowMenu, onDismissRequest = { isShowMenu = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)) {
                DropdownMenuItem(
                    text = { Text("个人资料", fontWeight = FontWeight.SemiBold) },
                    onClick = { },
                )
                DropdownMenuItem(
                    text = { Text("修改密码", fontWeight = FontWeight.SemiBold) },
                    onClick = { },
                )
                DropdownMenuItem(
                    text = { Text("注销", fontWeight = FontWeight.SemiBold) },
                    onClick = { }
                )
            }
            Spacer(modifier = Modifier.width(11.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 3.dp)
            ) {
                // title
                Text(
                    text = "硬核派克",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // subtitle
                Text("已坚持计划1天", color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // opt btn group
        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

