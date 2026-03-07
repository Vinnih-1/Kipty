package io.github.vinnih.kipty.domain.usecase.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import jakarta.inject.Inject
import java.io.File

class UpdateProfileIconUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(file: File) {
        if (!file.exists()) return

        val destination = File(context.filesDir, "profile_icon.png")
        if (file.canonicalPath.startsWith(context.cacheDir.canonicalPath)) {
            file.copyTo(destination, overwrite = true)
            repository.updateProfileIconPath(destination.absolutePath)
        }
    }
}
