package com.audionowdigital.android.openplayerdemo

import com.google.gson.annotations.SerializedName

class MetaStreamRequest (
        @SerializedName("event")
        var event: Event= Event()
)

class Event(
        @SerializedName("header")
        var header: Header = Header(),
        @SerializedName("payload")
        var payload: PayloadDevice = PayloadDevice()
)

class Header(
        @SerializedName("dialogRequestId")
        var dialogRequestId: String = "dialogRequestId-" + System.currentTimeMillis().toString(),
        @SerializedName("messageId")
        var messageId: String  = "messageId-" + System.currentTimeMillis().toString(),
        @SerializedName("name")
        var name: String = "StreamAudio",
        @SerializedName("namespace")
        var namespace: String = "TextToSpeech"
)

class PayloadDevice(
        @SerializedName("text")
        var text: String = "Khi còn nhỏ, tôi đã coi Maradona là thần tượng. Thật vinh dự khi có vài lần được gặp ông. Maradona là một cầu thủ tuyệt vời, và là một một đàn ông ấm áp, thân thiện ở ngoài sân cỏ. Ông là nguồn cảm hứng thi đấu cho tôi và nhiều thế hệ cầu thủ. Hãy yên nghỉ",
        @SerializedName("encodeFormat")
        var encodeFormat: String = "raw",
        @SerializedName("language")
        var language: String = "vi-VN"
)