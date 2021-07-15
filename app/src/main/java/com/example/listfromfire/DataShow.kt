package com.example.listfromfire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_data_show.*

class DataShow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_show)

        textView_Title.text = intent.extras?.getString("Title Key")
        textView_Note.text = intent.extras?.getString("Note Key")
    }
}