package io.github.vinnih.kipty.domain.usecase.audio

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class TranscribeAudioUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AudioRepository
) {
    operator fun invoke(audioEntity: AudioEntity): Flow<WorkInfo.State> {
        val request = OneTimeWorkRequestBuilder<TranscriptionWorker>()
            .setInputData(Data.Builder().putInt("AUDIO_ID", audioEntity.uid).build())
            .addTag(TranscriptionWorker.TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "transcript_audio_process",
            ExistingWorkPolicy.KEEP,
            request
        )

        return WorkManager.getInstance(context)
            .getWorkInfoByIdFlow(request.id)
            .filter { it != null }
            .map { it!!.state }
    }
}
