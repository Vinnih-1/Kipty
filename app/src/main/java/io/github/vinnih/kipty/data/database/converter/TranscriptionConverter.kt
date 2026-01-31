package io.github.vinnih.kipty.data.database.converter

import androidx.room.TypeConverter
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.serialization.json.Json

class TranscriptionConverter {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromList(list: List<AudioTranscription>?): String? = list?.let { json.encodeToString(it) }

    @TypeConverter
    fun toList(list: String?): List<AudioTranscription>? =
        list?.let { json.decodeFromString<List<AudioTranscription>>(it) }

    @TypeConverter
    fun fromObject(obj: AudioTranscription): String = obj.let { json.encodeToString(it) }

    @TypeConverter
    fun toObject(obj: String): AudioTranscription = json.decodeFromString(obj)
}
