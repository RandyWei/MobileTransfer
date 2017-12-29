package com.chinahrt.w.mobiletransfer.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


/**
 * Created by W on 2017/11/29.
 */
object NetWorkUtil {

    fun getWifiIpAddress(context:Context):String?{
        var info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected){
            return if (info.type == ConnectivityManager.TYPE_MOBILE){
                "mobile"
            }else{
                var wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                var wifiInfo = wifiManager.connectionInfo
                intIP2StringIP(wifiInfo.ipAddress)
            }
        }
        return null
    }

    fun getIPAddress(context: Context): String? {

        val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.getInetAddresses()
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }

            } else if (info.type == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIP2StringIP(wifiInfo.ipAddress)
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}