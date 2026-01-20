package io.github.vinnih.kipty.data.database.repository.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getAllFlow(): Flow<List<AudioEntity>>

    fun getAll(): List<AudioEntity>

    fun getById(id: Int): AudioEntity?

    fun getFlowById(id: Int): Flow<AudioEntity?>

    fun getFlowPlayTimeById(id: Int): Flow<Long>

    suspend fun incrementPlayTime(id: Int)

    suspend fun save(audio: AudioEntity): Long

    suspend fun delete(audio: AudioEntity)
}
