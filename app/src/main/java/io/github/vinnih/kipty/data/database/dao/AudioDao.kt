package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {

    @Query("SELECT * FROM audios")
    fun getAllFlow(): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audios")
    fun getAll(): List<AudioEntity>

    @Query("SELECT * FROM audios WHERE uid = :id")
    fun getById(id: Int): AudioEntity?

    @Query("SELECT * FROM audios WHERE uid = :id")
    fun getFlowById(id: Int): Flow<AudioEntity?>

    @Query("UPDATE audios SET playTime = playTime + 1 WHERE uid = :id")
    suspend fun incrementPlayTime(id: Int)

    @Query("SELECT playTime FROM audios WHERE uid = :id")
    fun getFlowPlayTimeById(id: Int): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(audio: AudioEntity): Long

    @Delete
    suspend fun delete(audio: AudioEntity)

    @Query("UPDATE audios SET duration = :duration, audioSize = :size WHERE uid = :id")
    suspend fun updateMetadata(id: Int, duration: Long, size: Long)

    @Query("UPDATE audios SET state = :state WHERE uid = :id")
    suspend fun updateAudioState(id: Int, state: TranscriptionState)

    @Query("SELECT * FROM audios WHERE duration = 0 OR audioSize = 0")
    suspend fun getAudiosWithMissingMetadata(): List<AudioEntity>
}
