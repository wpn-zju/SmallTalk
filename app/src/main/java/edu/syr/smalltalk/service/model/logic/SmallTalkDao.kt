package edu.syr.smalltalk.service.model.logic

import androidx.room.*
import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SmallTalkDao {
    @Query("SELECT * FROM small_talk_user WHERE user_id = :userId")
    fun getUser(userId: Int): Flow<List<SmallTalkUser>>

    @Query("SELECT * FROM small_talk_user")
    fun getUserList(): Flow<List<SmallTalkUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(smallTalkUser: SmallTalkUser)

    @Update
    fun updateUser(smallTalkUser: SmallTalkUser)

    @Delete
    fun deleteUser(smallTalkUser: SmallTalkUser)

    @Insert
    fun insertMessage(smallTalkMessage: SmallTalkMessage)

    @Delete
    fun deleteMessage(smallTalkMessage: SmallTalkMessage)

    @Query("SELECT * FROM small_talk_message WHERE message_id = :messageId")
    fun getMessage(messageId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT * FROM small_talk_message WHERE user_id = :userId")
    fun getMessageList(userId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT *, MAX(timestamp) FROM small_talk_message WHERE user_id = :userId GROUP BY chat_id ORDER BY timestamp DESC")
    fun getRecentMessageList(userId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT * FROM small_talk_message WHERE user_id = :userId AND chat_id = :chatId ORDER BY timestamp ASC")
    fun getChatMessageList(userId: Int, chatId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT * FROM SMALL_TALK_CONTACT WHERE contact_id = :contactId")
    fun getContact(contactId: Int): Flow<List<SmallTalkContact>>

    @Query("SELECT * FROM small_talk_contact ORDER BY contact_name ASC")
    fun getContactList(): Flow<List<SmallTalkContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(smallTalkContact: SmallTalkContact)

    @Update
    fun updateContact(smallTalkContact: SmallTalkContact)

    @Delete
    fun deleteContact(smallTalkContact: SmallTalkContact)

    @Query("SELECT * FROM SMALL_TALK_GROUP WHERE group_id = :groupId")
    fun getGroup(groupId: Int): Flow<List<SmallTalkGroup>>

    @Query("SELECT * FROM small_talk_group ORDER BY group_name ASC")
    fun getGroupList(): Flow<List<SmallTalkGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(smallTalkGroup: SmallTalkGroup)

    @Update
    fun updateGroup(smallTalkGroup: SmallTalkGroup)

    @Delete
    fun deleteGroup(smallTalkGroup: SmallTalkGroup)

    @Query("SELECT * FROM SMALL_TALK_REQUEST WHERE request_id = :requestId")
    fun getRequest(requestId: Int): Flow<List<SmallTalkRequest>>

    @Query("SELECT * FROM small_talk_request ORDER BY request_id ASC")
    fun getRequestList(): Flow<List<SmallTalkRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(smallTalkRequest: SmallTalkRequest)

    @Update
    fun updateRequest(smallTalkRequest: SmallTalkRequest)

    @Delete
    fun deleteRequest(smallTalkRequest: SmallTalkRequest)
}
