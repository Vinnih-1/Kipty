package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import io.github.vinnih.kipty.data.database.entity.SpeechEntity

@Dao
interface SpeechDao {

    @Insert
    suspend fun save(speech: SpeechEntity): Long
}
