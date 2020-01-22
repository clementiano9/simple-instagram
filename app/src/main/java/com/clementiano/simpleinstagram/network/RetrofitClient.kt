package com.clementiano.simpleinstagram.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private fun getRetrofit(url: String): Retrofit {
            return buildRetrofit(url)
        }

        private fun buildRetrofit(url: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }

        private val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        fun getBasicApiInterface(): ApiInterface = getRetrofit("https://api.instagram.com/").create(ApiInterface::class.java)
        fun getGraphApiInterface(): ApiInterface = getRetrofit("https://graph.instagram.com/").create(ApiInterface::class.java)
        fun getPlainApiInterface(): ApiInterface = getRetrofit("https://www.instagram.com/").create(ApiInterface::class.java)
    }
}