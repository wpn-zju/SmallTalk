package edu.syr.smalltalk.service.model.logic

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.syr.smalltalk.service.model.converter.InstantConverter
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import edu.syr.smalltalk.service.model.converter.JsonObjectConverter
import edu.syr.smalltalk.service.model.entity.*

@Database(entities = [
    SmallTalkUser::class,
    SmallTalkContact::class,
    SmallTalkGroup::class,
    SmallTalkRequest::class,
    SmallTalkMessage::class],
    version = 1, exportSchema = false)

@TypeConverters(IntArrayConverter::class, InstantConverter::class, JsonObjectConverter::class)
abstract class SmallTalkRoomDatabase : RoomDatabase() {
    abstract fun smallTalkDao(): SmallTalkDao

    companion object {
        @Volatile
        private var INSTANCE: SmallTalkRoomDatabase? = null

        fun getDatabase(context: Context): SmallTalkRoomDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmallTalkRoomDatabase::class.java,
                    "small_talk_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}