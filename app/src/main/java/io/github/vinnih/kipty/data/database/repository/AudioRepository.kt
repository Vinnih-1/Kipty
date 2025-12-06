package io.github.vinnih.kipty.data.database.repository

import io.github.vinnih.kipty.data.database.entity.AudioEntity

interface AudioRepository {

    suspend fun getAll(): List<AudioEntity>

    suspend fun getById(id: Long): AudioEntity?

    suspend fun insert(audio: AudioEntity)

    suspend fun delete(audio: AudioEntity)
}
