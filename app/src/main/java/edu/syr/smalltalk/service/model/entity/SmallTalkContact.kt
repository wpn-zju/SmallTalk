package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "small_talk_contact")
data class SmallTalkContact (
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "user_email")
    val userEmail: String
) : Serializable