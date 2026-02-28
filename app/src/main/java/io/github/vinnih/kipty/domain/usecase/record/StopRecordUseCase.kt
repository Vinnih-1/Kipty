package io.github.vinnih.kipty.domain.usecase.record

import io.github.vinnih.kipty.data.service.record.RecorderService
import javax.inject.Inject

class StopRecordUseCase @Inject constructor(private val recorderService: RecorderService) {

    operator fun invoke() {
        recorderService.stopRecord()
    }
}
