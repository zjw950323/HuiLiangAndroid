package com.huiliang.picturesque

import android.app.Application
//import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV

/**
 * Time: 2024/11/20
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
class AiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 如果是 Debug 模式，启用调试日志和 Debug 功能
//        if (BuildConfig.DEBUG) {
//            ARouter.openLog();    // 打印日志
//            ARouter.openDebug();  // 开启调试模式（如果在 InstantRun 模式下必须开启）
//        }
//        ARouter.init(this); // 初始化 ARouter
        // MMKV 初始化
        MMKV.initialize(this);
    }
}