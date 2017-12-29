package com.chinahrt.w.mobiletransfer.handler.file

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.MimeTypeFilter
import android.text.TextUtils
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.yanzhenjie.andserver.handler.BasicRequestHandler
import com.yanzhenjie.andserver.util.HttpRequestParser
import org.apache.commons.fileupload.util.mime.MimeUtility
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.entity.StringEntity
import org.apache.http.protocol.HttpContext
import java.io.File
import android.provider.MediaStore.Files.FileColumns
import java.net.URLDecoder
import java.util.*


/**
 * Created by W on 2017/12/7.
 */
class FileListHandler(_cxt:Context): BasicRequestHandler() {

    private var cxt = _cxt

    override fun handle(request: HttpRequest?, response: HttpResponse?, context: HttpContext?) {

        var params = HttpRequestParser.parse(request)

        var parent = URLDecoder.decode(params["parent"],"utf-8")

        var result = HashMap<String,Object>()
        result.put("status",0 as Object)

        var sdcard = Environment.getExternalStorageDirectory().path//获得SD卡目录/mnt/sdcard（获取的是手机外置sd卡的路径
        if (TextUtils.isEmpty(sdcard)){
            result.put("message","不知道为什么路径空了" as Object)
        }else{

            var uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(FileColumns._ID, FileColumns.DATA,FileColumns.MIME_TYPE)
            //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
            val sortOrder = FileColumns.DATE_MODIFIED

            var file = if (TextUtils.isEmpty(parent))
                File(sdcard)
            else
                File(sdcard,parent)

            //TODO 此处是不是可以使用扩展类
            var list = ArrayList<com.chinahrt.w.mobiletransfer.entity.File>()
            for (f in file!!.listFiles()){
                var _f = com.chinahrt.w.mobiletransfer.entity.File()
                _f.name = f.name
                _f.isDir = f.isDirectory

                var selection = FileColumns.DATA+"=?"
                var selectionArgs = arrayOf(f.absolutePath)
                var cursor = cxt.contentResolver.query(uri,projection,selection,selectionArgs,sortOrder)
                if (null!=cursor&&cursor.moveToNext()){
                    var name = cursor.getString(cursor.getColumnIndex(FileColumns.DATA))
                    _f.path = name
                    var mimeType = cursor.getString(cursor.getColumnIndex(FileColumns.MIME_TYPE))
                    _f.mimeType = mimeType

                    var id = cursor.getInt(cursor.getColumnIndex(FileColumns._ID))
                    _f.id = id
                }
                if (TextUtils.isEmpty(_f.path)) _f.path = f.absolutePath
                list.add(_f)
            }
            result.put("list",list as Object)

        }

        val stringEntity = StringEntity(Gson().toJson(result), "utf-8")
        response?.entity = stringEntity
    }
}