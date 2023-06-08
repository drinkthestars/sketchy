package com.goofy.goober.sketchy.capture

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap

fun captureAndShare(
    imageBitmap: ImageBitmap,
    context: Context
) {
    storeScreenShot(context, imageBitmap.asAndroidBitmap())
}

// API Level 28
fun captureAndShare(
    width: Int,
    height: Int,
    rect: androidx.compose.ui.geometry.Rect,
    context: Context
) {
    val bitmap = Bitmap.createBitmap(
        /* width = */ width,
        /* height = */height,
        /* config = */Bitmap.Config.ARGB_8888
    )
    try {
        PixelCopy.request(
            /* source = */ context.getActivityWindow(),
            /* srcRect = */ Rect(Rect(rect.left.toInt(), rect.top.toInt(), rect.right.toInt(), rect.bottom.toInt())),
            /* dest = */ bitmap,
            /* listener = */ { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    // share the bitmap
                    storeScreenShot(context, bitmap)
                } else {
                    Log.e("PixelCopy", "Failed to copy pixels: $copyResult")
                }
            },
            /* listenerThread = */ Handler(Looper.getMainLooper())
        )
    } catch (e: IllegalArgumentException) {
        Log.e("PixelCopy", "Failed to copy pixels: ${e.message}", e)
    }
}

private fun storeScreenShot(context: Context, bitmap: Bitmap) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/jpeg"

    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "title")
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )

    try {
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                share.putExtra(Intent.EXTRA_STREAM, uri)
                context.startActivity(share)
            }
        }
    } catch (e: Exception) {
        Log.d("PixelCopy", "Error copying $uri", e)
    }
}
