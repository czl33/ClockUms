package com.newczl.clockwidget.http

import TimeBean
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 *
 */
interface UmsApiService {

    @GET(Constant.GET_CURRENT_TIME)
    suspend fun getHotPlaylist(@Query("uid") id:String) : TimeBean


    companion object {
        private const val TAG = "UmsApiService"

        private var retrofitService: UmsApiService? = null

        private lateinit var httpResponse: Response

        private const val DEFAULT_TIMEOUT = 15

        val instance: UmsApiService
            get() {
                if (retrofitService == null) {
                    retrofitService =
                        getRetrofitService()
                }
                return retrofitService!!
            }


        private fun getRetrofitService(): UmsApiService {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            return Retrofit.Builder()
                .baseUrl(
                    Constant.URL
                )
                // 添加Gson转换器
                .client(mOkHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
//                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(UmsApiService::class.java)
        }

        private val loggingInterceptor: HttpLoggingInterceptor
            get() {
                val interceptor = HttpLoggingInterceptor { message -> Log.i(TAG, message) }
                interceptor.level = HttpLoggingInterceptor.Level.BASIC
                return interceptor
            }

        private var interceptor = Interceptor { chain ->
            val startTime = System.currentTimeMillis()
            val response = chain.proceed(chain.request())
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            val mediaType = response.body?.contentType()
            val content = response.body?.string()
            httpResponse = response

            Log.e(TAG, "content$content")
            Log.e(TAG, "httpResponse:${httpResponse.code}")
            response.newBuilder()
                .body(ResponseBody.create(mediaType, content ?: ""))
                .build()
        }


        private var mOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(
                DEFAULT_TIMEOUT.toLong(),
                TimeUnit.SECONDS
            ).callTimeout(
                DEFAULT_TIMEOUT.toLong(),
                TimeUnit.SECONDS
            )
            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }




}