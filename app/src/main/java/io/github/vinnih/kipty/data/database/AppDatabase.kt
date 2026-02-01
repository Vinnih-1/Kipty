package io.github.vinnih.kipty.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.vinnih.kipty.data.database.converter.TranscriptionConverter
import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.dao.NotificationDao
import io.github.vinnih.kipty.data.database.dao.SpeechDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.data.database.entity.SpeechEntity

@TypeConverters(TranscriptionConverter::class)
@Database(
    version = 2,
    exportSchema = true,
    entities = [AudioEntity::class, NotificationEntity::class, SpeechEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audioDao(): AudioDao

    abstract fun notificationDao(): NotificationDao

    abstract fun speechDao(): SpeechDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS speeches (
                uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                audioPath TEXT NOT NULL,
                speechPath TEXT NOT NULL,
                phrase TEXT NOT NULL,
                result TEXT NOT NULL,
                createdAt TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}
