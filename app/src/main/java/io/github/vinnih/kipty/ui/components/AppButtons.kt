package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.theme.AppTheme

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = MaterialTheme.colorScheme.onPrimary,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = container
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "Arrow back icon",
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun EditButton(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.onPrimary) {
    IconButton(
        onClick = {},
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.edit_document),
            contentDescription = "Edit audio icon",
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GenerateTranscriptionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = MaterialTheme.colorScheme.onPrimary,
    content: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(size = 8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        )
    ) {
        Row(
            modifier = Modifier.fillMaxHeight().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(
                16.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_document),
                contentDescription = "Generate transcription button",
                modifier = Modifier.size(24.dp)
            )
            Text(text = "Transcribe Audio")
        }
    }
}

@Composable
fun CreateAudioButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    container: Color = MaterialTheme.colorScheme.primary,
    content: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(size = 8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun CancelAudioButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = MaterialTheme.colorScheme.primaryContainer,
    content: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(size = 8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        )
    ) {
        Text(text = "Cancel")
    }
}

@Composable
fun PlayPauseAudioButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = MaterialTheme.colorScheme.onPrimary,
    content: Color = MaterialTheme.colorScheme.primary
) {
    Row(modifier = modifier.padding(4.dp)) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(.8f).fillMaxHeight(),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                bottomStart = 8.dp
            ),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = container,
                contentColor = content
            )
        ) {
            Row(
                modifier = Modifier.fillMaxHeight().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "Play and pause button",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Play")
            }
        }
        VerticalDivider(thickness = 1.dp)
        Button(
            onClick = {},
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 8.dp,
                bottomStart = 0.dp,
                bottomEnd = 8.dp
            ),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = container,
                contentColor = content
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_drop_down),
                contentDescription = "Arrow drop down button",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BackButtonPreview() {
    AppTheme {
        BackButton(onClick = {})
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EditButtonPreview() {
    AppTheme {
        EditButton(tint = MaterialTheme.colorScheme.inversePrimary)
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PlayPauseButtonPreview() {
    AppTheme {
        PlayPauseAudioButton(onClick = {}, modifier = Modifier.width(140.dp).height(40.dp))
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun GenerateTranscriptionButtonPreview() {
    AppTheme {
        GenerateTranscriptionButton(onClick = {
        }, modifier = Modifier.width(200.dp).height(60.dp).padding(4.dp))
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CreateAudioButtonPreview() {
    AppTheme {
        CreateAudioButton(text = "Next", onClick = {
        }, modifier = Modifier.width(200.dp).height(60.dp).padding(4.dp))
    }
}
