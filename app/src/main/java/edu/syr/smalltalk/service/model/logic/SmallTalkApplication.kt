package edu.syr.smalltalk.service.model.logic

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmallTalkApplication : Application() {
    private val database by lazy { SmallTalkRoomDatabase.getDatabase(this) }
    val repository by lazy { SmallTalkRepository(database.smallTalkDao()) }
}