package com.example.cryptoapi.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    const val baseUrl = "https://api.coingecko.com/api/v3/"

    private class RetryAfter429Interceptor(
        private val maxRetries: Int = 3,
        private val baseBackoffMs: Long = 500
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var attempt = 0
            var lastException: IOException? = null
            var request: Request = chain.request()

            while (attempt <= maxRetries) {
                try {
                    val response = chain.proceed(request)
                    if (response.code != 429) {
                        return response
                    }

                    // 429: Respect Retry-After header if present; otherwise exponential backoff
                    response.close()
                    val retryAfterSeconds = response.headers["Retry-After"]?.toLongOrNull()
                    val backoffMs = retryAfterSeconds?.let { it * 1000 } ?: (baseBackoffMs * (1L shl attempt))

                    if (attempt == maxRetries) break
                    try {
                        Thread.sleep(backoffMs)
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    }

                    attempt++
                    continue
                } catch (e: IOException) {
                    lastException = e
                    if (attempt == maxRetries) break
                    try {
                        Thread.sleep(baseBackoffMs * (1L shl attempt))
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    }
                    attempt++
                }
            }
            throw lastException ?: IOException("Request failed after retries")
        }
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(RetryAfter429Interceptor())
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}