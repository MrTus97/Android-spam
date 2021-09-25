package com.nhutlv.vietnam.quangnam.thangbinh.napchai;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_bar.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBack.visibility = View.GONE
        tvTitle.text = "Home"

        textView.setOnClickListener {
            startActivity(Intent(this, ActivityChiTiet::class.java))
        }

        napchai.setOnClickListener {
            startActivity(Intent(this, InAapActivity::class.java))
        }

        imageView.setOnClickListener {
            startActivity(Intent(this, ActivityLove::class.java))
        }
    }
}