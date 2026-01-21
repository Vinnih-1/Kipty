package io.github.vinnih.kipty.ui.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FakeHomeViewModel : HomeController {
    override val homeUiState: StateFlow<HomeUiState>
        get() = TODO("Not yet implemented")

    override fun getPlayTimeById(id: Int): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun loadAudios() {
        TODO("Not yet implemented")
    }

    override fun openNotificationSettings() {
        TODO("Not yet implemented")
    }

    override suspend fun populateDatabase(onSuccess: () -> Unit) {
        TODO("Not yet implemented")
    }
}
