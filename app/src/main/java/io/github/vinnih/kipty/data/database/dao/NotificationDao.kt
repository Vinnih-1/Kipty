package io.github.vinnih.kipty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
@Suppress("ktlint:standard:max-line-length")
interface NotificationDao {

    @Query(
        "SELECT * FROM notifications WHERE DATE(createdAt) = DATE('now') ORDER BY createdAt DESC"
    )
    fun getToday(): Flow<List<NotificationEntity>>

    @Query(
        "SELECT * FROM notifications WHERE DATE(createdAt) = DATE('now', '-1 day') ORDER BY createdAt DESC"
    )
    fun getYesterday(): Flow<List<NotificationEntity>>

    @Query(
        "SELECT * FROM notifications WHERE DATE(createdAt) < DATE('now', '-2 day') ORDER BY createdAt DESC"
    )
    fun getEarlier(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE read = 0")
    fun getAllUnread(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(audio: NotificationEntity): Long

    @Delete
    suspend fun delete(audio: NotificationEntity)

    @Query("UPDATE notifications SET read = 1 WHERE uid = :id")
    suspend fun read(id: Long)
}
