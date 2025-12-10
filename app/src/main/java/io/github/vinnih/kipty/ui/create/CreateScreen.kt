package io.github.vinnih.kipty.ui.create

import android.content.Intent
import android.content.res.Configuration
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.vinnih.androidtranscoder.utils.toWavReader
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.CreateAudioButton
import io.github.vinnih.kipty.ui.home.HomeController
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.dashedBorder
import java.io.File
import kotlinx.coroutines.launch

private enum class Stage {
    FILE,
    NAME,
    DESCRIPTION
}

private data class AudioCreator(
    val file: File? = null,
    val name: String? = null,
    val description: String? = null
)

@Composable
fun CreateScreen(
    onBack: () -> Unit,
    homeController: HomeController,
    modifier: Modifier = Modifier
) {
    var audio by remember { mutableStateOf(AudioCreator()) }
    var stage by remember { mutableStateOf(Stage.FILE) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    when (stage) {
        Stage.FILE -> AudioFileStage(modifier = modifier, onComplete = {
            stage = Stage.NAME
            audio = audio.copy(file = it)
        })

        Stage.NAME -> AudioNameStage(modifier = modifier, onComplete = {
            stage = Stage.DESCRIPTION
            audio = audio.copy(name = it)
        })

        Stage.DESCRIPTION -> AudioDescriptionStage(modifier = modifier, onComplete = {
            audio = audio.copy(description = it)
            scope.launch {
                homeController.createAudio(
                    audio.file!!.toWavReader(context.cacheDir),
                    audio.name?.ifEmpty { null },
                    audio.description
                )
                onBack.invoke()
            }
        })
    }
}

@Composable
private fun BasicCreateScreen(
    onNext: () -> Unit,
    stage: Stage,
    composition: LottieComposition?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1.0f
    )

    Box(
        modifier = modifier.fillMaxSize().padding(
            start = 48.dp,
            end = 48.dp,
            top = 100.dp,
            bottom = 100.dp
        )
    ) {
        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            content.invoke()
        }
        CreateAudioButton(
            text = if (stage !=
                Stage.DESCRIPTION
            ) {
                "Next"
            } else {
                "Create"
            },
            enabled = enabled,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AudioFileStage(onComplete: (File) -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/speaker-icon.json")
    )
    var selectedFile by remember { mutableStateOf<File?>(null) }
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "audio/*"
    }
    val context = LocalContext.current
    val audioPickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            val name = context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            ).use {
                if (it == null) return@use null

                it.moveToFirst()
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (index != -1) {
                    return@use it.getString(index)
                } else {
                    return@use null
                }
            }

            context.contentResolver.openInputStream(uri).use { input ->
                if (input == null || name == null) return@use

                val file = File(context.cacheDir, name)
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
                selectedFile = file
            }
        }

    BasicCreateScreen(composition = composition, enabled = selectedFile != null, content = {
        OutlinedIconButton(
            border = BorderStroke(0.dp, Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            onClick = { audioPickerLauncher.launch(intent) },
            modifier = Modifier.fillMaxWidth().height(128.dp).dashedBorder(
                color = colors.primary
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.audio_file),
                contentDescription = "Audio file icon",
                tint = colors.primary,
                modifier = Modifier.size(72.dp)
            )
        }
    }, onNext = {
        if (selectedFile != null) onComplete(selectedFile!!)
    }, stage = Stage.FILE, modifier = modifier)
}

@Composable
private fun AudioNameStage(onComplete: (String) -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/creative-idea.json")
    )

    BasicCreateScreen(onNext = {
        onComplete(name)
    }, Stage.NAME, modifier = modifier, composition = composition) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("My audio example") },
            supportingText = { Text("Audio name") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.audio_file),
                    contentDescription = "Audio file icon",
                    modifier = Modifier.size(38.dp)
                )
            }
        )
    }
}

@Composable
private fun AudioDescriptionStage(onComplete: (String) -> Unit, modifier: Modifier = Modifier) {
    var description by remember { mutableStateOf("") }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/creative-idea.json")
    )

    BasicCreateScreen(onNext = {
        onComplete(description)
    }, Stage.DESCRIPTION, modifier = modifier, composition = composition) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("My audio description example") },
            supportingText = { Text("Audio description") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.description),
                    contentDescription = "Audio description icon",
                    modifier = Modifier.size(38.dp)
                )
            }
        )
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
private fun CreateAudioFileStagePreview() {
    AppTheme {
        AudioFileStage(onComplete = {})
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
private fun CreateAudioNameStagePreview() {
    AppTheme {
        AudioNameStage(onComplete = {})
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
private fun CreateAudioDescriptionStageStagePreview() {
    AppTheme {
        AudioDescriptionStage(onComplete = {})
    }
}
