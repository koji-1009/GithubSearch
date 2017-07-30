package com.app.dr1009.githubsearch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class LegalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal)

        val textView = findViewById(R.id.legal_text) as TextView
        resources.openRawResource(R.raw.legal).use {
            val byteArray = ByteArray(it.available())
            it.read(byteArray)
            textView.text = String(byteArray)
        }
    }
}
