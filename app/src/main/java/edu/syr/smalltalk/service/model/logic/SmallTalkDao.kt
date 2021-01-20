package edu.syr.smalltalk.service.model.logic

import androidx.room.*
import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SmallTalkDao {
    @Query("SELECT * FROM small_talk_user WHERE user_id = :userId")
    fun watchUser(userId: Int): Flow<List<SmallTalkUser>>

    @Query("SELECT * FROM small_talk_user")
    fun watchUserList(): Flow<List<SmallTalkUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(smallTalkUser: SmallTalkUser)

    @Update
    fun updateUser(smallTalkUser: SmallTalkUser)

    @Delete
    fun deleteUser(smallTalkUser: SmallTalkUser)

    @Query("UPDATE small_talk_message SET has_read = 1 WHERE user_id = :userId AND chat_id = :chatId")
    fun readMessage(userId: Int, chatId: Int)

    @Query("SELECT * FROM small_talk_message WHERE message_id = :messageId")
    fun watchMessage(messageId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT * FROM small_talk_message WHERE user_id = :userId")
    fun watchMessageList(userId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT * FROM small_talk_message WHERE user_id = :userId AND chat_id = :chatId ORDER BY timestamp ASC")
    fun watchChatMessageList(userId: Int, chatId: Int): Flow<List<SmallTalkMessage>>

    @Query("SELECT p1.*, p2.unread_num FROM small_talk_message p1 LEFT JOIN (SELECT max(timestamp) latest_timestamp, sum(is_group) unread_num, chat_id FROM small_talk_message WHERE user_id = :userId GROUP BY chat_id) p2 ON p1.chat_id = p2.chat_id AND p1.timestamp = p2.latest_timestamp WHERE user_id = :userId GROUP BY p1.chat_id ORDER BY timestamp DESC")
    fun watchRecentMessageList(userId: Int): Flow<List<SmallTalkRecentMessage>>

    @Insert
    fun insertMessage(smallTalkMessage: SmallTalkMessage)

    @Delete
    fun deleteMessage(smallTalkMessage: SmallTalkMessage)

    @Query("SELECT * FROM small_talk_contact WHERE contact_id = :contactId")
    fun getContact(contactId: Int): List<SmallTalkContact>

    @Query("SELECT * FROM small_talk_contact WHERE contact_id = :contactId")
    fun watchContact(contactId: Int): Flow<List<SmallTalkContact>>

    @Query("SELECT * FROM small_talk_contact ORDER BY contact_name ASC")
    fun watchContactList(): Flow<List<SmallTalkContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(smallTalkContact: SmallTalkContact)

    @Update
    fun updateContact(smallTalkContact: SmallTalkContact)

    @Delete
    fun deleteContact(smallTalkContact: SmallTalkContact)

    @Query("SELECT * FROM SMALL_TALK_GROUP WHERE group_id = :groupId")
    fun getGroup(groupId: Int): List<SmallTalkGroup>

    @Query("SELECT * FROM SMALL_TALK_GROUP WHERE group_id = :groupId")
    fun watchGroup(groupId: Int): Flow<List<SmallTalkGroup>>

    @Query("SELECT * FROM small_talk_group ORDER BY group_name ASC")
    fun watchGroupList(): Flow<List<SmallTalkGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(smallTalkGroup: SmallTalkGroup)

    @Update
    fun updateGroup(smallTalkGroup: SmallTalkGroup)

    @Delete
    fun deleteGroup(smallTalkGroup: SmallTalkGroup)

    @Query("SELECT * FROM small_talk_request WHERE request_id = :requestId")
    fun getRequest(requestId: Int): List<SmallTalkRequest>

    @Query("SELECT * FROM small_talk_request WHERE request_id = :requestId")
    fun watchRequest(requestId: Int): Flow<List<SmallTalkRequest>>

    @Query("SELECT * FROM small_talk_request ORDER BY request_id ASC")
    fun watchRequestList(): Flow<List<SmallTalkRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(smallTalkRequest: SmallTalkRequest)

    @Update
    fun updateRequest(smallTalkRequest: SmallTalkRequest)

    @Delete
    fun deleteRequest(smallTalkRequest: SmallTalkRequest)

    @Query("SELECT * FROM small_talk_file WHERE first_selector = :firstSelector AND second_selector = :secondSelector")
    fun watchFileList(firstSelector: Int, secondSelector: Int): Flow<List<SmallTalkFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(smallTalkFile: SmallTalkFile)
}
