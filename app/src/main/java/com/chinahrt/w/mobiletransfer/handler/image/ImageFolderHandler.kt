package com.chinahrt.w.mobiletransfer.handler.image

import android.content.Context
import com.chinahrt.w.mobiletransfer.entity.Image
import com.chinahrt.w.mobiletransfer.utils.ImageUtil
import com.google.gson.Gson
import com.yanzhenjie.andserver.handler.BasicRequestHandler
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.protocol.HttpContext
import java.util.HashMap

/**
 * Created by W on 2017/12/6.
 */
class ImageFolderHandler(_cxt:Context): BasicRequestHandler() {


    private var cxt = _cxt

    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {


        var result = HashMap<String,Object>()
        result.put("status",0 as Object)
        result.put("list",(ImageUtil.getImage(cxt) as Image).folders as Object)

        val stringEntity = StringEntity(Gson().toJson(result), "utf-8")
        response?.entity = stringEntity
    }
}