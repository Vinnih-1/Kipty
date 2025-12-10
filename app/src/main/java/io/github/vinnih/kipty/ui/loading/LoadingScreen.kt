package io.github.vinnih.kipty.ui.loading

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.androidtranscoder.utils.toWavReader
import io.github.vinnih.kipty.data.application.AppConfig
import io.github.vinnih.kipty.ui.home.FakeHomeViewModel
import io.github.vinnih.kipty.ui.home.HomeController

@Composable
fun LoadingScreen(homeController: HomeController, text: String, onLoad: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!AppConfig(context).read().defaultSamplesLoaded) {
            homeController.copyAssets().map {
                it.toWavReader(context.cacheDir)
            }.forEach { reader ->
                homeController.createAudio(reader)
                reader.dispose()
            }
        }
        onLoad.invoke()
    }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(48.dp)) {
            Text(
                text = text,
                color = colors.primary,
                style = typography.displayMedium,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen(homeController = FakeHomeViewModel(), text = "Kipty", onLoad = {})
}
