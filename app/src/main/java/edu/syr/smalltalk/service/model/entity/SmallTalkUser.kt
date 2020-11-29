package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import java.io.Serializable

@Entity(tableName = "small_talk_user")
data class SmallTalkUser (
    @PrimaryKey
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__USER_ID)
    @SerializedName(ServerConstant.ACC_USER_SYNC__USER_ID)
    val userId: Int,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__USER_SESSION)
    @SerializedName(ServerConstant.ACC_USER_SYNC__USER_SESSION)
    val userSession: String,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__USER_EMAIL)
    @SerializedName(ServerConstant.ACC_USER_SYNC__USER_EMAIL)
    val userEmail: String,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__USER_NAME)
    @SerializedName(ServerConstant.ACC_USER_SYNC__USER_NAME)
    val userName: String,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__USER_PASSWORD)
    @SerializedName(ServerConstant.ACC_USER_SYNC__USER_PASSWORD)
    val userPassword: String,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__CONTACT_LIST)
    @SerializedName(ServerConstant.ACC_USER_SYNC__CONTACT_LIST)
    @TypeConverters(IntArrayConverter::class)
    val contactList: List<Int>,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__GROUP_LIST)
    @SerializedName(ServerConstant.ACC_USER_SYNC__GROUP_LIST)
    @TypeConverters(IntArrayConverter::class)
    val groupList: List<Int>,
    @ColumnInfo(name = ServerConstant.ACC_USER_SYNC__REQUEST_LIST)
    @SerializedName(ServerConstant.ACC_USER_SYNC__REQUEST_LIST)
    @TypeConverters(IntArrayConverter::class)
    val requestList: List<Int>
) : Serializable
