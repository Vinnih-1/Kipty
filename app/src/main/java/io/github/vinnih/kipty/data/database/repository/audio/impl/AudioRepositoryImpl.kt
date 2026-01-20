package io.github.vinnih.kipty.data.database.repository.audio.impl

import io.github.vinnih.kipty.data.database.dao.AudioDao
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AudioRepositoryImpl @Inject constructor(private val dao: AudioDao) : AudioRepository {

    override fun getAllFlow(): Flow<List<AudioEntity>> = dao.getAllFlow()

    override fun getAll(): List<AudioEntity> = dao.getAll()

    override fun getById(id: Int): AudioEntity? = dao.getById(id)

    override fun getFlowById(id: Int): Flow<AudioEntity?> = dao.getFlowById(id)

    override fun getFlowPlayTimeById(id: Int): Flow<Long> = dao.getFlowPlayTimeById(id)

    override suspend fun incrementPlayTime(id: Int) {
        withContext(Dispatchers.IO) {
            dao.incrementPlayTime(id)
        }
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
