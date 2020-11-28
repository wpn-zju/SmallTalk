package edu.syr.smalltalk.service.model.logic

import androidx.room.*
import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SmallTalkDao {
    @Query("SELECT * FROM small_talk_user")
    fun getAllUsers(): Flow<List<SmallTalkUser>>

    @Query("SELECT * FROM small_talk_user WHERE user_id = :userId")
    fun getUser(userId: Int): Flow<SmallTalkUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(smallTalkUser: SmallTalkUser)

    @Update
    fun updateUser(smallTalkUser: SmallTalkUser)

    @Delete
    fun deleteUser(smallTalkUser: SmallTalkUser)

    @Query("SELECT * FROM small_talk_message WHERE user_id = :userId")
    fun getMessageList(userId: Int): Flow<List<SmallTalkMessage>>

    @Insert
    fun insertMessage(smallTalkMessage: SmallTalkMessage)

    @Delete
    fun deleteMessage(smallTalkMessage: SmallTalkMessage)

    @Query("SELECT * FROM small_talk_contact")
    fun getContactList(): Flow<List<SmallTalkContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(smallTalkContact: SmallTalkContact)

    @Update
    fun updateContact(smallTalkContact: SmallTalkContact)

    @Delete
    fun deleteContact(smallTalkContact: SmallTalkContact)

    @Query("SELECT * FROM small_talk_group")
    fun getGroupList(): Flow<List<SmallTalkGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(smallTalkGroup: SmallTalkGroup)

    @Update
    fun updateGroup(smallTalkGroup: SmallTalkGroup)

    @Delete
    fun deleteGroup(smallTalkGroup: SmallTalkGroup)

    @Query("SELECT * FROM small_talk_request")
    fun getRequestList(): Flow<List<SmallTalkRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(smallTalkRequest: SmallTalkRequest)

    @Update
    fun updateRequest(smallTalkRequest: SmallTalkRequest)

    @Delete
    fun deleteRequest(smallTalkRequest: SmallTalkRequest)
}