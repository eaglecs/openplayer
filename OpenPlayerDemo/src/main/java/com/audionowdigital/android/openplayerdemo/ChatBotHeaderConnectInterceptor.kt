package com.audionowdigital.android.openplayerdemo

import android.util.Base64
import com.audionowdigital.android.openplayer.LogDebug
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response

class ChatBotHeaderConnectInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        val method = original.method
        requestBuilder.header("Content-Type", "application/octet-stream")
        requestBuilder.header("device-id", "9b18cd075b4fabfe")
        requestBuilder.header("device-type", "android")
        requestBuilder.header("client-version", "1.0.0")
        requestBuilder.header("timezone", "Asia/Ho_Chi_Minh")
        requestBuilder.header("olli-session-id", "755095a0-2be6-11eb-97e1-0b68a0480c6c")
        requestBuilder.header(
            "Authorization",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDM4MjQxODgsIm5iZiI6MTcwMzgyNDE4OCwianRpIjoiNGIyZTdiNjEtYmEyOS00ZGJlLWJjYTAtZGVjNWZmYWI3YTFkIiwiaWRlbnRpdHkiOiJ7XCJzdWJcIjogMTUxMSwgXCJuYW1lXCI6IFwiXFx1MDExMFxcdTFlZTljIEFuaCBMXFx1MDBlYVwiLCBcImVtYWlsXCI6IFwiZHVjYW5oQG9sbGktYWkuY29tXCIsIFwicm9sZVwiOiAxLCBcInN0YXR1c1wiOiAxLCBcImRldmljZV9pZFwiOiBcIlwiLCBcImRlZmF1bHRfbGFuZ3VhZ2VcIjogXCJ2aS1WTlwiLCBcImV4cHJpcmF0aW9uXCI6IDg2NDAwLCBcInBob25lX251bWJlclwiOiBcIlwiLCBcImNhbGxpbmdfbmFtZVwiOiBcIkFuaFwifSIsImZyZXNoIjpmYWxzZSwidHlwZSI6ImFjY2VzcyIsInVzZXJfY2xhaW1zIjp7Imh0dHBzOi8vaGFzdXJhLmlvL2p3dC9jbGFpbXMiOnsieC1oYXN1cmEtYWxsb3dlZC1yb2xlcyI6WyJtZW1iZXIiXSwieC1oYXN1cmEtdXNlci1pZCI6MTUxMSwieC1oYXN1cmEtZGVmYXVsdC1yb2xlIjoiZ3Vlc3QiLCJ4LWhhc3VyYS1yb2xlIjoibWVtYmVyIn19fQ.uvGPZOAv3aKQ0hG5Gg2odWIQCtiOniVKlkAs2ttRZfk"
        )
        requestBuilder.header("user-id", "1511")
        requestBuilder.method(method, original.body)
        return chain.proceed(requestBuilder.build())
    }
}