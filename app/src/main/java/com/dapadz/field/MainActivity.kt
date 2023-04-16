package com.dapadz.field

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val picker = findViewById<DeltaDatePicker>(R.id.testDate)
        val a = Calendar.getInstance().apply {
            set(2023, 12, 12)
        }
        val b = Calendar.getInstance().apply {
            set(2000, 1, 12)
        }
        picker.maxDate = a.timeInMillis
        picker.minDate = b.timeInMillis
//        picker.yearMinValue = 0
//        picker.yearMaxValue = 10
//        picker.mountMinValue = 0
//        picker.mountMaxValue = 20
//        picker.wrapSelectorWheel = true
//        picker.setTextColor(R.color.red)
    }
}