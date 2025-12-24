package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications")
    fun getAll(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE read = 0")
    fun getAllUnread(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE uid = :id")
    fun getById(id: Int): Flow<NotificationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(audio: NotificationEntity): Long

    @Delete
    suspend fun delete(audio: NotificationEntity)

    @Query("UPDATE notifications SET read = 1 WHERE read = 0")
    suspend fun readAll()
}
