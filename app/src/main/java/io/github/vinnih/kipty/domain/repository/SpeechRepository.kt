package io.github.vinnih.kipty.domain.repository

import io.github.vinnih.kipty.data.database.entity.SpeechEntity

interface SpeechRepository {

    suspend fun save(speech: SpeechEntity): Long

    suspend fun getById(id: Int): SpeechEntity?
}
