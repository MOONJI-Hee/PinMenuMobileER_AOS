package com.wooriyo.pinmenumobileer.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.AlertDialog
import com.wooriyo.pinmenumobileer.common.ConfirmDialog
import com.wooriyo.pinmenumobileer.databinding.FragmentMoreBinding
import com.wooriyo.pinmenumobileer.member.LoginActivity
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoreFragment : Fragment() {
    lateinit var binding: FragmentMoreBinding
    val TAG = "MoreFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(layoutInflater)

        binding.run {
            udtMbr.setOnClickListener { startActivity(Intent(context, MemberSetActivity::class.java)) }
            versionInfo.setOnClickListener {
                val content = getString(R.string.dialog_version).format(MyApplication.appver)
                AlertDialog("", content, 0).show((activity as MainActivity).supportFragmentManager, "VersionDialog")
            }
            logout.setOnClickListener {
                val onClickListener = View.OnClickListener {logout()}
                ConfirmDialog("", getString(R.string.dialog_logout), getString(R.string.btn_confirm), onClickListener).show((activity as MainActivity).supportFragmentManager, "LogoutDialog")
            }
            drop.setOnClickListener {
                val onClickListener = View.OnClickListener {dropMbr()}
                ConfirmDialog("", getString(R.string.dialog_drop), getString(R.string.btn_confirm), onClickListener).show((activity as MainActivity).supportFragmentManager, "DropDialog")
            }
        }
        return binding.root
    }

    fun logout() {
        MyApplication.pref.logout()
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun dropMbr() {
        ApiClient.service.dropMbr(MyApplication.useridx).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "회원 탈퇴 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        logout()
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "회원 탈퇴 실패 >> $t")
                Log.d(TAG, "회원 탈퇴 실패 >> ${call.request()}")
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MoreFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}