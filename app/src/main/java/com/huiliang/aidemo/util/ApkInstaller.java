package com.huiliang.aidemo.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Time: 2024/7/19
 * Author: muse
 * QQ: 554953278
 * Description:
 * ___       ___       ___       ___
 * /\__\     /\__\     /\  \     /\  \
 * /::L_L_   /:/ _/_   /::\  \   /::\  \
 * /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 * \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 * /:/  /   \::/  /   \::/  /   \:\/  /
 * \/__/     \/__/     \/__/     \/__/
 */


public class ApkInstaller {

    public static void installApk(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e("ApkInstaller", "APK file does not exist: " + filePath);
            return;
        }

        Log.d("ApkInstaller", "Installing APK: " + filePath);

        Uri apkUri;
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(file);
        }

        intent.setData(apkUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("ApkInstaller", "Error starting install activity", e);
            // 如果启动安装失败，可能需要请求安装未知来源应用的权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                settingsIntent.setData(Uri.parse("package:" + context.getPackageName()));
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(settingsIntent);
            }
        }
    }

    public static boolean canRequestPackageInstalls(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    public static void installApkWithProvider(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

            context.startActivity(intent);
        }
    }

}
