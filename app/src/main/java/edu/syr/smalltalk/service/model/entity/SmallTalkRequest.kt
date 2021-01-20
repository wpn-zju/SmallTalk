package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.EntityConstant
import java.io.Serializable

@Entity(tableName = "small_talk_request")
data class SmallTalkRequest (
    @PrimaryKey
    @ColumnInfo(name = EntityConstant.REQUEST_INFO_REQUEST_ID)
    @SerializedName(EntityConstant.REQUEST_INFO_REQUEST_ID)
    val requestId: Int,
    @ColumnInfo(name = EntityConstant.REQUEST_INFO_REQUEST_TYPE)
    @SerializedName(EntityConstant.REQUEST_INFO_REQUEST_TYPE)
    val requestType: String,
    @ColumnInfo(name = EntityConstant.REQUEST_INFO_REQUEST_STATUS)
    @SerializedName(EntityConstant.REQUEST_INFO_REQUEST_STATUS)
    val requestStatus: String,
    @ColumnInfo(name = EntityConstant.REQUEST_INFO_REQUEST_METADATA)
    @SerializedName(EntityConstant.REQUEST_INFO_REQUEST_METADATA)
    val requestMetadata: String,
) : Serializable {
    fun requestEquals(other: SmallTalkRequest): Boolean {
        return requestId == other.requestId &&
                requestStatus == other.requestStatus &&
                requestType == other.requestType
    }
}
