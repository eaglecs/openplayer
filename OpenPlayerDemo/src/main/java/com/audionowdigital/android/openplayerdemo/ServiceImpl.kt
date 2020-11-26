package com.audionowdigital.android.openplayerdemo

import io.reactivex.Observable
import io.reactivex.observers.DefaultObserver
import io.reactivex.observers.DisposableObserver
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceImpl {
    companion object{
        @JvmStatic
        fun streamChat(): Observable<ResponseBody>{
            val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://staging.chatbot.iviet.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(
                            OkHttpClient.Builder()
                                    .retryOnConnectionFailure(true)
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .writeTimeout(30, TimeUnit.SECONDS)
                                    .addInterceptor(ChatBotHeaderInterceptor())
                                    .build()
                    )
                    .build()
            return retrofit.create(ApiService::class.java).streamChat()
        }
    }
}