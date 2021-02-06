package com.dmishra.meteorsonearth.networkApi

import com.dmishra.meteorsonearth.model.MeteorData
import com.dmishra.meteorsonearth.util.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MeteorApiService {
    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constant.ApiDetail.URL)
            .client(clientBuilder())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        meteorApiInterface = retrofit.create(MeteorApiInterface::class.java)
    }

    private fun clientBuilder(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptorBuilder())
            .addInterceptor(tokenInterceptorBuilder())
            .build()
    }

    fun getMeteorsDataList(callback: Callback<List<MeteorData>>) {
        meteorApiInterface.getMeteorsJsonData().enqueue(callback)
    }

    companion object {
        private lateinit var meteorApiInterface: MeteorApiInterface
        private fun loggingInterceptorBuilder(): HttpLoggingInterceptor {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return loggingInterceptor
        }

        private fun tokenInterceptorBuilder(): Interceptor {
            return Interceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url()
                val tokenUrl = originalUrl.newBuilder()
                    .addQueryParameter("$\$app_token", Constant.ApiDetail.TOKEN)
                    .build()
                val requestBuilder = original.newBuilder()
                    .url(tokenUrl)
                val tokenRequest = requestBuilder.build()
                chain.proceed(tokenRequest)
            }
        }
    }


}
