package com.huiliang.lib_base.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import java.util.concurrent.Executors
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Time: 2024/12/17
 * Author: muse
 * QQ: 554953278
 * Description:
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
object ImageSaver {

    /**
     * 保存网络图片到相册
     * @param context 上下文
     * @param imageUrl 网络图片的 URL
     * @param fileName 要保存的文件名
     */
    fun saveNetworkImageToGallery(context: Context, imageUrl: String, fileName: String) {
        // 使用 Glide 下载网络图片并转换为 Bitmap
        Executors.newSingleThreadExecutor().execute {
            try {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get() // 下载并获取 Bitmap

                // 保存图片到相册
                saveImageToGallery(context, bitmap, fileName)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtils.e("Failed to download image: ${e.message}")
            }
        }
    }

    /**
     * 保存 Bitmap 到相册，自动适配 Android 版本
     */
    private fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToGalleryForAndroidQ(context, bitmap, fileName)
        } else {
            saveImageToGalleryLegacy(context, bitmap, fileName)
        }
    }

    /**
     * Android 10 及以上版本，使用 MediaStore 保存图片
     */
    private fun saveImageToGalleryForAndroidQ(context: Context, bitmap: Bitmap, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // 文件名
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // 文件类型
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyAppImages") // 相册文件夹
            put(MediaStore.Images.Media.IS_PENDING, true) // 标记为待写入
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { imageUri ->
            var fos: OutputStream? = null
            try {
                fos = resolver.openOutputStream(imageUri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos!!) // 写入 Bitmap 数据
                LogUtils.e("Image saved successfully: $imageUri")
                ToastUtils.showShort("保存成功")

                // 更新 IS_PENDING 状态为 false
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, false)
                resolver.update(imageUri, contentValues, null, null)
            } catch (e: IOException) {
                e.printStackTrace()
                LogUtils.e("Failed to save image: ${e.message}")
            } finally {
                fos?.close()
            }
        } ?: LogUtils.e("Failed to create MediaStore record")
    }

    /**
     * Android 10 以下版本，使用传统文件存储方式保存图片到相册
     */
    private fun saveImageToGalleryLegacy(context: Context, bitmap: Bitmap, fileName: String) {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(picturesDir, "MyAppImages") // 自定义相册文件夹
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val file = File(appDir, fileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) // 写入数据
            fos.flush()
            LogUtils.e("Image saved successfully: ${file.absolutePath}")

            // 通知系统刷新媒体库
            notifyGallery(context, file)
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtils.e("Failed to save image: ${e.message}")
        } finally {
            fos?.close()
        }
    }

    /**
     * 通知系统刷新媒体库，更新相册（适用于 Android 10 以下）
     */
    private fun notifyGallery(context: Context, file: File) {
        val uri = android.net.Uri.fromFile(file)
        context.sendBroadcast(
            android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
        )
        LogUtils.e("Gallery updated with image: $uri")
//        ToastUtils.showShort("保存成功")
    }
}