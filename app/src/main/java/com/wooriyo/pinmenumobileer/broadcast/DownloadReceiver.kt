package com.wooriyo.pinmenumobileer.broadcast

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenumobileer.R

class DownloadReceiver(context: Context): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
            val downResult = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val manager = context?.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
            val query: DownloadManager.Query = DownloadManager.Query().setFilterById(downResult)
            val cursor = manager.query(query)

            if (!cursor.moveToFirst()) {
                return
            }

            val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(index)
            cursor.close()
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Toast.makeText(context, R.string.msg_success_down, Toast.LENGTH_SHORT).show()
            } else if (status == DownloadManager.STATUS_FAILED) {
                Toast.makeText(context, R.string.msg_fail_down, Toast.LENGTH_SHORT).show()
            }
        }
    }
}