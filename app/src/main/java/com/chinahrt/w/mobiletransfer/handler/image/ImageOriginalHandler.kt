package com.chinahrt.w.mobiletransfer.handler.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.yanzhenjie.andserver.handler.BasicRequestHandler
import com.yanzhenjie.andserver.util.HttpRequestParser
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.protocol.HttpContext
import java.io.ByteArrayOutputStream

/**
 * Created by W on 2017/12/5.
 */
class ImageOriginalHandler(_cxt:Context):BasicRequestHandler() {
    private var cxt = _cxt
    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {
        var params = HttpRequestParser.parse(request)
        var id = params["id"]
        var contentResolver = cxt.contentResolver

        var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id).build()

        var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        var entity = ByteArrayEntity(baos.toByteArray())

        response?.entity = entity
        if (!bitmap.isRecycled)
            bitmap.recycle()
    }
}