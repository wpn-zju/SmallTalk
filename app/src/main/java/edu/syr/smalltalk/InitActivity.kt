package edu.syr.smalltalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_mail.*


class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        // TODO put into stack or not?
        // TODO check id of containers
//        supportFragmentManager.beginTransaction().replace(R.id.initContainer, MailFragment())
//            .commit()
    }
}