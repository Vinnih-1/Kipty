package io.github.vinnih.kipty.data.database.repository.impl

import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRepositoryImpl @Inject constructor(private val dao: AudioDao) : AudioRepository {

    override suspend fun getAll(): List<AudioEntity> = withContext(Dispatchers.IO) {
        return@withContext dao.getAll()
    }

    override suspend fun getById(id: Int): AudioEntity? = withContext(Dispatchers.IO) {
        return@withContext dao.getById(id)
    }

    override suspend fun save(audio: AudioEntity): Long = withContext(Dispatchers.IO) {
        return@withContext dao.save(audio)
    }

    override suspend fun delete(audio: AudioEntity) {
        withContext(Dispatchers.IO) {
            dao.delete(audio)
        }
    }
}
