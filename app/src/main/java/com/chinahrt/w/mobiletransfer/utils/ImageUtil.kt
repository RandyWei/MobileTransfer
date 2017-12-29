package com.chinahrt.w.mobiletransfer.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import com.chinahrt.w.mobiletransfer.entity.Image
import com.chinahrt.w.mobiletransfer.entity.ImageFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FilenameFilter
import java.io.IOException


/**
 * Created by W on 2017/12/1.
 */
object ImageUtil {

    var image:Image? = null

    private val STORE_IMAGES = arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATA)

    fun getImage(cxt:Context): Image? {
        if (image == null){
            var cursor = cxt.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,STORE_IMAGES,null,null, MediaStore.Images.Media.DATE_TAKEN + " desc")

            if (null!=cursor) {

                image = Image()

                //一个辅助集合，防止同一目录被扫描多次
                var dirPaths = HashSet<String>()
                image!!.count = cursor.count
                var list = ArrayList<Int>()//所有图片ID
                var folders = ArrayList<ImageFolder>()//所有文件夹
                while (cursor.moveToNext()) {
                    var id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    list.add(id)
                    var bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                    var path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))


                    var imageFolder:ImageFolder? = null

                    var parentFile = File(path).parentFile ?:continue

                    var dirPath = parentFile.absolutePath

                    if (dirPaths.contains(dirPath)){
                        continue
                    }else{
                        dirPaths.add(dirPath)


                        //判断一下是否dirPath不同，但是bucketName相同

                        var isNew = true

                        for (_imageFolder in folders){
                            if (_imageFolder.name.equals(bucketName)){
                                imageFolder = _imageFolder
                                isNew = false
                                break
                            }
                        }

                        if (isNew){
                            imageFolder = ImageFolder()

                            //判断是否有封面
                            if (imageFolder.cover == null)
                                imageFolder.cover = id

                            imageFolder.name = bucketName
                        }
                    }

                    val arrayCount = parentFile.list { _, filename ->
                        (filename.endsWith(".jpg")
                                || filename.endsWith(".png")
                                || filename.endsWith(".jpeg"))
                    }.size
                    imageFolder!!.count = arrayCount
                    if (!folders.contains(imageFolder) && arrayCount>0)
                        folders.add(imageFolder)

                }

                image!!.list = list
                image!!.folders = folders
            }
        }
        return image
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    fun bitmapToBase64(bitmap: Bitmap?): String? {

        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                baos!!.flush()
                baos!!.close()

                val bitmapBytes = baos!!.toByteArray()
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos!!.flush()
                    baos!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return result
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    fun base64ToBitmap(base64Data: String): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}