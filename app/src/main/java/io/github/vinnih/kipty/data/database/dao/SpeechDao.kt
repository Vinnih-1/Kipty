package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.SpeechEntity

@Dao
interface SpeechDao {

    @Insert
    suspend fun save(speech: SpeechEntity): Long

    @Query("SELECT * FROM speeches WHERE uid = :id")
    suspend fun getById(id: Int): SpeechEntity?
}
