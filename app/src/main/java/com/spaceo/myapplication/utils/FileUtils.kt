package com.spaceo.soscanner.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

private const val tag = "FileUtils"

fun getTimestampString(): String {
    val date = Calendar.getInstance()
    return SimpleDateFormat("yyyy MM dd hh mm ss", Locale.US).format(date.time).replace(" ", "")
}


fun Activity.saveFile(path: String, uri: Uri): String {

    val opt = BitmapFactory.Options()

    val mFolder = File("$filesDir/Images")

    val bitmapImage : Bitmap? = getBitmapFromUri(uri, opt)

    if (!mFolder.exists()) {
        mFolder.mkdir()
    }

    val tmpFile = File(mFolder.absolutePath, "IMG_my${getTimestampString()}.png")

    var fos: FileOutputStream? = null

    try {
        fos = FileOutputStream(tmpFile)
        bitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush()
        fos.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()

    } catch (e: Exception) {
        e.printStackTrace()
    }

    var filePath = ""

    if (tmpFile.exists() && tmpFile.length() > 0) {
        filePath = tmpFile.absolutePath
        val srcFile = File(path)
            val result = tmpFile.copyTo(srcFile, true)
            Log.d(tag, "copied file ${result.absolutePath}")
    }

    return filePath
}



@Throws(IOException::class)
fun Context.getBitmapFromUri(uri: Uri, options: BitmapFactory.Options? = null): Bitmap? {
    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap? = if (options != null)
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
    else
        BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image
}

suspend fun Activity.saveImageFile(path: String, shouldOverride: Boolean = false, uri: Uri): String {

    return withContext(Dispatchers.IO) {

        var scaledBitmap: Bitmap? = null

        try {
            val (hgt, wdt) = getImageHgtWdt(uri)

            // Part 1: Decode image
            val unscaledBitmap = decodeFile(this@saveImageFile, uri, wdt, hgt, ScalingLogic.FIT)
            if (unscaledBitmap != null) {
                    scaledBitmap = unscaledBitmap
            }

            // Store to tmp file
            val mFolder = File("$filesDir/Images")
            if (!mFolder.exists()) {
                mFolder.mkdir()
            }

            val tmpFile = File(mFolder.absolutePath, "IMG_${getTimestampString()}.png")

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(tmpFile)
                scaledBitmap?.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    fos
                )
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            var compressedPath = ""
            if (tmpFile.exists() && tmpFile.length() > 0) {
                compressedPath = tmpFile.absolutePath
                if (shouldOverride) {
                    val srcFile = File(path)
                    val result = tmpFile.copyTo(srcFile, true)
                    Log.d(tag, "copied file ${result.absolutePath}")
                    Log.d(tag, "Delete temp file ${tmpFile.delete()}")
                }
            }

            scaledBitmap?.recycle()

            return@withContext if (shouldOverride) path else compressedPath
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return@withContext ""
    }

}


fun decodeFile(
    context: Context,
    uri: Uri,
    dstWidth: Int,
    dstHeight: Int,
    scalingLogic: ScalingLogic
): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    context.getBitmapFromUri(uri, options)
    options.inJustDecodeBounds = false

    options.inSampleSize = calculateSampleSize(
        options.outWidth,
        options.outHeight,
        dstWidth,
        dstHeight,
        scalingLogic
    )

    return context.getBitmapFromUri(uri, options)
}

/**
 * ScalingLogic defines how scaling should be carried out if source and
 * destination image has different aspect ratio.
 *
 * CROP: Scales the image the minimum amount while making sure that at least
 * one of the two dimensions fit inside the requested destination area.
 * Parts of the source image will be cropped to realize this.
 *
 * FIT: Scales the image the minimum amount while making sure both
 * dimensions fit inside the requested destination area. The resulting
 * destination dimensions might be adjusted to a smaller size than
 * requested.
 */
enum class ScalingLogic {
    CROP, FIT
}

/**
 * Calculate optimal down-sampling factor given the dimensions of a source
 * image, the dimensions of a destination area and a scaling logic.
 *
 * @param srcWidth Width of source image
 * @param srcHeight Height of source image
 * @param dstWidth Width of destination area
 * @param dstHeight Height of destination area
 * @param scalingLogic Logic to use to avoid image stretching
 * @return Optimal down scaling sample size for decoding
 */
fun calculateSampleSize(
    srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int,
    scalingLogic: ScalingLogic
): Int {
    if (scalingLogic == ScalingLogic.FIT) {
        val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
        val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

        return if (srcAspect > dstAspect) {
            srcWidth / dstWidth
        } else {
            srcHeight / dstHeight
        }
    } else {
        val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
        val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

        return if (srcAspect > dstAspect) {
            srcHeight / dstHeight
        } else {
            srcWidth / dstWidth
        }
    }
}



fun Context.getImageHgtWdt(uri: Uri): Pair<Int, Int> {
        val opt = BitmapFactory.Options()

        /* by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded.
        If you try the use the bitmap here, you will get null.*/
        opt.inJustDecodeBounds = true
        val bm = getBitmapFromUri(uri, opt)

        var actualHgt = (opt.outHeight).toFloat()
        var actualWdt = (opt.outWidth).toFloat()

        /*val maxHeight = 816.0f
        val maxWidth = 612.0f*/
        val maxHeight = 720f
        val maxWidth = 1280f
        var imgRatio = actualWdt / actualHgt
        val maxRatio = maxWidth / maxHeight

//    width and height values are set maintaining the aspect ratio of the image
        if (actualHgt > maxHeight || actualWdt > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHgt
                    actualWdt = (imgRatio * actualWdt)
                    actualHgt = maxHeight
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWdt
                    actualHgt = (imgRatio * actualHgt)
                    actualWdt = maxWidth
                }
                else -> {
                    actualHgt = maxHeight
                    actualWdt = maxWidth
                }
            }
        }

        return Pair(actualHgt.toInt(), actualWdt.toInt())
    }

fun Context.getUri(context: Context, bitmap: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path)
}

@Throws(IOException::class)
fun Context.getBitmaps(context: Context, uri: Uri?): Bitmap? {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
}