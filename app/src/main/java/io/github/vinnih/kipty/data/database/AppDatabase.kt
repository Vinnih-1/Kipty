package io.github.vinnih.kipty.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.vinnih.kipty.data.database.converter.TranscriptionConverter
import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity

@TypeConverters(TranscriptionConverter::class)
@Database(entities = [AudioEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
}
