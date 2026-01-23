package io.github.vinnih.kipty.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.data.database.repository.notification.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotificationUiState(
    val today: List<NotificationEntity> = emptyList(),
    val yesterday: List<NotificationEntity> = emptyList(),
    val earlier: List<NotificationEntity> = emptyList(),
    val unread: List<NotificationEntity> = emptyList()
)

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: NotificationRepository) :
    ViewModel(),
    NotificationController {

    override val uiState: StateFlow<NotificationUiState>
        get() = combine(today, yesterday, earlier, unread) { today, yesterday, earlier, unread ->
            NotificationUiState(today, yesterday, earlier, unread)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationUiState()
        )

    override val today: StateFlow<List<NotificationEntity>> = repository.getToday()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override val yesterday: StateFlow<List<NotificationEntity>> = repository.getYesterday()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override val earlier: StateFlow<List<NotificationEntity>> = repository.getEarlier()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override val unread: StateFlow<List<NotificationEntity>> = repository.getAllUnread()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    override fun notify(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationCategory
    ) {
        val notificationEntity = NotificationEntity(
            title = title,
            content = content,
            audioId = audioEntity.uid,
            audioName = audioEntity.name,
            channel = channel,
            createdAt = LocalDateTime.now().toString()
        )

        viewModelScope.launch {
            repository.save(notificationEntity)
        }
    }

    override fun read(notificationEntity: NotificationEntity) {
        viewModelScope.launch {
            repository.read(notificationEntity)
        }
    }

    override fun delete(notificationEntity: NotificationEntity) {
        viewModelScope.launch {
            repository.delete(notificationEntity)
        }
    }
}
