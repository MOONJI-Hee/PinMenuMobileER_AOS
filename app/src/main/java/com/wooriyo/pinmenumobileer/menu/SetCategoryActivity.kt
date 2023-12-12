package com.wooriyo.pinmenumobileer.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetCategoryBinding
import java.text.Bidi

class SetCategoryActivity : BaseActivity() {
    lateinit var binding: ActivitySetCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}