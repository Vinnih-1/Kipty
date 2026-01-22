package io.github.vinnih.kipty.ui.create

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.AppWarn
import io.github.vinnih.kipty.ui.components.BaseButton
import io.github.vinnih.kipty.ui.components.WarnType
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.utils.dashedBorder
import io.github.vinnih.kipty.utils.getFileName
import io.github.vinnih.kipty.utils.getFileSize
import io.github.vinnih.kipty.utils.getFormattedSize
import io.github.vinnih.kipty.utils.processUriToFile
import java.io.File

@Composable
fun CreateScreen(
    createController: CreateController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by createController.uiState.collectAsState()
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            createController.clearUiState()
        }
    }

    Scaffold(
        topBar = {
            CreateTopBar(
                step = uiState.step,
                onBack = onBack,
                onPrevious = { createController.previousStep() },
                modifier = Modifier
            )
        },
        bottomBar = {
            BottomButton(
                onNext = { createController.nextStep() },
                onCreate = {
                    createController.createAudio()
                    onBack.invoke()
                },
                step = uiState.step,
                enabled = uiState.data.audioUri != null
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ProgressStepSection(
                currentStep = uiState.step,
                modifier = Modifier
            )
            ProcessingInformationNote()
            when (uiState.step) {
                Step.FILE -> AudioStepScreen(
                    uri = uiState.data.audioUri,
                    onUriSelect = { createController.selectAudio(it) },
                    modifier = Modifier
                )

                Step.DETAILS -> DetailsStepScreen(
                    name = uiState.data.audioUri!!.getFileName(context),
                    defaultTitle = uiState.data.title,
                    defaultDescription = uiState.data.description,
                    onTitleChange = { createController.insertTitle(it) },
                    onDescriptionChange = { createController.insertDescription(it) }
                )

                Step.IMAGE -> ImageStepScreen(
                    file = uiState.data.imageFile,
                    onFileSelect = { createController.selectImage(it) }
                )

                Step.REVIEW -> ReviewStepScreen(
                    audioUri = uiState.data.audioUri!!,
                    imageFile = uiState.data.imageFile,
                    title = uiState.data.title,
                    description = uiState.data.description
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTopBar(
    step: Step,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "New Transcription",
                        style = typography.titleLarge,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Step ${step.ordinal + 1} of ${Step.entries.size}",
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Light
                    )
                }
            },
            navigationIcon = {
                BaseButton(onClick = {
                    if (step == Step.FILE) {
                        onBack()
                    } else {
                        onPrevious()
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = null,
                        tint = colors.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            expandedHeight = 100.dp
        )
        HorizontalDivider()
    }
}

@Composable
fun ProgressStepSection(currentStep: Step, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val isSelectedItem: (Step) -> Boolean = { currentStep == it }

    @Composable
    fun IconIndicator(@DrawableRes id: Int, step: Step, selected: Boolean, divider: Boolean) {
        val done = currentStep.ordinal > step.ordinal

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(64.dp)
                        .border(
                            border = if (selected && !done) {
                                BorderStroke(2.dp, colors.primaryContainer)
                            } else {
                                BorderStroke(0.dp, Color.Transparent)
                            },
                            shape = CircleShape
                        )
                        .background(
                            color = if (done) {
                                colors.primaryContainer.copy(alpha = .7f)
                            } else {
                                colors.secondaryContainer.copy(.4f)
                            }
                        )
                ) {
                    if (done) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).align(Alignment.Center),
                            tint = colors.onPrimaryContainer
                        )
                    } else {
                        Icon(
                            painter = painterResource(id),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).align(Alignment.Center),
                            tint = if (selected) {
                                colors.primary
                            } else {
                                colors.onBackground.copy(
                                    alpha = .5f
                                )
                            }
                        )
                    }
                }
                Text(
                    text = step.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) {
                        colors.primary
                    } else {
                        colors.onBackground.copy(
                            alpha = .5f
                        )
                    }
                )
            }
            if (divider) {
                HorizontalDivider(
                    modifier = Modifier.width(36.dp),
                    thickness = 2.dp,
                    color = if (done) {
                        colors.primaryContainer
                    } else {
                        colors.onBackground
                    }
                )
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp)
    ) {
        IconIndicator(
            id = R.drawable.music,
            step = Step.FILE,
            selected = isSelectedItem(Step.FILE),
            divider = true
        )
        IconIndicator(
            id = R.drawable.file_text,
            step = Step.DETAILS,
            selected = isSelectedItem(Step.DETAILS),
            divider = true
        )
        IconIndicator(
            id = R.drawable.image,
            step = Step.IMAGE,
            selected = isSelectedItem(Step.IMAGE),
            divider = true
        )
        IconIndicator(
            id = R.drawable.check,
            step = Step.REVIEW,
            selected = isSelectedItem(Step.REVIEW),
            divider = false
        )
    }
}

