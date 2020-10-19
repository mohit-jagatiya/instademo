/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 https://www.spaceotechnologies.com
 *
 * Permissions is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.spaceo.myapplication.common

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.spaceo.myapplication.utils.PreferenceHelper.customPrefs
import com.spaceo.myapplication.utils.COMMON_REQ_CODE
import com.spaceo.myapplication.factory.ViewModelProviderFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.reflect.KClass

abstract class BaseActivity<V : BaseViewModel, B : ViewDataBinding> : AppCompatActivity() {

    protected abstract val modelClass: KClass<V>
    protected abstract val layoutId: Int
    protected lateinit var binding: B
    protected abstract fun initControls()
    protected open fun getDataFromIntent() {}

    /**
     * If we pass repository with Factory, then repository will call first then ViewModel init method
     * If we create repository inside viewmodel then ViewModel will call first
     * batter approch to pass repository with factory method to avoid recreation of repo if it already created
     * */
    protected val viewModel by lazy {
        ViewModelProvider(this, ViewModelProviderFactory(this.application)).get(modelClass.java)
    }

    protected lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = customPrefs(this)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        getDataFromIntent()
        initControls()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        viewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private lateinit var resultCallback: (resultCode: Int, data: Intent?) -> Unit

    fun startActivityForResult(intent: Intent, callback: (data: Intent?) -> Unit) {
        startActivityForResult(intent) { resultCode, data ->
            if (resultCode == Activity.RESULT_OK) {
                callback(data)
            }
        }
    }

    fun startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
        resultCallback = callback
        startActivityForResult(intent, COMMON_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::resultCallback.isInitialized && requestCode == COMMON_REQ_CODE)
            resultCallback(resultCode, data)
    }
    fun isPermissionsAllowed(permissions: Array<String>, shouldRequestIfNotAllowed: Boolean = false, requestCode: Int = -1): Boolean {
        var isGranted = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
                if (!isGranted)
                    break
            }
        }
        if (!isGranted && shouldRequestIfNotAllowed) {
            if (requestCode.equals(-1))
                throw RuntimeException("Send request code in third parameter")
            requestRequiredPermissions(permissions, requestCode)
        }

        return isGranted
    }

    private fun requestRequiredPermissions(permissions: Array<String>, requestCode: Int) {
        val pendingPermissions: ArrayList<String> = ArrayList()
        permissions.forEachIndexed { index, permission ->
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED)
                pendingPermissions.add(permission)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val array = arrayOfNulls<String>(pendingPermissions.size)
            pendingPermissions.toArray(array)
            requestPermissions(array, requestCode)
        }
    }

    fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        var isGranted = true
        for (grantResult in grantResults) {
            isGranted = grantResult.equals(PackageManager.PERMISSION_GRANTED)
            if (!isGranted)
                break
        }
        return isGranted
    }



    fun checkRotation(file: File): File {
        try {

            val filePath = file.absolutePath
            val bitmap = BitmapFactory.decodeFile(filePath)
            val newBitmap: Bitmap = modifyOrientation(bitmap, file.absolutePath)
            val mFolder = File(filesDir.absolutePath)
            if (!mFolder.exists()) {
                mFolder.mkdirs()
            }
            val newFile = getTemporalFile()
            val os: OutputStream = BufferedOutputStream(FileOutputStream(newFile))
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
            return newFile
        } catch (e: Exception) {
        }
        return file
    }
    private fun getTemporalFile(contentType: String = "image"): File {
        if (contentType.contains("video")) {
            return File(this.externalCacheDir, "android_${System.currentTimeMillis()}.mp4")
        } else if (contentType.contains("pdf")) {
            return File(this.externalCacheDir, "android_${System.currentTimeMillis()}.pdf")
        } else {
            return File(this.externalCacheDir, "android_${System.currentTimeMillis()}.jpg")
        }
    }
    private fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
        val ei = ExifInterface(image_absolute_path)
        return when (ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, vertical = false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, vertical = true)
            else -> rotate(bitmap, 90f)
        }
    }
    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) -1f else 1f, if (vertical) -1f else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }




}
