package com.huiliang.lib_net.interceptors


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : BaseInterceptor() {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer sk-uwKTwkUwX6gZLjuDCh2862qEfDEJ7tSkDUGTPLOqQGHNs5Fz"
            )
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}
