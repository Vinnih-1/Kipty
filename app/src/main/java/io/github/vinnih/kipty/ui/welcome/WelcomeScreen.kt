package io.github.vinnih.kipty.ui.welcome

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.ui.components.ProfilePicture
import io.github.vinnih.kipty.ui.create.rememberAudioPicker
import io.github.vinnih.kipty.utils.processUriToFile
import java.io.File
import kotlinx.coroutines.launch

enum class WelcomeStep {
    INTRO,
    PROFILE_SETUP,
    ALL_SET
}

@Composable
fun WelcomeScreen(
    welcomeController: WelcomeController,
    onGetStarted: () -> Unit,
    onDatabasePopulated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by welcomeController.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        welcomeController.populateDatabase { onDatabasePopulated() }
    }

    BackHandler(uiState.step.ordinal > 0) {
        welcomeController.previousStep()
    }

    Scaffold { paddingValues ->
        AnimatedContent(
            targetState = uiState.step,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it } + fadeOut())
            },
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            label = "WelcomeStepTransition"
        ) { step ->
            when (step) {
                WelcomeStep.INTRO -> IntroStepScreen(
                    onNext = { welcomeController.nextStep() }
                )

                WelcomeStep.PROFILE_SETUP -> ProfileSetupStepScreen(
                    username = uiState.username,
                    profileIconPath = uiState.profileIconPath,
                    profileIconUpdatedAt = uiState.profileIconUpdatedAt,
                    onUsernameChange = { welcomeController.updateUsername(it) },
                    onPickPhoto = { welcomeController.updateProfileIcon(it) },
                    onContinue = { welcomeController.nextStep() }
                )

                WelcomeStep.ALL_SET -> AllSetStepScreen(
                    username = uiState.username.ifBlank { "Account User" },
                    profileIconPath = uiState.profileIconPath,
                    profileIconUpdatedAt = uiState.profileIconUpdatedAt,
                    onGoToHome = {
                        scope.launch {
                            welcomeController.saveProfile()
                            onGetStarted()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun IntroStepScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        StepIndicator(currentStep = WelcomeStep.INTRO)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .size(80.dp)
                    .background(colors.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(R.drawable.headphones),
                    contentDescription = null,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = "Kipty",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Welcome to Kipty",
                    style = typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Improve your English by listening to\npodcasts with real-time transcription.",
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.Light,
                    color = colors.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureItem(
                iconRes = R.drawable.headphones,
                title = "Listen to podcasts",
                description = "Import your favorite English podcasts"
            )
            FeatureItem(
                iconRes = R.drawable.file_text,
                title = "Read along",
                description = "Follow the speech with synchronized text"
            )
            FeatureItem(
                iconRes = R.drawable.mic,
                title = "Practice pronunciation",
                description = "Record your voice and compare with the original"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        WelcomeButton(
            text = "Get started",
            onClick = onNext,
            enabled = true,
            showArrow = true
        )
    }
}

@Composable
private fun ProfileSetupStepScreen(
    username: String,
    profileIconPath: String,
    profileIconUpdatedAt: Long,
    onUsernameChange: (String) -> Unit,
    onPickPhoto: (File) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val rememberAudioPicker = rememberAudioPicker(
        mimeType = "image/*",
        arrayOf("image/png", "image/jpeg")
    ) {
        val file = it.processUriToFile(context)
        if (file != null) onPickPhoto(file)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        StepIndicator(currentStep = WelcomeStep.PROFILE_SETUP)
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Set up your profile",
                style = typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Add a photo and your name so you can\npersonalize your experience.",
                style = typography.bodyLarge,
                fontWeight = FontWeight.Light,
                color = colors.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        ProfilePicture(
            iconPath = profileIconPath,
            updatedAt = profileIconUpdatedAt,
            onClick = { rememberAudioPicker.invoke() }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "YOUR NAME",
                style = typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.6f)
            )
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = {
                    Text(
                        text = "Account User",
                        style = typography.bodyLarge,
                        color = colors.onBackground.copy(alpha = 0.4f)
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "This will be displayed on your home screen.",
                style = typography.bodySmall,
                fontWeight = FontWeight.Light,
                color = colors.onBackground.copy(alpha = 0.5f)
            )
        }

        WelcomeButton(
            text = "Continue",
            onClick = onContinue,
            enabled = true,
            showArrow = true
        )
    }
}

@Composable
private fun AllSetStepScreen(
    username: String,
    profileIconPath: String,
    profileIconUpdatedAt: Long,
    onGoToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        StepIndicator(currentStep = WelcomeStep.ALL_SET)
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "You're all set!",
                    style = typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome, ",
                        style = typography.bodyLarge,
                        fontWeight = FontWeight.Light,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = username,
                        style = typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = ". Start exploring podcasts and improve your English.",
                        style = typography.bodyLarge,
                        fontWeight = FontWeight.Light,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        ProfilePreviewCard(
            username = username,
            profileIconPath = profileIconPath,
            profileIconUpdatedAt = profileIconUpdatedAt
        )

        Spacer(modifier = Modifier.height(16.dp))

        WelcomeButton(
            text = "Go to Home",
            onClick = onGoToHome,
            enabled = true,
            showArrow = true
        )
    }
}

@Composable
private fun StepIndicator(currentStep: WelcomeStep, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        WelcomeStep.entries.forEach { step ->
            val isActive = step == currentStep
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .then(if (isActive) Modifier.size(24.dp, 4.dp) else Modifier.size(8.dp, 4.dp))
                    .clip(CircleShape)
                    .background(
                        if (isActive) colors.primary else colors.onBackground.copy(alpha = 0.25f)
                    )
            )
        }
    }
}

@Composable
private fun FeatureItem(
    iconRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(44.dp)
                    .background(colors.primaryContainer.copy(alpha = 0.4f))
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = description,
                    style = typography.bodySmall,
                    fontWeight = FontWeight.Light,
                    color = colors.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProfilePreviewCard(
    username: String,
    profileIconPath: String,
    profileIconUpdatedAt: Long,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            ProfilePicture(
                iconPath = profileIconPath,
                updatedAt = profileIconUpdatedAt,
                showUpdateIcon = false,
                size = 48.dp,
                shape = RoundedCornerShape(12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Welcome",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Light,
                    color = colors.primary
                )
                Text(
                    text = username,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }
        }
    }
}

@Composable
private fun WelcomeButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    showArrow: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primaryContainer,
            disabledContainerColor = colors.surfaceContainerHigh
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) {
                    colors.onPrimaryContainer
                } else {
                    colors.onBackground.copy(
                        alpha = 0.4f
                    )
                }
            )
            if (showArrow) {
                Icon(
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = null,
                    tint = if (enabled) {
                        colors.onPrimaryContainer
                    } else {
                        colors.onBackground.copy(
                            alpha = 0.4f
                        )
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
