package com.chinahrt.w.mobiletransfer.handler

import com.yanzhenjie.andserver.RequestHandler
import org.apache.http.entity.StringEntity
import org.apache.http.HttpException
import org.apache.http.HttpResponse
import java.io.IOException


/**
 * Created by W on 2017/12/1.
 */
abstract class BaseHandler:RequestHandler {
    /**
     * Send a 404 response.
     *
     * @param response [HttpResponse].
     * @throws HttpException [HttpException].
     * @throws IOException   [IOException].
     */
    @Throws(HttpException::class, IOException::class)
    protected fun requestInvalid(response: HttpResponse) {
        requestInvalid(response, "The requested resource does not exist.")
    }

    /**
     * Send a 404 response.
     *
     * @param response [HttpResponse].
     * @param message  message.
     * @throws HttpException [HttpException].
     * @throws IOException   [IOException].
     */
    @Throws(HttpException::class, IOException::class)
    protected fun requestInvalid(response: HttpResponse, message: String) {
        response.setStatusCode(404)
        response.entity = StringEntity(message)
    }
}