package com.chinahrt.w.mobiletransfer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.chinahrt.w.mobiletransfer.handler.RequestTestHandler
import com.chinahrt.w.mobiletransfer.handler.image.ImageListHandler
import com.chinahrt.w.mobiletransfer.handler.image.ImageOriginalHandler
import com.chinahrt.w.mobiletransfer.handler.image.ImageThumbnailHandler
import com.chinahrt.w.mobiletransfer.utils.NetWorkUtil
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import com.yanzhenjie.andserver.website.AssetsWebsite
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import android.widget.Toast
import com.chinahrt.w.mobiletransfer.handler.clipboard.GetClipboardHandler
import com.chinahrt.w.mobiletransfer.handler.clipboard.PostClipboardHandler
import com.chinahrt.w.mobiletransfer.handler.file.FileListHandler
import com.chinahrt.w.mobiletransfer.handler.image.ImageFolderHandler
import com.chinahrt.w.mobiletransfer.utils.ImageUtil
import com.yanzhenjie.andserver.website.WebSite


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,EasyPermissions.PermissionCallbacks {



    private var context: Context? = null

    private val mListener = object : Server.Listener {
        override fun onStarted() {
            // 服务器启动成功.
            Log.i("","服务器启动成功.")

            urlTv.text = "http://"+ ip+":"+port
            urlTv.setBackgroundColor((Color.parseColor("#259a76")))
        }

        override fun onStopped() {
            // 服务器停止了，一般是开发者调用server.stop()才会停止。
            Log.i("","服务器停止了")

            urlTv.text = "点击启动服务"
            urlTv.setBackgroundColor(Color.parseColor("#259a76"))
        }

        override fun onError(e: Exception) {
            // 服务器启动发生错误，一般是端口被占用。
            Log.i("","onError"+e.message)

            urlTv.text = "启动服务失败"
            urlTv.setBackgroundColor(Color.parseColor("#ec1c24"))
        }
    }

    private var webSite: WebSite? = null
    private var andServer:AndServer? = null
    private var server:Server? = null
    private var port = 8081
    private var ip:String = ""

    private val TAG = MainActivity::class.java.name


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        context = this

        title = "文件传送"

        ip = NetWorkUtil.getWifiIpAddress(context as MainActivity)!!

        when {
            null==ip -> {
                urlTv.text = "没有网络连接,请检查"
                urlTv.setBackgroundColor(Color.parseColor("#ec1c24"))
            }
            "mobile".equals(ip,true) -> {
                urlTv.text = "没有WIFI连接,请检查"
                urlTv.setBackgroundColor(Color.parseColor("#ec1c24"))
            }
            else -> {
                startServer()
            }
        }

        urlTv.setOnClickListener({
            startServer()
        })

    }


    @AfterPermissionGranted(100)
    private fun startServer(){
        if (EasyPermissions.hasPermissions(context,Manifest.permission.READ_EXTERNAL_STORAGE)){

            //初始化图片
            ImageUtil.getImage(this)

            urlTv.text = "http://"+ ip+":"+port

            if (webSite==null)
                webSite = AssetsWebsite(assets, "")

            if (andServer==null){
                andServer = AndServer.Build()
                    .port(port)     //设置端口号
                    .website(webSite)
                    .listener(mListener)
                    .registerHandler("test", RequestTestHandler())
                    .registerHandler("image/list", ImageListHandler(context as MainActivity))
                    .registerHandler("image/thumbnail", ImageThumbnailHandler(context as MainActivity))
                    .registerHandler("image/full", ImageOriginalHandler(context as MainActivity))
                    .registerHandler("image/folder", ImageFolderHandler(context as MainActivity))
                    .registerHandler("file/list", FileListHandler(context as MainActivity))
                    .registerHandler("clip/get", GetClipboardHandler(context as MainActivity))
                        .registerHandler("clip/post", PostClipboardHandler(context as MainActivity))
                    .timeout(10 * 1000)      //设置连接超时时间
                    .build()
            }

            //创建服务器
            if (null==server){
                server = andServer!!.createServer()
            }

            //启动服务器
            if (!server!!.isRunning)
                server!!.start()

        }else{
            EasyPermissions.requestPermissions(this,"需要使用存储权限,您是否同意?",100,Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }


    /**
     * 权限请求回调，提示用户之后，用户点击“允许”或者“拒绝”之后调用此方法
     * @param requestCode  定义的权限编码
     * @param permissions 权限名称
     * @param grantResults 允许/拒绝
     */
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms!!)){
            AppSettingsDialog.Builder(this).build().show()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        //startServer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            Toast.makeText(this, "已拒绝权限并不再询问" , Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
