package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.EntityConstant
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import java.io.Serializable

@Entity(tableName = "small_talk_group")
data class SmallTalkGroup (
    @PrimaryKey
    @ColumnInfo(name = EntityConstant.GROUP_INFO_GROUP_ID)
    @SerializedName(EntityConstant.GROUP_INFO_GROUP_ID)
    val groupId: Int,
    @ColumnInfo(name = EntityConstant.GROUP_INFO_GROUP_NAME)
    @SerializedName(EntityConstant.GROUP_INFO_GROUP_NAME)
    val groupName: String,
    @ColumnInfo(name = EntityConstant.GROUP_INFO_GROUP_HOST_ID)
    @SerializedName(EntityConstant.GROUP_INFO_GROUP_HOST_ID)
    val groupHostId: Int,
    @ColumnInfo(name = EntityConstant.GROUP_INFO_MEMBER_LIST)
    @SerializedName(EntityConstant.GROUP_INFO_MEMBER_LIST)
    @TypeConverters(IntArrayConverter::class)
    val groupMemberList: List<Int>,
    @ColumnInfo(name = EntityConstant.GROUP_INFO_GROUP_INFO)
    @SerializedName(EntityConstant.GROUP_INFO_GROUP_INFO)
    val groupInfo: String,
    @ColumnInfo(name = EntityConstant.GROUP_INFO_GROUP_AVATAR_LINK)
    @SerializedName(EntityConstant.GROUP_INFO_GROUP_AVATAR_LINK)
    val groupAvatarLink: String
) : Serializable {
    fun groupEquals(other: SmallTalkGroup): Boolean {
        return groupId == other.groupId &&
                groupName == other.groupName &&
                groupHostId == other.groupHostId &&
                groupMemberList == other.groupMemberList &&
                groupInfo == other.groupInfo &&
                groupAvatarLink == other.groupAvatarLink
    }
}
