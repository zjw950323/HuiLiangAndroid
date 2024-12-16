package com.huiliang.lib_net.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import com.huiliang.lib_net.error.DefaultErrorHandler
import com.huiliang.lib_net.error.ErrorHandler
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
open class BaseViewModel(
    context: Context,
    private val errorHandler: ErrorHandler = DefaultErrorHandler(context)
) : ViewModel() {
    fun handleResult(result: ResultByCoroutine<*>) {
        when (result) {
            is ResultByCoroutine.ApiError -> errorHandler.handleApiError(
                result.code,
                result.message
            )

            is ResultByCoroutine.Error -> errorHandler.handleError(result.exception)
            else -> { /* no-op */
            }
        }
    }
}