package io.github.vinnih.kipty.data.database.repository.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getAll(): Flow<List<AudioEntity>>

    fun getById(id: Int): Flow<AudioEntity?>

    suspend fun save(audio: AudioEntity): Long

    suspend fun delete(audio: AudioEntity)
}
