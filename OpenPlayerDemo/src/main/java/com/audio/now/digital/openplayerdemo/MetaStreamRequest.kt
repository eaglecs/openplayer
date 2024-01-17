package com.audio.now.digital.openplayerdemo

import com.google.gson.annotations.SerializedName

class MetaStreamRequest (
        @SerializedName("event")
        var event: Event = Event()
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
        var text: String = "Bây giờ là 10 giờ 45 phút",
        @SerializedName("encodeFormat")
        var encodeFormat: String = "raw",
        @SerializedName("language")
        var language: String = "vi-VN"
)