package edu.syr.smalltalk.init

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.syr.smalltalk.MainActivity
import edu.syr.smalltalk.R
import kotlinx.android.synthetic.main.fragment_auth_code.*
import kotlinx.android.synthetic.main.fragment_password.*


class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
    }

}