package io.github.vinnih.kipty.data.database.repository

import io.github.vinnih.kipty.data.database.dao.SpeechDao
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import io.github.vinnih.kipty.domain.repository.SpeechRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpeechRepositoryImpl @Inject constructor(private val dao: SpeechDao) : SpeechRepository {

    override suspend fun save(speech: SpeechEntity): Long = withContext(Dispatchers.IO) {
        return@withContext dao.save(speech)
    }

    override suspend fun getById(id: Int): SpeechEntity? = dao.getById(id)
}
