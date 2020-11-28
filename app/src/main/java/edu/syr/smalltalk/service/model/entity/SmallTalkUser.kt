package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import java.io.Serializable

@Entity(tableName = "small_talk_user")
data class SmallTalkUser (
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "user_session")
    val userSession: String,
    @ColumnInfo(name = "user_email")
    val userEmail: String,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "user_password")
    val userPassword: String,
    @ColumnInfo(name = "contact_list")
    @TypeConverters(IntArrayConverter::class)
    val contactList: List<Int>,
    @ColumnInfo(name = "group_list")
    @TypeConverters(IntArrayConverter::class)
    val groupList: List<Int>,
    @ColumnInfo(name = "request_list")
    @TypeConverters(IntArrayConverter::class)
    val requestList: List<Int>
) : Serializable
