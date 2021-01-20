package edu.syr.smalltalk.service.model.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ClientConstant
import edu.syr.smalltalk.service.model.converter.InstantConverter
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "small_talk_message")
data class SmallTalkMessage (
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    val userId: Int,
    @ColumnInfo(name = "is_group")
    @SerializedName("is_group")
    val isGroup: Boolean,
    @ColumnInfo(name = "has_read")
    @SerializedName("has_read")
    val hasRead: Boolean,
    @ColumnInfo(name = "chat_id")
    @SerializedName("chat_id")
    val chatId: Int,
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
    @ColumnInfo(name = ClientConstant.TIMESTAMP)
    @SerializedName(ClientConstant.TIMESTAMP)
    @TypeConverters(InstantConverter::class)
    val timestamp: Instant
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    @SerializedName("message_id")
    var messageId: Int = 0
}

data class SmallTalkRecentMessage (
    @Embedded
    val message: SmallTalkMessage,
    @ColumnInfo(name = "unread_num")
    val unreadNum: Int
) : Serializable
