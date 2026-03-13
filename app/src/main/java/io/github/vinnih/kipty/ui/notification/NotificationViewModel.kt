package io.github.vinnih.kipty.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.domain.usecase.notification.DeleteNotificationUseCase
import io.github.vinnih.kipty.domain.usecase.notification.GetEarlierNotificationsUseCase
import io.github.vinnih.kipty.domain.usecase.notification.GetTodayNotificationsUseCase
import io.github.vinnih.kipty.domain.usecase.notification.GetUnreadNotificationsUseCase
import io.github.vinnih.kipty.domain.usecase.notification.GetYesterdayNotificationsUseCase
import io.github.vinnih.kipty.domain.usecase.notification.ReadNotificationUseCase
import io.github.vinnih.kipty.domain.usecase.notification.SaveNotificationUseCase
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
class NotificationViewModel @Inject constructor(
    getTodayNotificationsUseCase: GetTodayNotificationsUseCase,
    getYesterdayNotificationsUseCase: GetYesterdayNotificationsUseCase,
    getEarlierNotificationsUseCase: GetEarlierNotificationsUseCase,
    getUnreadNotificationsUseCase: GetUnreadNotificationsUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val readNotificationUseCase: ReadNotificationUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase
) : ViewModel(),
    NotificationController {

    val today: StateFlow<List<NotificationEntity>> = getTodayNotificationsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val yesterday: StateFlow<List<NotificationEntity>> = getYesterdayNotificationsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val earlier: StateFlow<List<NotificationEntity>> = getEarlierNotificationsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unread: StateFlow<List<NotificationEntity>> = getUnreadNotificationsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override val uiState: StateFlow<NotificationUiState> =
        combine(today, yesterday, earlier, unread) { today, yesterday, earlier, unread ->
            NotificationUiState(today, yesterday, earlier, unread)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationUiState())

    override fun notify(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationCategory
    ) {
        viewModelScope.launch {
            saveNotificationUseCase(audioEntity, title, content, channel)
        }
    }

    override fun read(notificationEntity: NotificationEntity) {
        viewModelScope.launch {
            readNotificationUseCase(notificationEntity)
        }
    }

    override fun delete(notificationEntity: NotificationEntity) {
        viewModelScope.launch {
            deleteNotificationUseCase(notificationEntity)
        }
    }
}
