package com.wooriyo.pinmenumobileer.store

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.FragmentStoreListBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.model.StoreListDTO
import com.wooriyo.pinmenumobileer.store.adapter.StoreAdapter
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreListFragment : Fragment() {
    lateinit var binding: FragmentStoreListBinding
    val TAG = "StoreListFragment"

    var storeAdapter = StoreAdapter(MyApplication.storeList)

    companion object {
        var fragment : StoreListFragment? = null

        fun newInstance(): StoreListFragment {
            if(fragment == null) {
                fragment = StoreListFragment()
            }
            val args = Bundle()

            fragment!!.arguments = args
            return fragment!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 메인화면 돌아왔을 때 전역변수 초기화
        MyApplication.useridx = MyApplication.pref.getUserIdx()
        MyApplication.storeidx = 0
        MyApplication.setStoreDTO()

        // 어댑터 클릭이벤트
        storeAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onStoreClick(storeDTO: StoreDTO, intent: Intent) {
                checkDeviceLimit(storeDTO, intent)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStoreListBinding.inflate(layoutInflater)

        // 매장 리사이클러뷰, 어댑터 초기화
        binding.rvStore.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvStore.adapter = storeAdapter

        // 매장리스트 배경 최소 높이 지정 (최소 화면을 덮을 정도로)
//        binding.storeArea.minHeight = MyApplication.height - (84 * MyApplication.density).toInt()
        binding.storeArea.minHeight = binding.root.height

        // 우측 상단에 userid, 알파요 연동여부 출력
        val member = MyApplication.pref.getMbrDTO()
        if(member != null) {
            binding.userid.text = member.userid.substringBefore("@")

            if(member.arpayoid.isNullOrEmpty())
                binding.arpayo.text = getString(R.string.arpayo_dis_conn)
        }

        binding.version.text = "Ver ${MyApplication.appver}"

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            binding.storeArea.minHeight = binding.root.height
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getStoreList()
    }

    // 매장리스트 조회 Api
    fun getStoreList() {
        ApiClient.service.getStoreList(MyApplication.useridx)
            .enqueue(object: retrofit2.Callback<StoreListDTO>{
                override fun onResponse(call: Call<StoreListDTO>, response: Response<StoreListDTO>) {
                    Log.d(TAG, "매장 리스트 조회 url : $response")
                    if(response.isSuccessful) {
                        val storeListDTO = response.body() ?: return
                        if(storeListDTO.status == 1) {
                            MyApplication.manualPdf = storeListDTO.manual

                            MyApplication.storeList.clear()
                            MyApplication.storeList.addAll(storeListDTO.storeList)

                            if(MyApplication.storeList.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rvStore.visibility = View.GONE

                                val intent = Intent(context, RegStoreActivity::class.java)
                                intent.putExtra("pre", "Main")
                                startActivity(intent)
                            }else {
                                binding.empty.visibility = View.GONE
                                binding.rvStore.visibility = View.VISIBLE
                                storeAdapter.notifyDataSetChanged()
                                AppHelper.setRvHeight(binding.rvStore, MyApplication.storeList.size, 155)
                            }
                        }else Toast.makeText(context, storeListDTO.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoreListDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 리스트 조회 오류 > $t")
                    Log.d(TAG, "매장 리스트 조회 오류 > ${call.request()}")
                }
            })
    }

    fun checkDeviceLimit(store: StoreDTO, intent: Intent) {
        ApiClient.service.checkDeviceLimit(
            MyApplication.useridx, store.idx, MyApplication.pref.getToken().toString(), MyApplication.androidId, 0)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "이용자수 체크 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    if(result.status == 1){
                        MyApplication.store = store
                        MyApplication.storeidx = store.idx
                        startActivity(intent)
                    }else
                        Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "이용자수 체크 오류 > $t")
                    Log.d(TAG, "이용자수 체크 오류 > ${call.request()}")
                }
            })
    }
}