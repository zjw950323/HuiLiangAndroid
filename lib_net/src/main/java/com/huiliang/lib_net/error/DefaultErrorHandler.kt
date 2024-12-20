package com.huiliang.lib_net.error

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.huiliang.lib_net.ResultByCoroutine


/**
 * Time: 2024/5/31
 * Author: Muse丶
 * Description:
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
class DefaultErrorHandler(private val context: Context) : ErrorHandler {
    override fun handleApiError(code: Int, message: String?): ResultByCoroutine.ApiError {
        return when (code) {
            404 -> {
                LogUtils.e("DefaultErrorHandler", "Not Found:$code, $message")
                ResultByCoroutine.ApiError.NotFound
            }

            201 -> {
                LogUtils.e("DefaultErrorHandler", "Token Expired:$code, $message")
//                AccountProviderImpl.showLoginActivity(context, false)
                ResultByCoroutine.ApiError.TokenExpired
            }

            204 -> {
                LogUtils.e("DefaultErrorHandler", "Account Offline:$code, $message")
//                AccountProviderImpl.showLoginActivity(context, false)
                ResultByCoroutine.ApiError.AccountOffline
            }

            else -> {
                ToastUtils.showShort(message)
                LogUtils.e("DefaultErrorHandler", "API Error $code:,$message")
                ResultByCoroutine.ApiError.UnknownApiError(code, message)
            }
        }
    }

    override fun handleError(exception: Throwable): ResultByCoroutine.Error {
        ToastUtils.showShort(exception.message)
        LogUtils.e("DefaultErrorHandler", "Error: ${exception.message}", exception)
        return ResultByCoroutine.Error(exception)
    }
}

