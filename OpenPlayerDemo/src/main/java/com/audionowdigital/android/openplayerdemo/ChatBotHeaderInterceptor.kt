package com.audionowdigital.android.openplayerdemo

import android.util.Base64
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response

class ChatBotHeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        val method = original.method()
        requestBuilder.header("Content-Type", "application/octet-stream")
        requestBuilder.header("device-id", "9b18cd075b4fabfe")
        requestBuilder.header("device-type", "android")
        requestBuilder.header("client-version", "1.0.0")
        requestBuilder.header("timezone", "Asia/Ho_Chi_Minh")
        requestBuilder.header("olli-session-id", "755095a0-2be6-11eb-97e1-0b68a0480c6c")
        val gson = Gson()
        val metaStr = gson.toJson(MetaStreamRequest())
        val data: ByteArray = metaStr.toByteArray()
        val metaBase64 = Base64.encodeToString(data, Base64.NO_WRAP)
        metaBase64?.let {
            requestBuilder.header("meta", it)
        }
        requestBuilder.header("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDU3NzIzMTksIm5iZiI6MTYwNTc3MjMxOSwianRpIjoiMmRhNmE1ZTAtYTJjNi00OTcwLWEwY2EtNTQwNTUzOWRhNWRhIiwiaWRlbnRpdHkiOiJ7XCJzdWJcIjogMTY3LCBcIm5hbWVcIjogXCJEdWMgQW5oXCIsIFwiZW1haWxcIjogXCJsZWR1Y2FuaC5ia2l0MTBAZ21haWwuY29tXCIsIFwicm9sZVwiOiAxLCBcInN0YXR1c1wiOiAxLCBcImRldmljZV9pZFwiOiBcIjliMThjZDA3NWI0ZmFiZmVcIiwgXCJkZWZhdWx0X2xhbmd1YWdlXCI6IFwidmktVk5cIiwgXCJleHByaXJhdGlvblwiOiA4NjQwMCwgXCJwaG9uZV9udW1iZXJcIjogXCIwMTIzNDU2Nzg5NlwiLCBcImNhbGxpbmdfbmFtZVwiOiBcIkxlIER1YyBBbmhcIn0iLCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.yjvT1W8LlW5NyARCeLjSs7QHgb7-U2J1nLNDqGIb3I8")
        requestBuilder.header("user-id", "167")
        requestBuilder.method(method, original.body())
        return chain.proceed(requestBuilder.build())
    }
}