package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ServerConstant
import java.io.Serializable

@Entity(tableName = "small_talk_contact")
data class SmallTalkContact (
    @PrimaryKey
    @ColumnInfo(name = ServerConstant.ACC_CONTACT_SYNC__CONTACT_ID)
    @SerializedName(ServerConstant.ACC_CONTACT_SYNC__CONTACT_ID)
    val contactId: Int,
    @ColumnInfo(name = ServerConstant.ACC_CONTACT_SYNC__CONTACT_NAME)
    @SerializedName(ServerConstant.ACC_CONTACT_SYNC__CONTACT_NAME)
    val contactName: String,
    @ColumnInfo(name = ServerConstant.ACC_CONTACT_SYNC__CONTACT_EMAIL)
    @SerializedName(ServerConstant.ACC_CONTACT_SYNC__CONTACT_EMAIL)
    val contactEmail: String
) : Serializable
