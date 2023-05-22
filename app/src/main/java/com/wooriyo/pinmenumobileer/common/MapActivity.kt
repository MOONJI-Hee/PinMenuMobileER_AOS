package com.wooriyo.pinmenumobileer.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityMapBinding
import com.wooriyo.pinmenumobileer.model.KakaoResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity() {
    lateinit var binding: ActivityMapBinding

    val TAG = "MapActivity"

    private lateinit var mapView : MapView
    private lateinit var marker : MapPOIItem

    var lat : Double = 35.815982818603516               //우리요 위도
    var long: Double = 127.11023712158203               //우리요 경도
    var addr = ""

    var check: Boolean = false                                   //주소 검색 여부 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this@MapActivity)
        mapView.setZoomLevel(1, true)

        binding.map.addView(mapView)

        marker = MapPOIItem()
        marker.tag = 0
        marker.itemName = ""
        marker.isShowCalloutBalloonOnTouch = false
        marker.markerType = MapPOIItem.MarkerType.BluePin

        addr = intent.getStringExtra("address").toString()
        val strLat = intent.getStringExtra("lat")
        val strLong = intent.getStringExtra("long")

        if(!strLat.isNullOrEmpty() && !strLong.isNullOrEmpty()) {
            lat = strLat.toDouble()
            long = strLong.toDouble()
        }
        setLocation()

        binding.back.setOnClickListener { finish() }
        binding.search.setOnClickListener { getCoordinate() }
        binding.save.setOnClickListener { save() }

        binding.etAddr.setOnEditorActionListener { v, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                getCoordinate()
            }
            true
        }
        binding.etAddr.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(check) check = false
            }
        })
    }

    // 좌표로 지도 중심, 마커 위치 설정
    private fun setLocation() {
        Log.d(TAG, "주소 $addr")
        Log.d(TAG, "위도 $lat")
        Log.d(TAG, "경도 $long")

        if(addr.isNotEmpty()) binding.etAddr.setText(addr)

        check = true

        val point = MapPoint.mapPointWithGeoCoord(lat, long)
        marker.mapPoint = point

        mapView.removeAllPOIItems()
        mapView.setMapCenterPoint(point, false)
        mapView.addPOIItem(marker)
    }

    // 카카오 API > 주소 검색해서 위치 찾기
    private fun getCoordinate() {
        addr = binding.etAddr.text.toString()

        if(addr.isEmpty()) return

        ApiClient.kakaoService().kakaoSearch("KakaoAK ${getString(R.string.kakao_apiKey)}", addr)
            .enqueue(object : Callback<KakaoResultDTO> {
                override fun onResponse(call: Call<KakaoResultDTO>, response: Response<KakaoResultDTO>) {
                    Log.d(TAG, "주소 검색 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body()
                    if(result == null)
                        return
                    else if(result.documents.isNotEmpty()) {
                        val addrDTO = result.documents[0]

                        addr = addrDTO.address_name
                        long = addrDTO.x.toDouble()
                        lat = addrDTO.y.toDouble()

                        setLocation()
                    }else
                        Toast.makeText(this@MapActivity, R.string.msg_no_search_result, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<KakaoResultDTO>, t: Throwable) {
                    Toast.makeText(this@MapActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주소 검색 실패 > $t")
                }
            })
    }

    fun save() {
        if(addr.isEmpty())
            Toast.makeText(this@MapActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        else if(!check)
            Toast.makeText(this@MapActivity, R.string.msg_check_addr, Toast.LENGTH_SHORT).show()
        else {
            intent.putExtra("lat", lat.toString())
            intent.putExtra("long", long.toString())
            intent.putExtra("address", addr)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}