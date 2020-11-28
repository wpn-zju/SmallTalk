package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.model.converter.InstantConverter
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "small_talk_message")
data class SmallTalkMessage (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    @SerializedName("message_id")
    val messageId: Int,
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    val userId: Int,
    @ColumnInfo(name = "sender")
    @SerializedName("sender")
    val sender: Int,
    @ColumnInfo(name = "receiver")
    @SerializedName("receiver")
    val receiver: Int,
    @ColumnInfo(name = "content")
    @SerializedName("content")
    val content: String,
    @ColumnInfo(name = "content_type")
    @SerializedName("content_type")
    val contentType: String,
    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    @TypeConverters(InstantConverter::class)
    val timestamp: Instant
) : Serializable
