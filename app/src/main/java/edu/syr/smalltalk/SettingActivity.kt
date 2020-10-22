package edu.syr.smalltalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        findViewById<Button>(R.id.button_preferences).setOnClickListener {
            var fragment = supportFragmentManager.findFragmentById(R.id.preferencesContainer)
            if (fragment == null) {
                fragment = PrefFragment()
                supportFragmentManager.beginTransaction().add(R.id.preferencesContainer, fragment)
                    .commit()
                findViewById<Button>(R.id.button_preferences).text = "Done"
            } else {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
                findViewById<Button>(R.id.button_preferences).text = "System Preferences"
            }
        }
    }
}