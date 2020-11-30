package edu.syr.smalltalk.service.model.logic

import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.flow.Flow

// All return values in this class should be Flow<T>
class SmallTalkRepository(private val smallTalkDao: SmallTalkDao) {
    val userList: Flow<List<SmallTalkUser>> = smallTalkDao.getUserList()
    val contactList: Flow<List<SmallTalkContact>> = smallTalkDao.getContactList()
    val groupList: Flow<List<SmallTalkGroup>> = smallTalkDao.getGroupList()
    val requestList: Flow<List<SmallTalkRequest>> = smallTalkDao.getRequestList()

    fun getDataAccessor(): SmallTalkDao {
        return smallTalkDao
    }

    fun getUser(userId: Int): Flow<List<SmallTalkUser>> {
        return smallTalkDao.getUser(userId)
    }

    fun insertUser(userInfo: SmallTalkUser) {
        smallTalkDao.insertUser(userInfo)
    }

    fun updateUser(userInfo: SmallTalkUser) {
        smallTalkDao.updateUser(userInfo)
    }

    fun deleteUser(userInfo: SmallTalkUser) {
        smallTalkDao.deleteUser(userInfo)
    }

    fun getMessage(messageId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.getMessage(messageId)
    }

    fun insertMessage(smallTalkMessage: SmallTalkMessage) {
        smallTalkDao.insertMessage(smallTalkMessage)
    }

    fun deleteMessage(smallTalkMessage: SmallTalkMessage) {
        smallTalkDao.deleteMessage(smallTalkMessage)
    }

    fun getMessageList(userId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.getMessageList(userId)
    }

    fun getRecentMessageList(userId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.getRecentMessageList(userId)
    }

    fun getChatMessageList(userId: Int, chatId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.getChatMessageList(userId, chatId)
    }

    fun getContact(contactId: Int): Flow<List<SmallTalkContact>> {
        return smallTalkDao.getContact(contactId)
    }

    fun insertContact(smallTalkContact: SmallTalkContact) {
        smallTalkDao.insertContact(smallTalkContact)
    }

    fun updateContact(smallTalkContact: SmallTalkContact) {
        smallTalkDao.updateContact(smallTalkContact)
    }

    fun deleteContact(smallTalkContact: SmallTalkContact) {
        smallTalkDao.deleteContact(smallTalkContact)
    }

    fun getGroup(groupId: Int): Flow<List<SmallTalkGroup>> {
        return smallTalkDao.getGroup(groupId)
    }

    fun insertGroup(smallTalkGroup: SmallTalkGroup) {
        smallTalkDao.insertGroup(smallTalkGroup)
    }

    fun updateGroup(smallTalkGroup: SmallTalkGroup) {
        smallTalkDao.updateGroup(smallTalkGroup)
    }

    fun deleteGroup(smallTalkGroup: SmallTalkGroup) {
        smallTalkDao.deleteGroup(smallTalkGroup)
    }

    fun getRequest(requestId: Int): Flow<List<SmallTalkRequest>> {
        return smallTalkDao.getRequest(requestId)
    }

    fun insertRequest(smallTalkRequest: SmallTalkRequest) {
        smallTalkDao.insertRequest(smallTalkRequest)
    }

    fun updateRequest(smallTalkRequest: SmallTalkRequest) {
        smallTalkDao.updateRequest(smallTalkRequest)
    }

    fun deleteRequest(smallTalkRequest: SmallTalkRequest) {
        smallTalkDao.deleteRequest(smallTalkRequest)
    }
}
