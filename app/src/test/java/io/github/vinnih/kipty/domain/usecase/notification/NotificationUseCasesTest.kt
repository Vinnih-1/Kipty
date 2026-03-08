package io.github.vinnih.kipty.domain.usecase.notification

import app.cash.turbine.test
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.domain.repository.NotificationRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationUseCasesTest {

    private val repository = mockk<NotificationRepository>(relaxed = true)

    private fun buildNotification(uid: Int = 1, read: Boolean = false) = NotificationEntity(
        uid = uid,
        title = "Transcription Done",
        content = "Your audio has been transcribed",
        read = read,
        audioId = 10,
        audioName = "My Audio",
        channel = NotificationCategory.TRANSCRIPTION_DONE,
        createdAt = "2024-01-01T10:00:00"
    )

    private fun buildAudio() = AudioEntity(
        uid = 10,
        name = "My Audio",
        createdAt = "2024-01-01",
        audioPath = "path/audio.opus",
        imagePath = "path/image.jpg",
        isDefault = false,
        duration = 5000L,
        audioSize = 1024L,
        state = TranscriptionState.TRANSCRIBED
    )

    // --- DeleteNotificationUseCase ---

    @Test
    fun `DeleteNotificationUseCase should call repository delete`() = runTest {
        val notification = buildNotification()
        val useCase = DeleteNotificationUseCase(repository)

        useCase(notification)

        coVerify { repository.delete(notification) }
    }

    // --- ReadNotificationUseCase ---

    @Test
    fun `ReadNotificationUseCase should call repository read`() = runTest {
        val notification = buildNotification()
        val useCase = ReadNotificationUseCase(repository)

        useCase(notification)

        coVerify { repository.read(notification) }
    }

    // --- SaveNotificationUseCase ---

    @Test
    fun `SaveNotificationUseCase should build entity with correct fields and save`() = runTest {
        val audio = buildAudio()
        val useCase = SaveNotificationUseCase(repository)

        useCase(
            audioEntity = audio,
            title = "Transcription Done",
            content = "Done!",
            channel = NotificationCategory.TRANSCRIPTION_DONE
        )

        coVerify {
            repository.save(
                match {
                    it.title == "Transcription Done" &&
                        it.content == "Done!" &&
                        it.audioId == audio.uid &&
                        it.audioName == audio.name &&
                        it.channel == NotificationCategory.TRANSCRIPTION_DONE &&
                        !it.read
                }
            )
        }
    }

    // --- GetTodayNotificationsUseCase ---

    @Test
    fun `GetTodayNotificationsUseCase should return flow from repository`() = runTest {
        val notifications = listOf(buildNotification(1), buildNotification(2))
        every { repository.getToday() } returns flowOf(notifications)
        val useCase = GetTodayNotificationsUseCase(repository)

        useCase().test {
            assertEquals(notifications, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- GetYesterdayNotificationsUseCase ---

    @Test
    fun `GetYesterdayNotificationsUseCase should return flow from repository`() = runTest {
        val notifications = listOf(buildNotification(3))
        every { repository.getYesterday() } returns flowOf(notifications)
        val useCase = GetYesterdayNotificationsUseCase(repository)

        useCase().test {
            assertEquals(notifications, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- GetEarlierNotificationsUseCase ---

    @Test
    fun `GetEarlierNotificationsUseCase should return flow from repository`() = runTest {
        val notifications = listOf(buildNotification(4), buildNotification(5))
        every { repository.getEarlier() } returns flowOf(notifications)
        val useCase = GetEarlierNotificationsUseCase(repository)

        useCase().test {
            assertEquals(notifications, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- GetUnreadNotificationsUseCase ---

    @Test
    fun `GetUnreadNotificationsUseCase should return flow from repository`() = runTest {
        val notifications = listOf(buildNotification(6, read = false))
        every { repository.getAllUnread() } returns flowOf(notifications)
        val useCase = GetUnreadNotificationsUseCase(repository)

        useCase().test {
            assertEquals(notifications, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetUnreadNotificationsUseCase should emit empty list when all are read`() = runTest {
        every { repository.getAllUnread() } returns flowOf(emptyList())
        val useCase = GetUnreadNotificationsUseCase(repository)

        useCase().test {
            assertEquals(emptyList<NotificationEntity>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
