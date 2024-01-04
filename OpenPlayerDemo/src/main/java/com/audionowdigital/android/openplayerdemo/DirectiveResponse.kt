package com.audionowdigital.android.openplayerdemo

import com.google.gson.annotations.SerializedName

class DirectiveResponse(
    @SerializedName("header")
    val header: HeaderDirective?,
)
class HeaderDirective(
    @SerializedName("serverMessageId")
    val serverMessageId: String?
)