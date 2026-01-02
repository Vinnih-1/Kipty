package io.github.vinnih.kipty.ui.home

interface HomeController {

    fun openNotificationSettings()

    suspend fun createDefault(data: suspend (String, String, String, String) -> Unit)
}
