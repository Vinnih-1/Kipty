package io.github.vinnih.kipty.domain.usecase.player

import io.github.vinnih.kipty.data.service.player.TempPlayerService
import javax.inject.Inject

class TempPlayerPlayUseCase @Inject constructor(private val tempPlayerService: TempPlayerService) {

    operator fun invoke(audioFilePath: String) = tempPlayerService.play(audioFilePath)
}
