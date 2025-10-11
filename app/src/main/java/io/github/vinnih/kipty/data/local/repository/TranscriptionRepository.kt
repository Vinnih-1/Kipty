package io.github.vinnih.kipty.data.local.repository

import io.github.vinnih.kipty.data.local.dao.TranscriptionDao
import io.github.vinnih.kipty.data.local.entity.Transcription
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptionRepository @Inject constructor(
    val transcriptionDao: TranscriptionDao
) {

    suspend fun insertTranscription(transcription: Transcription)
        = transcriptionDao.insertTranscription(transcription)

    suspend fun deleteTranscription(transcription: Transcription)
        = transcriptionDao.deleteTranscription(transcription)

    fun getAllTranscriptions(): Flow<List<Transcription>> = transcriptionDao.getAllTranscriptions()
}