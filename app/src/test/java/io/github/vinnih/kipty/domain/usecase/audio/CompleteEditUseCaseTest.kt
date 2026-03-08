package io.github.vinnih.kipty.domain.usecase.audio

import android.content.Context
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.domain.repository.AudioRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CompleteEditUseCaseTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val repository = mockk<AudioRepository>(relaxed = true)
    private val context = mockk<Context>()
    private lateinit var filesDir: File
    private lateinit var useCase: CompleteEditUseCase

    @Before
    fun setup() {
        filesDir = tmpFolder.newFolder("files")
        every { context.filesDir } returns filesDir
        useCase = CompleteEditUseCase(context, repository)
    }

    private fun buildAudio(audioPath: String) = AudioEntity(
        uid = 1,
        name = "Old Title",
        description = "Old Description",
        createdAt = "2024-01-01",
        audioPath = audioPath,
        imagePath = "old/image.jpg",
        isDefault = false,
        duration = 5000L,
        audioSize = 1024L,
        state = TranscriptionState.TRANSCRIBED
    )

    @Test
    fun `given new image file, should copy image to transcription folder and save`() = runTest {
        val audioDir = tmpFolder.newFolder("transcription_folder")
        val audioFile = File(audioDir, "audio.opus").apply { createNewFile() }
        val imageFile = tmpFolder.newFile("new_cover.jpg").apply { writeText("img") }
        val audio = buildAudio(audioFile.absolutePath)

        useCase(audio, "New Title", "New Desc", imageFile)

        val expectedImagePath = File(audioDir, imageFile.name).absolutePath
        coVerify {
            repository.save(
                match {
                    it.name == "New Title" &&
                        it.description == "New Desc" &&
                        it.imagePath == expectedImagePath
                }
            )
        }
    }

    @Test
    fun `given null image file, should use default icon from filesDir`() = runTest {
        val audioDir = tmpFolder.newFolder("transcription_folder2")
        val audioFile = File(audioDir, "audio.opus").apply { createNewFile() }
        val defaultIcon = File(filesDir, "default-icon.png").apply { createNewFile() }
        val audio = buildAudio(audioFile.absolutePath)

        useCase(audio, "Title", "Desc", null)

        coVerify {
            repository.save(
                match { it.imagePath.contains("default-icon.png") }
            )
        }
    }

    @Test
    fun `given image already in transcription folder, should not copy and use same path`() = runTest {
        val audioDir = tmpFolder.newFolder("transcription_folder3")
        val audioFile = File(audioDir, "audio.opus").apply { createNewFile() }
        val imageAlreadyThere = File(audioDir, "cover.jpg").apply { createNewFile() }
        val audio = buildAudio(audioFile.absolutePath)

        useCase(audio, "Title", "Desc", imageAlreadyThere)

        coVerify {
            repository.save(
                match { it.imagePath == imageAlreadyThere.absolutePath }
            )
        }
    }
}
