package com.chinahrt.w.mobiletransfer.handler.image

import android.content.Context
import android.provider.MediaStore
import android.text.TextUtils

import android.util.Log
import com.chinahrt.w.mobiletransfer.entity.Image
import com.chinahrt.w.mobiletransfer.utils.ImageUtil
import com.google.gson.Gson
import com.yanzhenjie.andserver.handler.BasicRequestHandler
import com.yanzhenjie.andserver.util.HttpRequestParser
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.protocol.HttpContext
import java.net.URLDecoder
import java.sql.SQLException
import java.util.*


/**
 * Created by W on 2017/12/1.
 */
class ImageListHandler(_cxt: Context): BasicRequestHandler() {


    private var cxt = _cxt
    private val TAG = ImageListHandler::class.java.name

    private val STORE_IMAGES = arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID)

    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {
        Log.i(TAG,"image/list")

        var params = HttpRequestParser.parse(request)

        var offset = params!!["offset"]
        var size = params!!["size"]
        var bucketName = URLDecoder.decode(params!!["bucket"],"utf-8")

        var result = HashMap<String,Object>()
        result.put("status",0 as Object)

        if (TextUtils.isEmpty(offset)||TextUtils.isEmpty(size))
            result.put("list",(ImageUtil.getImage(cxt) as Image).list as Object)
        else {

            var list = ArrayList<Int>()
            var contentResolver = cxt.contentResolver

            try {

                //分页
                var limitStr = ""
                if (offset != null && size != null)
                    limitStr = " limit $offset,$size"

                //条件
                var selection:String? = null
                var selectionArgs: Array<String>? = null
                if (!TextUtils.isEmpty(bucketName)){
                     selection = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?"
                     selectionArgs = arrayOf(bucketName as String)
                }
                var cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, selection, selectionArgs, MediaStore.Images.Media.DATE_TAKEN + " desc" + limitStr)

                if (null != cursor) {
                    while (cursor.moveToNext()) {
                        list.add(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)))
                    }
                }
                result.put("list", list as Object)
            } catch (e: SQLException) {
                result.put("status", 1 as Object)
                result.put("message", e.message as Object)
            }
        }

        val stringEntity = StringEntity(Gson().toJson(result), "utf-8")
        response?.entity = stringEntity
    }

}