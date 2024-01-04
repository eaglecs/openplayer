package com.audionowdigital.android.openplayerdemo

import com.google.gson.annotations.SerializedName

class MetaStreamGPTRequest (
        @SerializedName("event")
        var event: EventGPT= EventGPT(),
        @SerializedName("sampleRate")
        val sampleRate: Int = 16000
)

class EventGPT(
        @SerializedName("header")
        var header: HeaderGPT = HeaderGPT(),
        @SerializedName("payload")
        var payload: PayloadDeviceGPT = PayloadDeviceGPT()
)

class HeaderGPT(
        @SerializedName("dialogRequestId")
        var dialogRequestId: String = "dialogRequestId-" + System.currentTimeMillis().toString(),
        @SerializedName("messageId")
        var messageId: String = "messageId-" + System.currentTimeMillis().toString(),
        @SerializedName("name")
        var name: String = "WaitingForData",
        @SerializedName("namespace")
        var namespace: String = "TextToSpeech",
        @SerializedName("serverMessageId")
        var serverMessageId: String = "1511#9005495087910421"
)

class PayloadDeviceGPT(
        @SerializedName("serverMessageId")
        var serverMessageId: String = "1511#9005495087910421"
)