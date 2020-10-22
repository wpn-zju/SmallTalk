package edu.syr.smalltalk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_hello.*

class HelloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)
        // TODO: carry parameters to next activity
        // use apply with putString?
        button_signIn.setOnClickListener {
            startActivity(Intent(this, InitActivity::class.java))
        }
        button_singUp.setOnClickListener {
            startActivity(Intent(this, InitActivity::class.java))
        }
    }
}