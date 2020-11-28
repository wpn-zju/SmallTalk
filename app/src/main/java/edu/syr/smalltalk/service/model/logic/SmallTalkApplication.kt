package edu.syr.smalltalk.service.model.logic

import android.app.Application
import edu.syr.smalltalk.service.ISmallTalkService

class SmallTalkApplication : Application() {
    lateinit var service: ISmallTalkService
    val database by lazy { SmallTalkRoomDatabase.getDatabase(this) }
    val repository by lazy { SmallTalkRepository(service, database.smallTalkDao()) }
}