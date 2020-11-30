package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.ServerConstant
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import java.io.Serializable

@Entity(tableName = "small_talk_group")
data class SmallTalkGroup (
    @PrimaryKey
    @ColumnInfo(name = ServerConstant.ACC_GROUP_SYNC__GROUP_ID)
    @SerializedName(ServerConstant.ACC_GROUP_SYNC__GROUP_ID)
    val groupId: Int,
    @ColumnInfo(name = ServerConstant.ACC_GROUP_SYNC__GROUP_NAME)
    @SerializedName(ServerConstant.ACC_GROUP_SYNC__GROUP_NAME)
    val groupName: String,
    @ColumnInfo(name = ServerConstant.ACC_GROUP_SYNC__GROUP_HOST)
    @SerializedName(ServerConstant.ACC_GROUP_SYNC__GROUP_HOST)
    val groupHostId: Int,
    @ColumnInfo(name = ServerConstant.ACC_GROUP_SYNC__GROUP_MEMBER_LIST)
    @SerializedName(ServerConstant.ACC_GROUP_SYNC__GROUP_MEMBER_LIST)
    @TypeConverters(IntArrayConverter::class)
    val groupMemberList: List<Int>
) : Serializable {
    fun groupEquals(other: SmallTalkGroup): Boolean {
        return groupId == other.groupId &&
                groupName == other.groupName &&
                groupHostId == other.groupHostId
    }
}
