package io.github.vinnih.kipty.domain.usecase.audio

import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import jakarta.inject.Inject

class CancelTranscriptionUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        WorkManager.getInstance(context).cancelAllWorkByTag(TranscriptionWorker.TAG)
    }
}
