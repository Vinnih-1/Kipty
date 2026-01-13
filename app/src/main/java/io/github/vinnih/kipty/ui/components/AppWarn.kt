package io.github.vinnih.kipty.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.theme.AppTheme

enum class WarnType {
    Notify,
    Error
}

@Composable
fun AppWarn(
    warnType: WarnType,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit,
    dismiss: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val color = when (warnType) {
        WarnType.Notify -> colors.onSecondaryContainer
        WarnType.Error -> colors.onErrorContainer
    }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(86.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.3f),
                contentColor = color
            )
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    icon.invoke()
                }
                Column(
                    modifier = Modifier.align(
                        Alignment.TopCenter
                    ).padding(horizontal = 48.dp)
                ) {
                    content.invoke()
                }
                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    dismiss.invoke()
                }
            }
        }
    }
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AppWarnPreview() {
    val typography = MaterialTheme.typography

    AppTheme {
        AppWarn(
            warnType = WarnType.Notify,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.notifications),
                    contentDescription = "Notification icon",
                    modifier = Modifier.size(36.dp)
                )
            },
            content = {
                Text(
                    text = "Receive transcription notifications",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Be notified when transcription are ready",
                    style = typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "Enable",
                    style = typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp).clickable(onClick = {})
                )
            },
            dismiss = {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null,
                        modifier = Modifier.size(
                            24.dp
                        )
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
        )
    }
}
