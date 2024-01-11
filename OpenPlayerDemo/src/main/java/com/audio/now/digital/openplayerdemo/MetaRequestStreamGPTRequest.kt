package com.audio.now.digital.openplayerdemo

import com.google.gson.annotations.SerializedName

class MetaRequestStreamGPTRequest (
    @SerializedName("event")
        var event: EventRequestGPT = EventRequestGPT(),
)

class EventRequestGPT(
        @SerializedName("header")
        var header: HeaderRequestGPT = HeaderRequestGPT()
)

class HeaderRequestGPT(
        @SerializedName("dialogRequestId")
        var dialogRequestId: String = "dialogRequestId-" + System.currentTimeMillis().toString(),
        @SerializedName("messageId")
        var messageId: String = "messageId-" + System.currentTimeMillis().toString(),
        @SerializedName("name")
        var name: String = "Recognize",
        @SerializedName("namespace")
        var namespace: String = "SpeechRecognizer",
        @SerializedName("rawSpeech")
        var rawSpeech: String = "cách nấu bánh chưng"
)