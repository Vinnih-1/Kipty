package io.github.vinnih.kipty.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.PlayerControls
import io.github.vinnih.kipty.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(controller: PlayerController, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val player = controller.player

    Scaffold { paddingValues ->
        ModalBottomSheet(
            modifier =
                Modifier.fillMaxHeight().padding(
                    paddingValues
                ),
            scrimColor = Color.Transparent,
            sheetState = sheetState,
            onDismissRequest = {
            },
            shape = RectangleShape,
            dragHandle = {}
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(72.dp)) {
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(
                        painter = painterResource(R.drawable.keyboard_arrow_down),
                        contentDescription = ""
                    )
                }
                Text(text = "Player", modifier = Modifier.align(Alignment.Center))
            }
            HorizontalDivider(thickness = 2.dp)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column(Modifier.padding(top = 20.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.broken_image),
                        contentDescription = ""
                    )
                }
                Text(
                    text = player.mediaMetadata.displayTitle.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                PlayerControls(controller = controller)
            }
        }
    }
}

@Preview
@Composable
fun PlayerScreenPreview() {
    AppTheme {
        PlayerScreen(controller = FakePlayerViewModel(), {})
    }
}
