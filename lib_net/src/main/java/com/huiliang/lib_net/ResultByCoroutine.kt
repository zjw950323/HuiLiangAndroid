package com.huiliang.lib_net

/**
 * Time: 2024/5/30
 * Author: Muse丶
 * Description:协程请求返回值
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */
sealed class ResultByCoroutine<out T> {
    data class Success<out T>(val data: T) : ResultByCoroutine<T>()
    data class Error(val exception: Throwable) : ResultByCoroutine<Nothing>()
    sealed class ApiError(val code: Int, val message: String?) : ResultByCoroutine<Nothing>() {
        object NotFound : ApiError(404, "Not Found")
        object TokenExpired : ApiError(201, "Token Expired")
        object AccountOffline : ApiError(204, "Account Offline")
        data class UnknownApiError(val errorCode: Int, val errorMessage: String?) :
            ApiError(errorCode, errorMessage)
    }

    object Loading : ResultByCoroutine<Nothing>()
}