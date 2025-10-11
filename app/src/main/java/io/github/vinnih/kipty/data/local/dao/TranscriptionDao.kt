package io.github.vinnih.kipty.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.vinnih.kipty.data.local.entity.Transcription
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDao {

    @Insert
    suspend fun insertTranscription(transcription: Transcription)

    @Delete
    suspend fun deleteTranscription(transcription: Transcription)

    @Query("SELECT * FROM transcriptions")
    fun getAllTranscriptions(): Flow<List<Transcription>>
}