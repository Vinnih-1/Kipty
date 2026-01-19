package io.github.vinnih.kipty.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.vinnih.kipty.data.database.converter.TranscriptionConverter
import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.dao.NotificationDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationEntity

@TypeConverters(TranscriptionConverter::class)
@Database(
    version = 1,
    exportSchema = true,
    entities = [AudioEntity::class, NotificationEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao

    abstract fun notificationDao(): NotificationDao
}
