package com.audio.now.digital.openplayerdemo

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {
    @POST("stream")
    @Streaming
    fun streamChat(): Observable<ResponseBody>
    @GET("connect")
    @Streaming
    fun createDownChannel(): Observable<ResponseBody>
}