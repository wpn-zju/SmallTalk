package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "small_talk_contact")
data class SmallTalkContact (
    @PrimaryKey
    @ColumnInfo(name = "contact_id")
    @SerializedName("contact_id")
    val contactId: Int,
    @ColumnInfo(name = "contact_name")
    @SerializedName("contact_name")
    val contactName: String,
    @ColumnInfo(name = "contact_email")
    @SerializedName("contact_email")
    val contactEmail: String
) : Serializable