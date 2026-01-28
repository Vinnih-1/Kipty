package io.github.vinnih.kipty.ui.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.vinnih.kipty.ui.create.BottomButton
import io.github.vinnih.kipty.ui.create.CreateTopBar
import io.github.vinnih.kipty.ui.create.DetailsStepScreen
import io.github.vinnih.kipty.ui.create.ImageStepScreen
import io.github.vinnih.kipty.ui.create.ProgressStepSection
import io.github.vinnih.kipty.ui.create.Step
import io.github.vinnih.kipty.ui.theme.AppTheme
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun EditScreen(
    editController: EditController,
    id: Int,
    step: Step,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by editController.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        editController.retrieveData(id)
    }

    Scaffold(
        topBar = {
            CreateTopBar(
                step = step,
                onBack = onBack,
                onPrevious = onBack,
                modifier = Modifier
            )
        },
        bottomBar = {
            BottomButton(
                onNext = {
                    editController.completeEdit(onSuccess = { onBack.invoke() })
                },
                onCreate = {
                    editController.completeEdit(onSuccess = { onBack.invoke() })
                },
                step = step,
                enabled = uiState.title.isNotEmpty()
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
                currentStep = step,
                modifier = Modifier
            )
            if (step == Step.DETAILS) {
                DetailsStepScreen(
                    name = uiState.title,
                    onTitleChange = { editController.editTitle(it) },
                    defaultTitle = uiState.title,
                    defaultDescription = uiState.description,
                    onDescriptionChange = { editController.editDescription(it) }
                )
            } else {
                ImageStepScreen(
                    file = uiState.imageFile,
                    onFileSelect = { editController.editImage(it) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditScreenPreviewDetails() {
    AppTheme {
        EditScreen(
            editController = object : EditController {
                private val _uiState = MutableStateFlow(
                    EditUiState(
                        title = "My recording",
                        description = "A short description of my recording.",
                        imageFile = null
                    )
                )
                override val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

                override fun retrieveData(id: Int) {}
                override fun editTitle(title: String) {}
                override fun editDescription(description: String) {}
                override fun editImage(image: File?) {}
                override fun completeEdit(onSuccess: () -> Unit) {}
                override fun clearUiState() {}
            },
            id = 1,
            step = Step.DETAILS,
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditScreenPreviewImage() {
    AppTheme {
        EditScreen(
            editController = object : EditController {
                private val _uiState = MutableStateFlow(
                    EditUiState(
                        title = "My recording",
                        description = "A short description of my recording.",
                        imageFile = null
                    )
                )
                override val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

                override fun retrieveData(id: Int) {}
                override fun editTitle(title: String) {}
                override fun editDescription(description: String) {}
                override fun editImage(image: File?) {}
                override fun completeEdit(onSuccess: () -> Unit) {}
                override fun clearUiState() {}
            },
            id = 1,
            step = Step.IMAGE,
            onBack = {}
        )
    }
}
