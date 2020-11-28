package edu.syr.smalltalk.service.model.logic

import android.app.Application

class SmallTalkApplication : Application() {
    private val database by lazy { SmallTalkRoomDatabase.getDatabase(this) }
    val repository by lazy { SmallTalkRepository(database.smallTalkDao()) }
}