package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {

    @Query("SELECT * FROM audios")
    fun getAll(): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audios WHERE uid = :id")
    fun getById(id: Int): Flow<AudioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(audio: AudioEntity): Long

    @Delete
    suspend fun delete(audio: AudioEntity)
}
