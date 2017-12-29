package com.chinahrt.w.mobiletransfer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by W on 2017/12/5.
 */
class ConnectChangeReceiver: BroadcastReceiver() {

    private val TAG = ConnectChangeReceiver::class.java.name

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG,intent?.action)


    }
}