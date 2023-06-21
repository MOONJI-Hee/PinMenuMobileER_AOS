package com.wooriyo.pinmenumobileer.broadcast

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wooriyo.pinmenumobileer.MyApplication
import java.io.IOException

class BtConnectReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

        val action = intent.action
        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            Toast.makeText(context, "BlueTooth Connect", Toast.LENGTH_SHORT).show()
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
            try {
                if (MyApplication.bluetoothPort.isConnected) MyApplication.bluetoothPort.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (MyApplication.btThread != null) {
                if(MyApplication.btThread!!.isAlive) {
                    MyApplication.btThread!!.interrupt()
                    MyApplication.btThread = null
                }
            }
//                ConnectionFailedDevice()
            Toast.makeText(context, "BlueTooth Disconnect", Toast.LENGTH_SHORT).show()
        }
    }
}