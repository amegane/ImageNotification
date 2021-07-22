package com.amegane3231.imagenotification.interfaces

import android.graphics.Bitmap
import android.graphics.Color
import android.util.DisplayMetrics

interface ImageProcessingListener {
    fun rgbToGray(inputImage: Bitmap) : Bitmap {
        val width = inputImage.width
        val height = inputImage.height
        val outputImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for(j in 0 until height) {
            for (i in 0 until width) {
                val rgb = inputImage.getPixel(i, j)
                val gray = (Color.red(rgb) * 0.3 + Color.green(rgb) * 0.59 + Color.blue(rgb) * 0.11).toInt()
                outputImage.setPixel(i, j, Color.rgb(gray, gray, gray))
            }
        }
        return outputImage
    }

    fun resize(bitmap: Bitmap, processedImageWidth: Int, processedImageHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, processedImageWidth, processedImageHeight, true)
    }

    fun resize(bitmap: Bitmap, densityDpi: Int) : Bitmap {
        return if (0 <= densityDpi && densityDpi <= DisplayMetrics.DENSITY_LOW) {
            resize(bitmap, LDPI_SIZE, LDPI_SIZE)
        } else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            resize(bitmap, MDPI_SIZE, MDPI_SIZE)
        } else if (densityDpi <= DisplayMetrics.DENSITY_HIGH) {
            resize(bitmap, HDPI_SIZE, HDPI_SIZE)
        } else if (densityDpi <= DisplayMetrics.DENSITY_XHIGH) {
            resize(bitmap, XHDPI_SIZE, XHDPI_SIZE)
        } else if (densityDpi <= DisplayMetrics.DENSITY_XXHIGH) {
            resize(bitmap, XXHDPI_SIZE, XXHDPI_SIZE)
        } else {
            resize(bitmap, XXXHDPI_SIZE, XXXHDPI_SIZE)
        }
    }

    fun createAlphaImage(bitmap: Bitmap) : Bitmap {
        val bitmapInstance = Bitmap.createBitmap(bitmap)
        val width = bitmapInstance.width
        val height = bitmapInstance.height
        for (j in 0 until height) {
            for (i in 0 until width) {
                val pixel = bitmapInstance.getPixel(i, j)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                if (red == 0 && green == 0 && blue == 0) {
                    bitmapInstance.setPixel(i, j, Color.argb(0, 0, 0,  0))
                }
            }
        }
        return bitmapInstance
    }

    companion object {
        private const val LDPI_SIZE = 36
        private const val MDPI_SIZE = 48
        private const val HDPI_SIZE = 72
        private const val XHDPI_SIZE = 96
        private const val XXHDPI_SIZE = 144
        private const val XXXHDPI_SIZE = 192
    }
}