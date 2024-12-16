package com.huiliang.lib_net.error

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
interface ErrorHandler {
    fun handleApiError(code: Int, message: String?): ResultByCoroutine.ApiError
    fun handleError(exception: Throwable): ResultByCoroutine.Error
}

