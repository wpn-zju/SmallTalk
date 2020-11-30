package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.model.converter.InstantConverter
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "small_talk_message")
data class SmallTalkMessage (
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    val userId: Int,
    @ColumnInfo(name = "chat_id")
    @SerializedName("chat_id")
    val chatId: Int,
    @ColumnInfo(name = ServerConstant.CHAT_NEW_MESSAGE__SENDER)
    @SerializedName(ServerConstant.CHAT_NEW_MESSAGE__SENDER)
    val sender: Int,
    @ColumnInfo(name = ServerConstant.CHAT_NEW_MESSAGE__RECEIVER)
    @SerializedName(ServerConstant.CHAT_NEW_MESSAGE__RECEIVER)
    val receiver: Int,
    @ColumnInfo(name = ServerConstant.CHAT_NEW_MESSAGE__CONTENT)
    @SerializedName(ServerConstant.CHAT_NEW_MESSAGE__CONTENT)
    val content: String,
    @ColumnInfo(name = ServerConstant.CHAT_NEW_MESSAGE__CONTENT_TYPE)
    @SerializedName(ServerConstant.CHAT_NEW_MESSAGE__CONTENT_TYPE)
    val contentType: String,
    @ColumnInfo(name = ServerConstant.TIMESTAMP)
    @SerializedName(ServerConstant.TIMESTAMP)
    @TypeConverters(InstantConverter::class)
    val timestamp: Instant
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    @SerializedName("message_id")
    var messageId: Int = 0
}
