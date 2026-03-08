package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.domain.repository.AudioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SaveAudioUseCaseTest {

    private val repository = mockk<AudioRepository>()
    private val useCase = SaveAudioUseCase(repository)

    private fun buildAudio() = AudioEntity(
        uid = 0,
        name = "My Audio",
        createdAt = "2024-01-01",
        audioPath = "path/audio.opus",
        imagePath = "path/image.jpg",
        isDefault = false,
        duration = 5000L,
        audioSize = 1024L,
        state = TranscriptionState.NONE
    )

    @Test
    fun `should call repository save and return generated id`() = runTest {
        val audio = buildAudio()

        coEvery { repository.save(audio) } returns 42L

        val result = useCase(audio)

        coVerify { repository.save(audio) }
        assertEquals(42L, result)
    }
}
