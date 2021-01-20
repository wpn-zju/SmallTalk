package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.EntityConstant
import java.io.Serializable

@Entity(tableName = "small_talk_contact")
data class SmallTalkContact (
    @PrimaryKey
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_ID)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_ID)
    val contactId: Int,
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_EMAIL)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_EMAIL)
    val contactEmail: String,
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_NAME)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_NAME)
    val contactName: String,
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_GENDER)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_GENDER)
    val contactGender: Int,
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_LOCATION)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_LOCATION)
    val contactLocation: String,
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_INFO)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_INFO)
    val contactInfo: String,
    @ColumnInfo(name = EntityConstant.CONTACT_INFO_CONTACT_AVATAR_LINK)
    @SerializedName(EntityConstant.CONTACT_INFO_CONTACT_AVATAR_LINK)
    val contactAvatarLink: String,
) : Serializable {
    fun contactEquals(other: SmallTalkContact): Boolean {
        return contactId == other.contactId &&
                contactName == other.contactName &&
                contactEmail == other.contactEmail &&
                contactGender == other.contactGender &&
                contactLocation == other.contactLocation &&
                contactInfo == other.contactInfo &&
                contactAvatarLink == other.contactAvatarLink
    }
}
