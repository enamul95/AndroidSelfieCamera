package com.era.androidselfiecamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var btnSelfie:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSelfie = findViewById(R.id.btnSelfie);

        btnSelfie.setOnClickListener {
            Toast.makeText(applicationContext,"Selfie Camera",Toast.LENGTH_LONG).show()
        }
    }
}