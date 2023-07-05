package com.goofy.goober.sketchy.common

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

@Composable
fun createImageBitmap(@DrawableRes id: Int): ImageBitmap {
    return ImageBitmap.imageResource(id)
}
