package io.github.vinnih.kipty.domain.usecase.player

import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.workers.PopulateWorker
import io.github.vinnih.kipty.domain.repository.AudioRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first

class LoadAudiosUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository,
    private val prepareUseCase: PreparePlayerUseCase
) {

    suspend operator fun invoke(onFirstAudio: (AudioEntity) -> Unit) {
        val workManager = WorkManager.getInstance(context)

        workManager.getWorkInfosByTagFlow(PopulateWorker.TAG)
            .first { workInfos ->
                workInfos.isNotEmpty() && workInfos.all { it.state.isFinished }
            }
        audioRepository.getAllFlow()
            .dropWhile { it.isEmpty() }
            .first()
            .filter { !it.transcription.isNullOrEmpty() }
            .forEachIndexed { index, it ->
                if (index == 0) {
                    onFirstAudio(it)
                }
                prepareUseCase(it)
            }
    }
}
