package io.github.vinnih.kipty.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.vinnih.kipty.data.database.converter.TranscriptionConverter
import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.dao.NotificationDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationEntity

@TypeConverters(TranscriptionConverter::class)
@Database(
    version = 2,
    exportSchema = true,
    entities = [AudioEntity::class, NotificationEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    @Suppress("ktlint:standard:property-naming")
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE audios ADD COLUMN playTime INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

    abstract fun audioDao(): AudioDao

    abstract fun notificationDao(): NotificationDao
}
