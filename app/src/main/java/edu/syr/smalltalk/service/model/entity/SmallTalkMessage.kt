package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import edu.syr.smalltalk.service.model.converter.InstantConverter
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "small_talk_message")
data class SmallTalkMessage (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    val messageId: Int,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "sender")
    val sender: Int,
    @ColumnInfo(name = "receiver")
    val receiver: Int,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "content_type")
    val contentType: String,
    @ColumnInfo(name = "timestamp")
    @TypeConverters(InstantConverter::class)
    val timestamp: Instant
) : Serializable
