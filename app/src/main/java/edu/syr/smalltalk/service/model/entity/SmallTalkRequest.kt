package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.model.converter.JsonObjectConverter
import java.io.Serializable

@Entity(tableName = "small_talk_request")
data class SmallTalkRequest (
    @PrimaryKey
    @ColumnInfo(name = ServerConstant.ACC_REQUEST_SYNC__REQUEST_ID)
    @SerializedName(ServerConstant.ACC_REQUEST_SYNC__REQUEST_ID)
    val requestId: Int,
    @ColumnInfo(name = ServerConstant.ACC_REQUEST_SYNC__REQUEST_STATUS)
    @SerializedName(ServerConstant.ACC_REQUEST_SYNC__REQUEST_STATUS)
    val requestStatus: String,
    @ColumnInfo(name = ServerConstant.ACC_REQUEST_SYNC__REQUEST_TYPE)
    @SerializedName(ServerConstant.ACC_REQUEST_SYNC__REQUEST_TYPE)
    val requestType: String,
    @ColumnInfo(name = ServerConstant.ACC_REQUEST_SYNC__REQUEST_METADATA)
    @SerializedName(ServerConstant.ACC_REQUEST_SYNC__REQUEST_METADATA)
    @TypeConverters(JsonObjectConverter::class)
    val requestMetadata: JsonObject,
) : Serializable {
    fun requestEquals(other: SmallTalkRequest): Boolean {
        return requestId == other.requestId &&
                requestStatus == other.requestStatus &&
                requestType == other.requestType
    }
}
