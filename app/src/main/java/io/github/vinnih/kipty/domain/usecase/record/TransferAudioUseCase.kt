package io.github.vinnih.kipty.domain.usecase.record

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.service.audio.OutputFormat
import io.github.vinnih.kipty.domain.usecase.audio.ResampleAudioUseCase
import io.github.vinnih.kipty.utils.createFolder
import java.io.File
import javax.inject.Inject

class TransferAudioUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resampleAudioUseCase: ResampleAudioUseCase
) {

    suspend operator fun invoke(recordPath: String, path: String, outputFile: (File) -> Unit) {
        val path = File(context.filesDir, path).createFolder()
        val recordFile = resampleAudioUseCase(
            file = File(recordPath),
            bitrate = 192,
            format = OutputFormat.OPUS
        )
        val destination = File(path, recordFile.name)

        recordFile.copyTo(destination, true).also { outputFile(it) }
        File(recordPath).delete()
        recordFile.delete()
    }
}
