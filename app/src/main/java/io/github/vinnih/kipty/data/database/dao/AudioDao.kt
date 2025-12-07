package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.AudioEntity

@Dao
interface AudioDao {

    @Query("SELECT * FROM audios")
    suspend fun getAll(): List<AudioEntity>

    @Query("SELECT * FROM audios WHERE uid = :id")
    suspend fun getById(id: Int): AudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(audio: AudioEntity)

    @Delete
    suspend fun delete(audio: AudioEntity)
}
