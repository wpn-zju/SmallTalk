package edu.syr.smalltalk.service.model.logic

import android.app.Application

class SmallTalkApplication : Application() {
    private val database by lazy { SmallTalkRoomDatabase.getDatabase(this) }
    val repository by lazy { SmallTalkRepository(database.smallTalkDao()) }

    companion object {
        // const val BASE_URL = "http://192.168.1.224:8079"
        const val BASE_URL = "http://smalltalknow.com:8079"
        // const val BASE_URL = "https://hk.smalltalknow.com:8079"
    }
}
