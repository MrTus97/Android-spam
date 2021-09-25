package com.nhutlv.vietnam.quangnam.thangbinh.napchai;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.header_bar.*

class ActivityLove : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_love)

        tvTitle.text = "Chi Tiết"
        btnBack.setOnClickListener {
            finish()
        }
    }
}