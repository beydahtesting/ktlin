package com.example.myapplication

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun compressToJPEG(bitmap: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
