package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.service.player.TempPlayerService
import javax.inject.Inject

class TempPlayerStopUseCase @Inject constructor(private val tempPlayerService: TempPlayerService) {

    operator fun invoke() = tempPlayerService.stop()
}
