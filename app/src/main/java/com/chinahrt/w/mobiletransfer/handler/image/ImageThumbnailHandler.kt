package com.chinahrt.w.mobiletransfer.handler.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.chinahrt.w.mobiletransfer.handler.BaseHandler
import com.google.gson.Gson
import com.yanzhenjie.andserver.handler.BasicRequestHandler
import com.yanzhenjie.andserver.util.HttpRequestParser
import org.apache.http.HttpEntity
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.protocol.HttpContext
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.entity.ByteArrayEntity
import java.io.ByteArrayOutputStream


/**
 * Created by W on 2017/12/1.
 */
class ImageThumbnailHandler(_cxt:Context): BasicRequestHandler() {

    private var cxt = _cxt

    private val TAG = ImageThumbnailHandler::class.java.name


    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {
        var params = HttpRequestParser.parse(request)
        var id = params["id"]?.toLong()
        var contentResolver = cxt.contentResolver
        var options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        var bitmap = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id as Long,MediaStore.Images.Thumbnails.MICRO_KIND,options)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        var entity = ByteArrayEntity(baos.toByteArray())

        response?.entity = entity
    }




}