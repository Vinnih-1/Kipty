package io.github.vinnih.kipty.data.database.repository

import io.github.vinnih.kipty.data.database.entity.AudioEntity

interface AudioRepository {

    suspend fun getAll(): List<AudioEntity>

    suspend fun getById(id: Int): AudioEntity?

    suspend fun save(audio: AudioEntity): Long

    suspend fun delete(audio: AudioEntity)
}
