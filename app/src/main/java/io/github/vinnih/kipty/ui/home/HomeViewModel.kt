package io.github.vinnih.kipty.ui.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.workers.PopulateWorker
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(@ApplicationContext val context: Context) :
    ViewModel(),
    HomeController {

    override fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override suspend fun populateDatabase(onSuccess: () -> Unit) {
        val request = OneTimeWorkRequestBuilder<PopulateWorker>().build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork(
            "initial_setup_work",
            ExistingWorkPolicy.KEEP,
            request
        )

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect {
                when (it?.state) {
                    androidx.work.WorkInfo.State.SUCCEEDED -> {
                        onSuccess.invoke()
                    }

                    else -> {}
                }
            }
        }
    }
}