@Suppress("ktlint:standard:max-line-length")
@Composable
private fun ProcessingInformationNote(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var showWarn by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Button(
            onClick = { showWarn = !showWarn },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.surfaceContainerHigh
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().weight(.9f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = null,
                    tint = colors.onBackground,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Processing information",
                    style = typography.titleSmall,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Light
                )
            }
            Icon(
                painter = painterResource(R.drawable.arrow_drop_down),
                contentDescription = null,
                tint = colors.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
        AnimatedVisibility(showWarn) {
            AppWarn(
                warnType = WarnType.Error,
                icon = {},
                content = {
                    Text(
                        text = "This may take some time.",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This task may take some time to be processed depending on your device.",
                        style = typography.bodySmall,
                        fontWeight = FontWeight.Light
                    )
                },
                dismiss = {},
                modifier = Modifier.height(84.dp).padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun AudioStepScreen(uri: Uri?, onUriSelect: (Uri?) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val rememberAudioPicker = rememberAudioPicker(
        mimeType = "audio/*",
        extra = arrayOf(
            "audio/mpeg",
            "audio/wav",
            "audio/x-wav",
            "audio/wave"
        )
    ) {
        onUriSelect(it)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 6.dp)
        ) {
            Text(
                text = "Select audio file",
                style = typography.headlineLarge,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose the podcast episode you want to transcribe",
                style = typography.titleSmall,
                color = colors.onBackground,
                fontWeight = FontWeight.Light
            )
        }

        if (uri == null) {
            OutlinedIconButton(
                border = BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                onClick = { rememberAudioPicker.invoke() },
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = 24.dp
                ).height(256.dp).dashedBorder(
                    color = colors.secondary.copy(alpha = .4f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                            .background(colors.secondaryContainer.copy(alpha = .4f))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music),
                            contentDescription = "Audio file icon",
                            tint = colors.onSecondaryContainer.copy(.8f),
                            modifier = Modifier.size(56.dp).align(Alignment.Center)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Tap to select audio",
                            style = typography.titleMedium,
                            color = colors.onBackground
                        )
                        Text(
                            text = "MP3, WAV M4A supported",
                            style = typography.titleSmall,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        } else {
            Card(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .size(64.dp)
                            .background(colors.secondaryContainer.copy(.4f))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music),
                            contentDescription = null,
                            tint = colors.onSecondaryContainer.copy(.8f),
                            modifier = Modifier.size(36.dp).align(Alignment.Center)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(.8f)
                    ) {
                        Text(
                            text = uri.getFileName(context),
                            style = typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = uri.getFileSize(context).getFormattedSize(),
                            style = typography.titleSmall,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Light
                        )
                    }
                    BaseButton(onClick = { onUriSelect(null) }) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null,
                            tint = colors.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsStepScreen(
    name: String,
    modifier: Modifier = Modifier,
    defaultTitle: String,
    defaultDescription: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var titleState by remember(defaultTitle) { mutableStateOf(defaultTitle) }
    var descriptionState by remember(defaultDescription) { mutableStateOf(defaultDescription) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 6.dp)
        ) {
            Text(
                text = "Add details",
                style = typography.headlineLarge,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Give your transcription a name and description",
                style = typography.titleSmall,
                color = colors.onBackground,
                fontWeight = FontWeight.Light
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            TextField(
                value = titleState,
                onValueChange = {
                    onTitleChange(it)
                    titleState = it
                },
                label = {
                    Text(
                        text = "Title (Optional)",
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                placeholder = {
                    Text(
                        text = "e.g., $name",
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Light
                    )
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = descriptionState,
                onValueChange = {
                    onDescriptionChange(it)
                    descriptionState = it
                },
                label = {
                    Text(
                        text = "Description (Optional)",
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                placeholder = {
                    Text(
                        text = "Add a brief description of this episode...",
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Light
                    )
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().height(148.dp)
            )
        }
    }
}

@Composable
fun ImageStepScreen(file: File?, onFileSelect: (File?) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val rememberAudioPicker = rememberAudioPicker(
        mimeType = "image/*",
        arrayOf("image/png", "image/jpeg")
    ) {
        onFileSelect(it.processUriToFile(context))
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 6.dp)
        ) {
            Text(
                text = "Add cover image",
                style = typography.headlineLarge,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Optional: Add a thumbnail for this episode",
                style = typography.titleSmall,
                color = colors.onBackground,
                fontWeight = FontWeight.Light
            )
        }

        if (file == null) {
            OutlinedIconButton(
                border = BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                onClick = { rememberAudioPicker.invoke() },
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = 24.dp
                ).height(256.dp).dashedBorder(
                    color = colors.secondary.copy(alpha = .4f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                            .background(colors.secondaryContainer.copy(alpha = .4f))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image),
                            contentDescription = "Audio file icon",
                            tint = colors.onSecondaryContainer.copy(.8f),
                            modifier = Modifier.size(56.dp).align(Alignment.Center)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Tap to select image",
                            style = typography.titleMedium,
                            color = colors.onBackground
                        )
                        Text(
                            text = "JPG, PNG supported",
                            style = typography.titleSmall,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        } else {
            Card(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .size(64.dp)
                            .background(colors.secondaryContainer.copy(.4f))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image),
                            contentDescription = null,
                            tint = colors.onSecondaryContainer.copy(.8f),
                            modifier = Modifier.size(36.dp).align(Alignment.Center)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(.8f)
                    ) {
                        Text(
                            text = file.nameWithoutExtension,
                            style = typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = file.length().getFormattedSize(),
                            style = typography.titleSmall,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Light
                        )
                    }
                    BaseButton(onClick = { onFileSelect(null) }) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null,
                            tint = colors.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewStepScreen(
    audioUri: Uri,
    imageFile: File?,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    @Composable
    fun CardDetails(@DrawableRes icon: Int, title: String, content: @Composable () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = colors.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = title,
                        style = typography.bodyMedium,
                        color = colors.onBackground.copy(.7f),
                        fontWeight = FontWeight.Bold
                    )
                }
                content()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 6.dp)
        ) {
            Text(
                text = "Review & create",
                style = typography.headlineLarge,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Check your details before creating the transcription",
                style = typography.titleSmall,
                color = colors.onBackground,
                fontWeight = FontWeight.Light
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            CardDetails(R.drawable.music, "Audio File") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = audioUri.getFileName(context),
                        style = typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = audioUri.getFileSize(context).getFormattedSize(),
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            CardDetails(R.drawable.file_text, "Details") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = title,
                        style = typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = description,
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            CardDetails(R.drawable.image, "Cover Image") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = imageFile?.nameWithoutExtension ?: "No cover image selected",
                        style = typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontStyle = if (imageFile != null) FontStyle.Normal else FontStyle.Italic
                    )
                    Text(
                        text = imageFile?.length()?.getFormattedSize() ?: "",
                        style = typography.titleSmall,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberAudioPicker(
    mimeType: String,
    extra: Array<String>,
    onUriSelect: (Uri) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        onUriSelect(uri)
    }

    return {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_MIME_TYPES, extra)
        }
        launcher.launch(intent)
    }
}

@Composable
fun BottomButton(
    onNext: () -> Unit,
    onCreate: () -> Unit,
    step: Step,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    HorizontalDivider()
    Box(
        modifier = modifier
            .background(colors.surfaceContainer)
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .height(110.dp)
    ) {
        Button(
            onClick = {
                if (step == Step.REVIEW) {
                    onCreate()
                } else {
                    onNext()
                }
            },
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primaryContainer,
                disabledContentColor = colors.onPrimaryContainer.copy(alpha = .5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = if (Step.REVIEW == step) "Create Transcription" else "Continue",
                style = typography.titleLarge,
                color = if (enabled) {
                    colors.onPrimaryContainer
                } else {
                    colors.onPrimaryContainer.copy(alpha = .3f)
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    device = "spec:parent=pixel_5,navigation=buttons"
)
@Composable
private fun CreateScreenPreview() {
    AppTheme {
        CreateScreen(createController = FakeCreateViewModel(), onBack = {})
    }
}
