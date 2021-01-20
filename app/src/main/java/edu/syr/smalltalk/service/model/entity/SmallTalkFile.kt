package edu.syr.smalltalk.service.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import edu.syr.smalltalk.service.android.constant.EntityConstant
import edu.syr.smalltalk.service.model.converter.InstantConverter
import java.io.Serializable
import java.time.Instant


@Entity(tableName = "small_talk_file")
data class SmallTalkFile(
    @PrimaryKey
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_ID)
    @SerializedName(EntityConstant.FILE_INFO_FILE_ID)
    val fileId: Int,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FIRST_SELECTOR)
    @SerializedName(EntityConstant.FILE_INFO_FIRST_SELECTOR)
    val firstSelector: Int,
    @ColumnInfo(name = EntityConstant.FILE_INFO_SECOND_SELECTOR)
    @SerializedName(EntityConstant.FILE_INFO_SECOND_SELECTOR)
    val secondSelector: Int,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_NAME)
    @SerializedName(EntityConstant.FILE_INFO_FILE_NAME)
    val fileName: String,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_LINK)
    @SerializedName(EntityConstant.FILE_INFO_FILE_LINK)
    val fileLink: String,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_UPLOADER)
    @SerializedName(EntityConstant.FILE_INFO_FILE_UPLOADER)
    val fileUploader: Int,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_UPLOAD_TIME)
    @SerializedName(EntityConstant.FILE_INFO_FILE_UPLOAD_TIME)
    @TypeConverters(InstantConverter::class)
    val fileUploadTime: Instant,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_EXPIRE_TIME)
    @SerializedName(EntityConstant.FILE_INFO_FILE_EXPIRE_TIME)
    @TypeConverters(InstantConverter::class)
    val fileExpireTime: Instant,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_SIZE)
    @SerializedName(EntityConstant.FILE_INFO_FILE_SIZE)
    val fileSize: Int,
    @ColumnInfo(name = EntityConstant.FILE_INFO_FILE_DOWNLOADS)
    @SerializedName(EntityConstant.FILE_INFO_FILE_DOWNLOADS)
    val fileDownloads: Int,
) : Serializable {
    fun fileEquals(other: SmallTalkFile): Boolean {
        return fileId == other.fileId &&
                fileName == other.fileName &&
                fileLink == other.fileLink &&
                fileUploader == other.fileUploader &&
                fileUploadTime == other.fileUploadTime &&
                fileExpireTime == other.fileExpireTime &&
                fileSize == other.fileSize &&
                fileDownloads == other.fileDownloads
    }
}
