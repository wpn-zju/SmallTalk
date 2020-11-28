package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import edu.syr.smalltalk.service.model.converter.IntArrayConverter
import java.io.Serializable

@Entity(tableName = "small_talk_group")
data class SmallTalkGroup (
    @PrimaryKey
    @ColumnInfo(name = "group_id")
    val groupId: Int,
    @ColumnInfo(name = "group_name")
    val groupName: String,
    @ColumnInfo(name = "group_host")
    val groupHostUserId: Int,
    @ColumnInfo(name = "group_member_list")
    @TypeConverters(IntArrayConverter::class)
    val groupMemberList: List<Int>
) : Serializable