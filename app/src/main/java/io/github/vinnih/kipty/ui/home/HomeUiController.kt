package io.github.vinnih.kipty.ui.home

import java.io.File

interface HomeUiController {

    fun convertFile(file: File, onSuccess: (File) -> Unit)
}
