package io.github.vinnih.kipty.domain.usecase.record

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.service.record.RecorderService
import java.io.File
import javax.inject.Inject

class StartRecordUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recorderService: RecorderService
) {

    operator fun invoke(outputFile: (File) -> Unit) {
        val outputFile = File(
            context.cacheDir,
            "recording_${System.currentTimeMillis()}.m4a"
        )

        recorderService.startRecord(outputFile).also { outputFile(outputFile) }
    }
}
