package com.huiliang.lib_net

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.huiliang.lib_net.bean.ApiListResponse
import com.huiliang.lib_net.bean.ApiResponse
import com.huiliang.lib_net.error.DefaultErrorHandler
import com.huiliang.lib_net.error.ErrorHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huiliang.lib_net.interceptors.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Time: 2024/5/30
 * Author: Muse丶
 * Description:网络请求工具类
 *     ___       ___       ___       ___
 *    /\__\     /\__\     /\  \     /\  \
 *   /::L_L_   /:/ _/_   /::\  \   /::\  \
 *  /:/L:\__\ /:/_/\__\ /\:\:\__\ /::\:\__\
 *  \/_/:/  / \:\/:/  / \:\:\/__/ \:\:\/  /
 *    /:/  /   \::/  /   \::/  /   \:\/  /
 *    \/__/     \/__/     \/__/     \/__/
 */


class NetworkRequestManager private constructor(
    private val context: Context,
    private val errorHandler: ErrorHandler = DefaultErrorHandler(context)
) {

    private val client: OkHttpClient
    private val retrofitBuilder: Retrofit.Builder
    private val gson = Gson()

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(HeaderInterceptor())
            .build()

        retrofitBuilder = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
    }

    companion object {
        @Volatile
        private var INSTANCE: NetworkRequestManager? = null

        fun getInstance(context: Context): NetworkRequestManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkRequestManager(context).also { INSTANCE = it }
            }
        }
    }

    fun <T> createService(
        apiServiceClass: Class<T>,
        baseUrl: String = SimpleConstant().BASE_URL
    ): T {
        return retrofitBuilder
            .baseUrl(baseUrl)
            .build()
            .create(apiServiceClass)
    }

    // 返回单个模型
    suspend fun <T> fetchResult(
        call: suspend () -> Response<ApiResponse<T>>,
        type: Class<T>,
        requestName: String
    ): ResultByCoroutine<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    when (apiResponse.code) {
                        0 -> {
                            val dataJson = gson.toJson(apiResponse.data)
                            val dataObject = gson.fromJson(dataJson, type)
                            ResultByCoroutine.Success(dataObject)
                        }

                        else -> {
                            errorHandler.handleApiError(apiResponse.code, apiResponse.msg)
                                .also {
                                    LogUtils.e(
                                        "NetworkRequest",
                                        "Error in code:${apiResponse.code}, $requestName: ${apiResponse.msg}"
                                    )
                                }
                        }
                    }
                } else {
                    errorHandler.handleError(HttpException(response)).also {
                        LogUtils.e(
                            "NetworkRequest",
                            "Error in $requestName: No response body,${requestName}"
                        )
                    }
                }
            } else {
                ToastUtils.showShort(response.message())
                errorHandler.handleError(HttpException(response)).also {
                    LogUtils.e(
                        "NetworkRequest",
                        "Error in $requestName: ${response.code()},${requestName}"
                    )
                }
            }
        } catch (e: Exception) {
            errorHandler.handleError(e).also {
                LogUtils.e("NetworkRequest", "Exception in $requestName", e)
            }
        }
    }

    // 返回列表模型
    suspend fun <T> fetchListResult(
        call: suspend () -> Response<ApiListResponse<T>>,
        type: TypeToken<List<T>>,
        requestName: String
    ): ResultByCoroutine<List<T>> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
//                    when (apiResponse.code) {
//                        0 -> {
//                            val dataJson = gson.toJson(apiResponse.data)
//                            val dataList: List<T> = gson.fromJson(dataJson, type.type)
//                            ResultByCoroutine.Success(dataList)
//                        }
//
//                        else -> errorHandler.handleApiError(apiResponse.code, apiResponse.msg)
//                            .also {
//                                LogUtils.e(
//                                    "NetworkRequest",
//                                    "Error in $requestName: ${apiResponse.msg}"
//                                )
//                            }
//                    }
                    val dataJson = gson.toJson(apiResponse.data)
                    val dataList: List<T> = gson.fromJson(dataJson, type.type)
                    ResultByCoroutine.Success(dataList)
                } else {
                    errorHandler.handleError(HttpException(response)).also {
                        LogUtils.e("NetworkRequest", "Error in $requestName: No response body")
                    }
                }
            } else {
                errorHandler.handleError(HttpException(response)).also {
                    LogUtils.e("NetworkRequest", "Error in $requestName: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            errorHandler.handleError(e).also {
                LogUtils.e("NetworkRequest", "Exception in $requestName", e)
            }
        }
    }

    // 返回列表模型
    suspend fun <T> fetchListResult1(
        call: suspend () -> Response<ApiListResponse<T>>,
        type: TypeToken<List<T>>,
        requestName: String
    ): ResultByCoroutine<List<T>> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    when (apiResponse.code) {
                        0 -> {
                            val dataString = apiResponse.data
                            val newData = if (dataString.isNotEmpty()) {
                                listOf(dataString[1])
                            } else {
                                listOf()
                            }
                            val dataJson = gson.toJson(newData)
                            val dataList: List<T> = gson.fromJson(dataJson, type.type)
                            ResultByCoroutine.Success(dataList)
                        }

                        else -> errorHandler.handleApiError(apiResponse.code, apiResponse.msg)
                            .also {
                                LogUtils.e(
                                    "NetworkRequest",
                                    "Error in $requestName: ${apiResponse.msg}"
                                )
                            }
                    }
                } else {
                    errorHandler.handleError(HttpException(response)).also {
                        LogUtils.e("NetworkRequest", "Error in $requestName: No response body")
                    }
                }
            } else {
                errorHandler.handleError(HttpException(response)).also {
                    LogUtils.e("NetworkRequest", "Error in $requestName: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            errorHandler.handleError(e).also {
                LogUtils.e("NetworkRequest", "Exception in $requestName", e)
            }
        }
    }
}
