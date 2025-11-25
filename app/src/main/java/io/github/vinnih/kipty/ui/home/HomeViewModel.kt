package io.github.vinnih.kipty.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        @param:ApplicationContext val context: Context,
    ) : ViewModel(), HomeUiController {
    override fun convertFile(file: File, onSuccess: (File) -> Unit) {
        TODO("Not yet implemented")
    }
}
