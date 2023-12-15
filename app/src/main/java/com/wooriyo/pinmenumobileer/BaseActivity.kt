package com.wooriyo.pinmenumobileer

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenumobileer.common.dialog.LoadingDialog
import com.wooriyo.pinmenumobileer.util.AppHelper

open class BaseActivity: AppCompatActivity() {
    open lateinit var loadingDialog : LoadingDialog
    open lateinit var mActivity: Activity
    open lateinit var TAG: String

    companion object {
        var currentActivity: Activity? = null
    }

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivity = this
        TAG = mActivity.localClassName
        loadingDialog = LoadingDialog()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
        currentActivity = this
    }
}