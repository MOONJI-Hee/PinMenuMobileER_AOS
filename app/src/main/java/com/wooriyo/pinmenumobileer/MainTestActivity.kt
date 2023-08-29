package com.wooriyo.pinmenumobileer

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.wooriyo.pinmenumobileer.databinding.ActivityMainTestBinding
import com.wooriyo.pinmenumobileer.store.StoreListFragment

class MainTestActivity : BaseActivity() {
    lateinit var binding: ActivityMainTestBinding

    var isMain = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            icMain.setOnClickListener { goMain() }
            icPay.setOnClickListener { setNavi(it.id) }
            icQr.setOnClickListener { setNavi(it.id) }
            icPrint.setOnClickListener { setNavi(it.id) }
            icMore.setOnClickListener { setNavi(it.id) }
        }
    }

    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
    }

    fun goMain() {
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

    fun setNavi(id:Int) {
        if(isMain) {
            binding.run{
                bottomNav.setBackgroundColor(getColor(R.color.white))
                ivMain.setImageResource(R.drawable.ic_main_tabar_main_n_white)
                ivPay.setImageResource(R.drawable.icon_card_n_black)
                ivQr.setImageResource(R.drawable.icon_qr_n_black)
                ivPrint.setImageResource(R.drawable.icon_print_n_black)
                ivMore.setImageResource(R.drawable.ic_main_tabar_more_n_black)
                setTxtBlack(tvMain)
                setTxtBlack(tvPay)
                setTxtBlack(tvQr)
                setTxtBlack(tvPrint)
                setTxtBlack(tvMore)
            }
        }
        isMain = false

        when(id) {
            R.id.icPay -> {
                binding.ivPay.setImageResource(R.drawable.icon_card_p)
            }

            R.id.icQr -> {
                binding.ivQr.setImageResource(R.drawable.icon_qr_p)
            }

            R.id.icPrint -> {
                binding.ivPrint.setImageResource(R.drawable.icon_print_p)
            }

            R.id.icMore -> {
                binding.ivMore.setImageResource(R.drawable.ic_main_tabar_more_s)
            }
        }
    }

    fun setTxtBlack(tv:TextView) {
        tv.setTextColor(getColor(R.color.black))
    }

    fun setTxtWhite(tv:TextView) {
        tv.setTextColor(getColor(R.color.white))
    }
}