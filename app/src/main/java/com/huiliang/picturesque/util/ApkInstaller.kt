package com.huiliang.picturesque.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.LogUtils
import java.io.File

/**
 * Time: 2024/12/30
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
object ApkInstaller {

    fun installApk(context: Context, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            LogUtils.e("ApkInstaller", "APK file does not exist: $filePath")
            return
        }

        LogUtils.e("ApkInstaller", "Installing APK: $filePath")

        val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file).also {
                context.grantUriPermission(
                    context.packageName, it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            LogUtils.e("ApkInstaller", "Error starting install activity", e)
            handleInstallError(context)
        }
    }

    fun canRequestPackageInstalls(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun installApkWithProvider(context: Context, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            LogUtils.e("ApkInstaller", "APK file does not exist: $filePath")
            return
        }

        val apkUri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", file
        )

        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            LogUtils.e("ApkInstaller", "Error starting install activity", e)
        }
    }

    private fun handleInstallError(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(settingsIntent)
        }
    }
}
