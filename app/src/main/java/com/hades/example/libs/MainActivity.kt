package com.hades.example.libs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hades.utility.android.utils.ThemeUtils
import com.hades.example.myapplication.R

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: " + ThemeUtils.dp2px(this, 10f));
    }
}