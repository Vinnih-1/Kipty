package io.github.vinnih.kipty.ui.home

interface HomeController {

    fun openNotificationSettings()

    suspend fun populateDatabase(onSuccess: () -> Unit)
}
