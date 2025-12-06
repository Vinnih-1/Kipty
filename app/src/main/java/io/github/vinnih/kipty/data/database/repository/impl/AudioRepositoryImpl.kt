package io.github.vinnih.kipty.data.database.repository.impl

import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(private val dao: AudioDao) : AudioRepository {

    override suspend fun getAll(): List<AudioEntity> = dao.getAll()

    override suspend fun getById(id: Long): AudioEntity? = dao.getById(id)

    override suspend fun insert(audio: AudioEntity) {
        dao.insert(audio)
    }

    override suspend fun delete(audio: AudioEntity) {
        dao.delete(audio)
    }
}
