package com.audionowdigital.android.openplayer

import android.util.Log

object LogDebug {
    @JvmStatic
    fun d(tag: String = "",msg: String){
//        if (BuildConfig.DEBUG){
            Log.d("duc_anh_", "$tag $msg" )
//        }
//        else {
//            Log.d("duc_anh", "$tag $msg" )
//        }
    }

    @JvmStatic
    fun e(tag: String = "",msg: String){
//        if (BuildConfig.DEBUG){
            Log.e("duc_anh_", "$tag $msg")
//        }
//
//        else {
//            Log.e("duc_anh", "$tag $msg")
//        }
    }
}