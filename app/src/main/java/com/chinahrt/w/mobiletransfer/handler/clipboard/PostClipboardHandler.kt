package com.chinahrt.w.mobiletransfer.handler.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.google.gson.Gson
import com.yanzhenjie.andserver.handler.BasicRequestHandler
import com.yanzhenjie.andserver.util.HttpRequestParser
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.protocol.HttpContext
import java.net.URLDecoder
import java.util.HashMap

/**
 * Created by W on 2017/12/8.
 */
class PostClipboardHandler(_cxt:Context):BasicRequestHandler() {
    private var cxt = _cxt
    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {

        var params = HttpRequestParser.parse(request)

        var data = URLDecoder.decode(params["data"],"utf-8")

        var result = HashMap<String,Object>()
        result.put("status",0 as Object)

        try {
            var clipboardManager: ClipboardManager = cxt.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboardManager!=null){
                var clipData = ClipData.newPlainText("label",data)
                clipboardManager.primaryClip = clipData

            }else{
                result.put("status",1 as Object)
                result.put("message","剪贴板异常" as Object)
            }
        }catch (e:Exception){
            result.put("status",1 as Object)
            result.put("message",e.message as Object)
        }


        val stringEntity = StringEntity(Gson().toJson(result), "utf-8")
        response?.entity = stringEntity
    }
}