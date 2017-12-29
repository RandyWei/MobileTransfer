package com.chinahrt.w.mobiletransfer.handler

import android.content.Context
import android.util.Log
import com.yanzhenjie.andserver.RequestHandler
import com.yanzhenjie.andserver.util.HttpRequestParser
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.protocol.HttpContext

/**
 * Created by W on 2017/11/29.
 */
class RequestTestHandler:RequestHandler {

    private val TAG = RequestTestHandler::class.java.name


    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {


        val stringEntity = StringEntity("Test", "utf-8")
        response?.setEntity(stringEntity)


    }
}