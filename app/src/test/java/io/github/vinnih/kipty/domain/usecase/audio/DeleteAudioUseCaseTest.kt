package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.domain.repository.AudioRepository
import io.mockk.coVerify
import io.mockk.mockk
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DeleteAudioUseCaseTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val repository = mockk<AudioRepository>(relaxed = true)
    private val useCase = DeleteAudioUseCase(repository)

    private fun buildAudio(isDefault: Boolean, audioPath: String) = AudioEntity(
        uid = 1,
        name = "Test",
        createdAt = "2024-01-01",
        audioPath = audioPath,
        imagePath = "",
        isDefault = isDefault,
        duration = 1000L,
        audioSize = 512L,
        state = TranscriptionState.NONE
    )

    @Test
    fun `given any audio, should call repository delete`() = runTest {
        val audio = buildAudio(isDefault = true, audioPath = "samples/test/audio.opus")

        useCase(audio)
        coVerify { repository.delete(audio) }
    }

    @Test
    fun `given non-default audio, should delete parent directory`() = runTest {
        val audioDir = tmpFolder.newFolder("my_audio")
        val audioFile = File(audioDir, "audio.opus").apply { createNewFile() }
        val audio = buildAudio(isDefault = false, audioPath = audioFile.absolutePath)

        useCase(audio)
        assert(!audioDir.exists()) { "Expected directory to be deleted" }
    }

    @Test
    fun `given default audio, should NOT delete any directory`() = runTest {
        val audioDir = tmpFolder.newFolder("default_audio")
        val audioFile = File(audioDir, "audio.opus").apply { createNewFile() }
        val audio = buildAudio(isDefault = true, audioPath = audioFile.absolutePath)

        useCase(audio)
        assert(audioDir.exists()) { "Expected directory to remain intact for default audio" }
    }
}
