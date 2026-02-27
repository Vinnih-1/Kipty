package io.github.vinnih.kipty.domain.usecase.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.workers.PopulateWorker
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PopulateDatabaseUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(onSuccess: () -> Unit): Job {
        val request = OneTimeWorkRequestBuilder<PopulateWorker>()
            .addTag(PopulateWorker.Companion.TAG)
            .build()
        val workManager = WorkManager.Companion.getInstance(context)

        workManager.enqueueUniqueWork(
            "initial_setup_work",
            ExistingWorkPolicy.KEEP,
            request
        )

        return CoroutineScope(Dispatchers.IO).launch {
            workManager.getWorkInfoByIdFlow(request.id).collect {
                if (it?.state == WorkInfo.State.SUCCEEDED) {
                    onSuccess.invoke()
                }
            }
        }
    }
}
