//package com.audionowdigital.android.openplayerdemo
//
//import android.media.MediaDataSource
//import android.os.Build
//import androidx.annotation.RequiresApi
//
//@RequiresApi(Build.VERSION_CODES.M)
//class ByteArrayMediaDataSource(private val data: ByteArray) : MediaDataSource() {
//
//    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
//
//        synchronized(data) {
//            val length: Long = data.size.toLong()
//            if (position >= length) {
//                return -1 // -1 indicates EOF
//            }
//            var sizeResult = size
//            if (position + size > length) {
//                sizeResult -= (position + size - length).toInt()
//            }
//            System.arraycopy(data, position.toInt(), buffer, offset, sizeResult)
//            return size
//        }
//
//
////        if (position >= data.size) return -1 // -1 indicates EOF
////
////        val endPosition: Int = (position + size).toInt()
////        var size2: Int = size
////        if (endPosition > data.size)
////            size2 -= endPosition - data.size
////
////        System.arraycopy(data, position.toInt(), buffer, offset, size2)
////        return size2
//    }
//
//    override fun getSize(): Long {
//        synchronized(data){
//            return data.size.toLong()
//        }
//    }
//
//    override fun close() {}
//}