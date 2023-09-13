package com.wooriyo.pinmenumobileer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeList
import com.wooriyo.pinmenumobileer.common.SelectStoreFragment
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ActivityMainTestBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.more.MoreFragment
import com.wooriyo.pinmenumobileer.payment.fragment.SetPayFragment
import com.wooriyo.pinmenumobileer.printer.PrinterMenuFragment
import com.wooriyo.pinmenumobileer.qr.QrAgreeActivity
import com.wooriyo.pinmenumobileer.qr.SetQrcodeFragment
import com.wooriyo.pinmenumobileer.store.StoreListFragment
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainTestBinding
    val TAG = "MainActivity"
    val mActivity = this

    @RequiresApi(33)
    val pms_noti : String = Manifest.permission.POST_NOTIFICATIONS

    var isMain = true

    val goQrAgree = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            goQr()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goMain()

        // 알림 권한 확인
        if(MyApplication.osver >= 33) {
            checkNotiPms()
        }

        binding.run {
            icMain.setOnClickListener { goMain() }
            icPay.setOnClickListener { setNavi(it.id) }
            icQr.setOnClickListener { setNavi(it.id) }
            icPrint.setOnClickListener { setNavi(it.id) }
            icMore.setOnClickListener { setNavi(it.id) }
        }
    }

    // SDK 33 이상에서 알림 권한 확인
    @RequiresApi(33)
    fun checkNotiPms() {
        when {
            ActivityCompat.checkSelfPermission(mActivity, pms_noti) == PackageManager.PERMISSION_DENIED -> getNotiPms()

            ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms_noti) -> {
                AlertDialog.Builder(mActivity)
                    .setTitle(R.string.pms_notification_title)
                    .setMessage(R.string.pms_notification_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getNotiPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }
        }
    }

    @RequiresApi(33)
    fun getNotiPms() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(pms_noti), AppProperties.REQUEST_NOTIFICATION)
    }

    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
    }

    private fun goMain() {
        if(!isMain) {
            binding.run{
                bottomNav.setBackgroundResource(R.drawable.bg_main_tabbar)
                ivMain.setImageResource(R.drawable.ic_main_tabar_main_s)
                ivPay.setImageResource(R.drawable.icon_card_n_white)
                ivQr.setImageResource(R.drawable.icon_qr_n_white)
                ivPrint.setImageResource(R.drawable.icon_print_n_white)
                ivMore.setImageResource(R.drawable.ic_main_tabar_more_n)
                tvMain.setTextColor(getColor(R.color.main))
                setTxtWhite(tvPay)
                setTxtWhite(tvQr)
                setTxtWhite(tvPrint)
                setTxtWhite(tvMore)
            }
        }
        isMain = true
        replace(StoreListFragment.newInstance())
    }

    private fun goSelStore(type: String) {
        replace(SelectStoreFragment.newInstance(type))
    }

    private fun goPay() {
//        binding.ivPay.setImageResource(R.drawable.icon_card_p)
        replace(SetPayFragment.newInstance())
    }

    private fun goQr() {
//        binding.ivQr.setImageResource(R.drawable.icon_qr_p)
        replace(SetQrcodeFragment.newInstance())
    }

    private fun goPrint() {
//        binding.ivPrint.setImageResource(R.drawable.icon_print_p)
        replace(PrinterMenuFragment.newInstance())
    }

    private fun goMore() {
//        binding.ivMore.setImageResource(R.drawable.ic_main_tabar_more_s)
        replace(MoreFragment.newInstance())
    }

    fun checkQrAgree(position: Int) {
        MyApplication.store = storeList[position]
        MyApplication.storeidx = storeList[position].idx
        if(storeList[position].agree == "Y")
            goQr()
        else
            goQrAgree.launch(Intent(mActivity, QrAgreeActivity::class.java))
    }

    private fun setNavi(id:Int) {
        if(isMain) {
            binding.run{
                bottomNav.setBackgroundColor(getColor(R.color.white))
                ivMain.setImageResource(R.drawable.ic_main_tabar_main_n_white)
                setTxtBlack(tvMain)
                setTxtBlack(tvPay)
                setTxtBlack(tvQr)
                setTxtBlack(tvPrint)
                setTxtBlack(tvMore)
            }
        }
        isMain = false

        binding.run {
            ivPay.setImageResource(R.drawable.icon_card_n_black)
            ivQr.setImageResource(R.drawable.icon_qr_n_black)
            ivPrint.setImageResource(R.drawable.icon_print_n_black)
            ivMore.setImageResource(R.drawable.ic_main_tabar_more_n_black)
        }

        when(id) {
            R.id.icPay -> {
                when(MyApplication.storeList.size) {
                    0 -> Toast.makeText(mActivity, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                    1 -> insPaySetting(0)
                    else ->  {
                        binding.ivPay.setImageResource(R.drawable.icon_card_p)
                        goSelStore("pay")
                    }
                }
            }

            R.id.icQr -> {
                when(MyApplication.storeList.size) {
                    0 -> Toast.makeText(mActivity, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                    1 -> checkQrAgree(0)
                    else -> {
                        binding.ivQr.setImageResource(R.drawable.icon_qr_p)
                        goSelStore("qr")
                    }
                }
            }

            R.id.icPrint -> {
                when(MyApplication.storeList.size) {
                    0 -> Toast.makeText(mActivity, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                    1 -> insPrintSetting(0)
                    else -> {
                        binding.ivPrint.setImageResource(R.drawable.icon_print_p)
                        goSelStore("print")
                    }
                }
            }

            R.id.icMore -> goMore()
        }
    }

    fun setTxtBlack(tv:TextView) {
        tv.setTextColor(getColor(R.color.black))
    }

    fun setTxtWhite(tv:TextView) {
        tv.setTextColor(getColor(R.color.white))
    }

    fun insPaySetting(position: Int) {
        ApiClient.service.insPaySetting(MyApplication.useridx, MyApplication.storeList[position].idx, MyApplication.androidId)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    if(result.status == 1) {
                        MyApplication.store = MyApplication.storeList[position]
                        MyApplication.storeidx = MyApplication.storeList[position].idx
                        MyApplication.bidx = result.bidx
                        goPay()
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 오류 >> $t")
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 오류 >> ${call.request()}")
                }
            })
    }

    fun insPrintSetting(position: Int) {
        ApiClient.service.insPrintSetting(MyApplication.useridx, MyApplication.storeList[position].idx, MyApplication.androidId)
            .enqueue(object : retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    if(result.status == 1){
                        MyApplication.store = MyApplication.storeList[position]
                        MyApplication.storeidx = MyApplication.storeList[position].idx
                        MyApplication.bidx = result.bidx
                        goPrint()
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 오류 >> $t")
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 오류 >> ${call.request()}")
                }
            })
    }
}