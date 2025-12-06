package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.androidtranscoder.utils.toWavReader
import io.github.vinnih.kipty.data.application.AppConfig
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.components.FloatingAddButton
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by viewModels()
    private val audioViewModel: AudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!AppConfig(applicationContext).read().defaultSamplesLoaded) {
            lifecycleScope.launch {
                homeViewModel.copyAssets().map {
                    it.toWavReader(applicationContext.cacheDir)
                }.forEach { reader ->
                    homeViewModel.createAudio(reader)
                    reader.dispose()
                }
            }
        }

        setContent {
            AppTheme {
                Scaffold(
                    topBar = { KiptyTopBar("Home") },
                    bottomBar = { KiptyBottomBar() },
                    floatingActionButton = {
                        FloatingAddButton(modifier = Modifier.size(72.dp))
                    }
                ) { paddingValues ->
                    HomeScreen(
                        controller = homeViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
