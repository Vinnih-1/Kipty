package io.github.vinnih.kipty.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.vinnih.kipty.R
import java.io.File

@Composable
fun ProfilePicture(
    iconPath: String,
    updatedAt: Long,
    modifier: Modifier = Modifier,
    showUpdateIcon: Boolean = true,
    size: Dp = 100.dp,
    shape: Shape = CircleShape,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val imageModel = remember(iconPath, updatedAt) {
        if (iconPath.isNotEmpty()) {
            ImageRequest.Builder(context)
                .data(File(iconPath))
                .memoryCacheKey("$iconPath-$updatedAt")
                .diskCacheKey("$iconPath-$updatedAt")
                .crossfade(true)
                .size(size.value.toInt())
                .build()
        } else {
            null
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(colors.secondaryContainer)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    tint = colors.onSecondaryContainer,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
        if (showUpdateIcon) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, colors.onSecondaryContainer.copy(.4f), CircleShape)
                    .background(colors.secondaryContainer, CircleShape)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = colors.onSecondaryContainer
                )
            }
        }
    }
}
