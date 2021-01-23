package edu.syr.smalltalk.service.model.entity

import android.content.Context
import androidx.room.*
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.R
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

    fun getContentPreview(context: Context): String {
        return when (contentType) {
            ClientConstant.CHAT_CONTENT_TYPE_TEXT -> content
            ClientConstant.CHAT_CONTENT_TYPE_IMAGE -> context.getString(R.string.notification_image)
            ClientConstant.CHAT_CONTENT_TYPE_AUDIO -> context.getString(R.string.notification_audio)
            ClientConstant.CHAT_CONTENT_TYPE_VIDEO -> context.getString(R.string.notification_video)
            ClientConstant.CHAT_CONTENT_TYPE_FILE -> context.getString(R.string.notification_file)
            else -> context.getString(R.string.notification_others)
        }
    }
}

data class SmallTalkRecentMessage (
    @Embedded
    val message: SmallTalkMessage,
    @ColumnInfo(name = "unread_num")
    val unreadNum: Int
) : Serializable
