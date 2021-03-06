package com.audionowdigital.android.openplayerdemo

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {
    @POST("stream")
    @Streaming
    fun streamChat(): Observable<ResponseBody>
}