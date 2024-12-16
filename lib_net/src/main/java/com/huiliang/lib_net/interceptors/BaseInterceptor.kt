package com.huiliang.lib_net.interceptors

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request

/**
 * Created by 傅令杰 on 2017/4/11
 */
abstract class BaseInterceptor : Interceptor {
    protected fun getUrlParameters(chain: Interceptor.Chain): LinkedHashMap<String, String?> {
        val url: HttpUrl = chain.request().url
        val size = url.querySize
        val params = LinkedHashMap<String, String?>()
        for (i in 0 until size) {
            params[url.queryParameterName(i)] = url.queryParameterValue(i)
        }
        return params
    }

    protected fun getUrlParameters(chain: Interceptor.Chain, key: String?): String? {
        val request: Request = chain.request()
        return request.url.queryParameter(key!!)
    }

    protected fun getBodyParameters(chain: Interceptor.Chain): LinkedHashMap<String, String> {
        val formBody = chain.request().body as FormBody
        val params = LinkedHashMap<String, String>()
        var size = 0
        if (formBody != null) {
            size = formBody.size
        }
        for (i in 0 until size) {
            params[formBody.name(i)] = formBody.value(i)
        }
        return params
    }

    protected fun getBodyParameters(chain: Interceptor.Chain, key: String): String? {
        return getBodyParameters(chain)[key]
    }
}
