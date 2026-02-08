package io.github.vinnih.kipty.ui.speech

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.formatTime
import kotlinx.coroutines.launch

private enum class Scene {
    TOGGLE,
    RECORDING,
    PROCESSING,
    RESULT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechScreen(
    phrase: AudioTranscription?,
    selectedAudio: AudioEntity?,
    onPlay: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    recordController: SpeechViewModel = hiltViewModel()
) {
    if (phrase == null || selectedAudio == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = MaterialTheme.colorScheme
    var permissionGranted by remember { mutableStateOf(false) }
    var speechEntity by remember { mutableStateOf<SpeechEntity?>(null) }
    var scene by remember { mutableStateOf(Scene.TOGGLE) }
    val uiState by recordController.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    RequestAudioPermission {
        permissionGranted = true
    }

    LaunchedEffect(uiState.result) {
        if (scene == Scene.PROCESSING) {
            scene = Scene.RESULT
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss.invoke()
            recordController.abortRecording()
        },
        sheetState = sheetState,
        containerColor = colors.secondaryContainer,
        modifier = modifier
    ) {
        SpeechComponent(
            scene = scene,
            onToggle = {
                ToggleScene(
                    permissionGranted = permissionGranted,
                    phrase = phrase,
                    onPlay = {
                        recordController.stopTempAudio()
                        onPlay.invoke()
                    },
                    onToggle = {
                        recordController.toggleRecording(selectedAudio.audioPath)
                        scene = Scene.RECORDING
                    }
                )
            },
            onRecording = {
                RecordingScene(
                    phrase = phrase,
                    amplitudes = uiState.amplitudes,
                    recordingTime = uiState.recordingTime,
                    onRecord = {
                        recordController.toggleRecording(selectedAudio.audioPath)
                        scope.launch {
                            scene = Scene.PROCESSING
                            speechEntity = recordController.getById(
                                recordController.calculatePronunciationScore(phrase).toInt()
                            )
                        }
                    }
                )
            },
            onProcessing = {
                ProcessingScene()
            },
            onResult = {
                ResultScene(
                    speechEntity = speechEntity,
                    phrase = phrase,
                    uiState = uiState,
                    onRetry = {
                        recordController.clearAll()
                        scene = Scene.TOGGLE
                    },
                    onPlay = {
                        recordController.stopTempAudio()
                        onPlay.invoke()
                    },
                    onListen = {
                        recordController.playTempAudio(it)
                    }
                )
            }
        )
    }
}

@Composable
private fun ToggleScene(
    phrase: AudioTranscription,
    permissionGranted: Boolean,
    onPlay: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Practice this phrase",
                style = typography.titleLarge,
                color = colors.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap the microphone and speak the phrase below",
                style = typography.bodyMedium,
                color = colors.onSecondaryContainer,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colors.secondary
            ),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clickable(onClick = onPlay)
        ) {
            Text(
                text = phrase.text,
                style = typography.titleLarge,
                color = colors.onSecondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        BaseButton(
            onClick = {
                if (permissionGranted) {
                    onToggle.invoke()
                }
            },
            modifier = Modifier
                .size(84.dp)
                .background(colors.secondary, CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.mic),
                contentDescription = null,
                tint = colors.onSecondary
            )
        }
        Text(
            text = "Tap to start recording",
            style = typography.bodyMedium,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun RecordingScene(
    phrase: AudioTranscription,
    amplitudes: List<Float>,
    recordingTime: Long,
    onRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (recordingTime == 60L) onRecord.invoke()

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Listening...",
            style = typography.titleLarge,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Speak clearly into your microphone",
            style = typography.bodyMedium,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colors.secondary
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = phrase.text,
                style = typography.titleLarge,
                color = colors.onSecondary.copy(alpha = .7f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        AudioWaveform(
            amplitudes = amplitudes,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = recordingTime.times(1_000).formatTime(),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.primary,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFB71C1C))
                .clickable { onRecord.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        Text(
            text = "Tap to stop recording",
            style = typography.bodyMedium,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun ProcessingScene(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(84.dp),
            color = colors.primary,
            strokeWidth = 6.dp
        )
        Text(
            text = "Processing...",
            style = typography.titleLarge,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Analyzing your pronunciation",
            style = typography.bodyMedium,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ResultScene(
    speechEntity: SpeechEntity?,
    phrase: AudioTranscription,
    uiState: SpeechUiState,
    onRetry: () -> Unit,
    onPlay: () -> Unit,
    onListen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (speechEntity == null) return

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val score = uiState.result.second

    val scoreColor = when {
        score >= 90 -> Color(0xFF2E7D32)
        score >= 80 -> Color(0xFF66BB6A)
        score >= 70 -> Color(0xFF4CAF50)
        score >= 60 -> Color(0xFFFFA726)
        score >= 40 -> Color(0xFFFF9800)
        else -> Color(0xFFEF5350)
    }

    val feedbackMessage = when {
        score >= 90 -> "Excellent!"
        score >= 80 -> "Great job!"
        score >= 70 -> "Good work!"
        score >= 60 -> "Not bad! Keep practicing"
        score >= 40 -> "Keep trying!"
        else -> "Try again! Practice makes perfect"
    }

    val expectedWords = phrase.text
        .lowercase()
        .replace(Regex("[,.:;!?\"'-]"), "")
        .split(Regex("\\s+"))

    val userWords = uiState.result.first
        .lowercase()
        .replace(Regex("[,.:;!?\"'-]"), "")
        .split(Regex("\\s+"))

    val annotatedString = buildAnnotatedString {
        expectedWords.forEachIndexed { index, expectedWord ->
            val userWord = userWords.getOrNull(index) ?: ""

            val similarity = if (expectedWord == userWord) {
                1f
            } else {
                calculateSimilarity(expectedWord, userWord)
            }

            val wordColor = when {
                similarity >= 0.95f -> Color(0xFF2E7D32)
                similarity >= 0.80f -> Color(0xFF66BB6A)
                similarity >= 0.60f -> Color(0xFFFFA726)
                else -> Color(0xFFEF5350)
            }

            withStyle(
                style = SpanStyle(
                    color = wordColor,
                    fontWeight = if (similarity < 0.8f) FontWeight.Bold else FontWeight.Normal
                )
            ) {
                append(expectedWord)
            }

            if (index < expectedWords.size - 1) {
                append(" ")
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Your result",
            style = typography.titleLarge,
            color = colors.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$score%",
            style = typography.displayLarge.copy(fontSize = 72.sp),
            color = scoreColor,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = feedbackMessage,
            style = typography.titleMedium,
            color = colors.onSecondaryContainer,
            textAlign = TextAlign.Center
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colors.secondary
            ),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clickable(onClick = onPlay)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = annotatedString,
                    style = typography.titleLarge,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondary
                ),
                modifier = Modifier
                    .weight(.5f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rotate_left),
                        contentDescription = null,
                        tint = colors.onSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Try Again",
                        style = typography.titleMedium,
                        color = colors.onSecondary
                    )
                }
            }

            Button(
                onClick = { onListen(speechEntity.speechPath) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                ),
                modifier = Modifier
                    .weight(.5f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.headphones),
                        contentDescription = null,
                        tint = colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Listen",
                        style = typography.titleMedium,
                        color = colors.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeechComponent(
    scene: Scene,
    onToggle: @Composable () -> Unit,
    onRecording: @Composable () -> Unit,
    onProcessing: @Composable () -> Unit,
    onResult: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (scene) {
            Scene.TOGGLE -> onToggle.invoke()
            Scene.RECORDING -> onRecording.invoke()
            Scene.PROCESSING -> onProcessing.invoke()
            Scene.RESULT -> onResult.invoke()
        }
    }
}

@Composable
fun AudioWaveform(amplitudes: List<Float>, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val colors = MaterialTheme.colorScheme
    val barWidth = 3.dp
    val barSpacing = 2.dp

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val centerY = size.height / 2
        val totalBarWidth = (barWidth + barSpacing).toPx()
        val visibleBars = (size.width / totalBarWidth).toInt().coerceAtMost(50)
        val displayAmplitudes = amplitudes.takeLast(visibleBars)
        val totalUsedWidth = displayAmplitudes.size * totalBarWidth
        val startX = (size.width - totalUsedWidth) / 2

        displayAmplitudes.forEachIndexed { index, amplitude ->
            val normalizedAmplitude = amplitude.coerceIn(0f, 1f)
            val barHeight = lerp(
                4.dp.toPx(),
                60.dp.toPx(),
                normalizedAmplitude
            )
            val xPosition = startX + (index * totalBarWidth)
            val yPosition = centerY - (barHeight / 2)
            val alpha = if (index >= displayAmplitudes.size - 5) {
                pulseAlpha
            } else {
                1f
            }

            drawRoundRect(
                color = colors.primary.copy(alpha = alpha),
                topLeft = Offset(xPosition, yPosition),
                size = Size(barWidth.toPx(), barHeight),
                cornerRadius = CornerRadius(barWidth.toPx() / 2)
            )
        }
    }
}

private fun calculateSimilarity(word1: String, word2: String): Float {
    if (word1 == word2) return 1f

    val distance = levenshteinDistance(word1, word2)
    val maxLength = maxOf(word1.length, word2.length)

    if (maxLength == 0) return 1f

    return 1f - (distance.toFloat() / maxLength)
}

private fun levenshteinDistance(s1: String, s2: String): Int {
    val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

    for (i in 0..s1.length) dp[i][0] = i
    for (j in 0..s2.length) dp[0][j] = j

    for (i in 1..s1.length) {
        for (j in 1..s2.length) {
            val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + cost
            )
        }
    }

    return dp[s1.length][s2.length]
}

@Composable
fun RequestAudioPermission(onPermissionGrant: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGrant()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true)
private fun SpeechPreview() {
    AppTheme {
        ResultScene(
            speechEntity = null,
            phrase = AudioTranscription(
                text = "natural, and easy to understand everyday English conversations." +
                    " Today, I am joined by my co-host",
                start = 0L,
                end = 0L
            ),
            uiState = SpeechUiState(),
            onRetry = {},
            onPlay = {},
            onListen = {}
        )
    }
}
