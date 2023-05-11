package com.dapadz.field

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    var a = false
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val picker = findViewById<Keyboard>(R.id.testDate)
        picker.setRightActionIcon(R.drawable.baseline_arrow_back_24)
        picker.setActionClickListener(object : KeyboardActionClickListener {
            override fun onRightActionClick() {
                val icon = if (a) R.drawable.baseline_close_24 else null
                picker.setRightActionIcon(icon)
                a = !a
            }
        })
    }
}