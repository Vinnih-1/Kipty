package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.AudioEntity

@Dao
interface AudioDao {

    @Query("SELECT * FROM audios")
    fun getAll(): List<AudioEntity>

    @Query("SELECT * FROM audios WHERE uid = :id")
    fun getById(id: Long): AudioEntity?

    @Insert
    fun insert(audio: AudioEntity)

    @Delete
    fun delete(audio: AudioEntity)
}
