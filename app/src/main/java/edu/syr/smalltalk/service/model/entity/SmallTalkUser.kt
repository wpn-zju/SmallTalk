package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.EntityConstant
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import java.io.Serializable

@Entity(tableName = "small_talk_user")
data class SmallTalkUser (
    @PrimaryKey
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_ID)
    @SerializedName(EntityConstant.USER_INFO_USER_ID)
    val userId: Int,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_SESSION)
    @SerializedName(EntityConstant.USER_INFO_USER_SESSION)
    val userSession: String,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_EMAIL)
    @SerializedName(EntityConstant.USER_INFO_USER_EMAIL)
    val userEmail: String,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_NAME)
    @SerializedName(EntityConstant.USER_INFO_USER_NAME)
    val userName: String,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_PASSWORD)
    @SerializedName(EntityConstant.USER_INFO_USER_PASSWORD)
    val userPassword: String,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_CONTACT_LIST)
    @SerializedName(EntityConstant.USER_INFO_USER_CONTACT_LIST)
    @TypeConverters(IntArrayConverter::class)
    val contactList: List<Int>,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_GROUP_LIST)
    @SerializedName(EntityConstant.USER_INFO_USER_GROUP_LIST)
    @TypeConverters(IntArrayConverter::class)
    val groupList: List<Int>,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_REQUEST_LIST)
    @SerializedName(EntityConstant.USER_INFO_USER_REQUEST_LIST)
    @TypeConverters(IntArrayConverter::class)
    val requestList: List<Int>,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_GENDER)
    @SerializedName(EntityConstant.USER_INFO_USER_GENDER)
    val userGender: Int,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_LOCATION)
    @SerializedName(EntityConstant.USER_INFO_USER_LOCATION)
    val userLocation: String,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_INFO)
    @SerializedName(EntityConstant.USER_INFO_USER_INFO)
    val userInfo: String,
    @ColumnInfo(name = EntityConstant.USER_INFO_USER_AVATAR_LINK)
    @SerializedName(EntityConstant.USER_INFO_USER_AVATAR_LINK)
    val userAvatarLink: String,
) : Serializable
