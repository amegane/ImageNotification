package com.amegane3231.imagenotification.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.DisplayMetrics

private const val LDPI_SIZE = 36
private const val MDPI_SIZE = 48
private const val HDPI_SIZE = 72
private const val XHDPI_SIZE = 96
private const val XXHDPI_SIZE = 144
private const val XXXHDPI_SIZE = 192
private const val BLEND_IMAGE_WIDTH = 80F
private const val BLEND_IMAGE_HEIGHT = 80F
private const val ICON_WIDTH = 128
private const val ICON_HEIGHT = 128

fun Bitmap.rgbToGray(): Bitmap {
    val width = this.width
    val height = this.height
    val outputImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (j in 0 until height) {
        for (i in 0 until width) {
            val rgb = this.getPixel(i, j)
            val gray =
                (Color.red(rgb) * 0.3 + Color.green(rgb) * 0.59 + Color.blue(rgb) * 0.11).toInt()
            outputImage.setPixel(i, j, Color.argb(Color.alpha(rgb), gray, gray, gray))
        }
    }
    return outputImage
}

fun Bitmap.resize(processedImageWidth: Int, processedImageHeight: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, processedImageWidth, processedImageHeight, true)
}

fun Bitmap.resize(densityDpi: Int): Bitmap {
    return if (0 <= densityDpi && densityDpi <= DisplayMetrics.DENSITY_LOW) {
        resize(LDPI_SIZE, LDPI_SIZE)
    } else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
        resize(MDPI_SIZE, MDPI_SIZE)
    } else if (densityDpi <= DisplayMetrics.DENSITY_HIGH) {
        resize(HDPI_SIZE, HDPI_SIZE)
    } else if (densityDpi <= DisplayMetrics.DENSITY_XHIGH) {
        resize(XHDPI_SIZE, XHDPI_SIZE)
    } else if (densityDpi <= DisplayMetrics.DENSITY_XXHIGH) {
        resize(XXHDPI_SIZE, XXHDPI_SIZE)
    } else {
        resize(XXXHDPI_SIZE, XXXHDPI_SIZE)
    }
}

fun Bitmap.createAlphaImage(): Bitmap {
    val bitmap = Bitmap.createBitmap(this)
    val width = bitmap.width
    val height = bitmap.height
    for (j in 0 until height) {
        for (i in 0 until width) {
            val pixel = bitmap.getPixel(i, j)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            if (red == 0 && green == 0 && blue == 0) {
                bitmap.setPixel(i, j, Color.argb(0, 0, 0, 0))
            }
        }
    }
    return bitmap
}

fun Bitmap.createIconImage(): Bitmap {
    val resizeScale = if (this.width >= this.height) {
        BLEND_IMAGE_WIDTH / this.width
    } else {
        BLEND_IMAGE_HEIGHT / this.height
    }
    val iconImage =
        this.resize((this.width * resizeScale).toInt(), (this.height * resizeScale).toInt())
    val iconBase = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(iconBase)
    canvas.drawBitmap(
        iconImage,
        (iconBase.width - iconImage.width) / 2F,
        (iconBase.height - iconImage.height) / 2F,
        null
    )
    return iconBase
}