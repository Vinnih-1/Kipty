package io.github.vinnih.kipty.domain.usecase.settings

import android.content.Context
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UpdateProfileIconUseCaseTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()
    private val repository = mockk<AppPreferencesRepository>(relaxed = true)
    private val context = mockk<Context>()
    private lateinit var cacheDir: File
    private lateinit var filesDir: File
    private lateinit var useCase: UpdateProfileIconUseCase

    @Before
    fun setup() {
        cacheDir = tmpFolder.newFolder("cache")
        filesDir = tmpFolder.newFolder("files")
        every { context.cacheDir } returns cacheDir
        every { context.filesDir } returns filesDir
        useCase = UpdateProfileIconUseCase(context, repository)
    }

    @Test
    fun `given file from cache dir, should copy and update repository`() = runTest {
        val cacheFile = File(cacheDir, "profile_icon_temp.png").apply { createNewFile() }

        useCase(cacheFile)
        coVerify { repository.updateProfileIconPath(any()) }
    }

    @Test
    fun `given file NOT from cache dir, should do nothing`() = runTest {
        val externalFile = tmpFolder.newFile("external_icon.png")

        useCase(externalFile)
        coVerify(exactly = 0) { repository.updateProfileIconPath(any()) }
    }

    @Test
    fun `given file that does not exist, should do nothing`() = runTest {
        val ghostFile = File(cacheDir, "nonexistent.png")

        useCase(ghostFile)
        coVerify(exactly = 0) { repository.updateProfileIconPath(any()) }
    }

    @Test
    fun `given file from cache dir, destination should be profile_icon_png inside filesDir`() =
        runTest {
            val cacheFile = File(cacheDir, "profile_icon_temp.png").apply { createNewFile() }

            useCase(cacheFile)

            val expectedPath = File(filesDir, "profile_icon.png").absolutePath

            coVerify { repository.updateProfileIconPath(expectedPath) }
        }
}
