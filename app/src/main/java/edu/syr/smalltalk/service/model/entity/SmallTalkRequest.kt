package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.model.converter.JsonObjectConverter
import java.io.Serializable

@Entity(tableName = "small_talk_request")
data class SmallTalkRequest (
    @PrimaryKey
    @ColumnInfo(name = "request_id")
    @SerializedName("request_id")
    val requestId: Int,
    @ColumnInfo(name = "request_status")
    @SerializedName("request_status")
    val requestStatus: String,
    @ColumnInfo(name = "request_type")
    @SerializedName("request_type")
    val requestType: String,
    @ColumnInfo(name = "request_metadata")
    @SerializedName("request_metadata")
    @TypeConverters(JsonObjectConverter::class)
    val requestMetadata: JsonObject,
) : Serializable