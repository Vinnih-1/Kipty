package io.github.vinnih.kipty.data.application

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ApplicationData(val selectedModel: String, val defaultSamplesLoaded: Boolean)

class AppConfig(context: Context) {
    val file = context.filesDir.resolve("config.json").also {
        if (!it.exists()) {
            it.createNewFile()
            it.writeText(Json.encodeToString(ApplicationData("", false)))
        }
    }

    fun read(): ApplicationData = Json.decodeFromString<ApplicationData>(file.readText())

    fun write(data: ApplicationData) = file.writeText(Json.encodeToString(data))
}
