package edu.syr.smalltalk.service.model.logic

import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.flow.Flow

class SmallTalkRepository(private val smallTalkDao: SmallTalkDao) {
    val watchContactList: Flow<List<SmallTalkContact>> = smallTalkDao.watchContactList()
    val watchGroupList: Flow<List<SmallTalkGroup>> = smallTalkDao.watchGroupList()
    val watchRequestList: Flow<List<SmallTalkRequest>> = smallTalkDao.watchRequestList()

    fun insertUser(userInfo: SmallTalkUser) {
        smallTalkDao.insertUser(userInfo)
    }

    fun updateUser(userInfo: SmallTalkUser) {
        smallTalkDao.updateUser(userInfo)
    }

    fun deleteUser(userInfo: SmallTalkUser) {
        smallTalkDao.deleteUser(userInfo)
    }

    fun insertMessage(smallTalkMessage: SmallTalkMessage) {
        smallTalkDao.insertMessage(smallTalkMessage)
    }

    fun deleteMessage(smallTalkMessage: SmallTalkMessage) {
        smallTalkDao.deleteMessage(smallTalkMessage)
    }

    fun readAll(userId: Int) {
        smallTalkDao.readAll(userId)
    }

    fun readMessage(userId: Int, chatId: Int) {
        smallTalkDao.readMessage(userId, chatId)
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

    fun insertGroup(smallTalkGroup: SmallTalkGroup) {
        smallTalkDao.insertGroup(smallTalkGroup)
    }

    fun updateGroup(smallTalkGroup: SmallTalkGroup) {
        smallTalkDao.updateGroup(smallTalkGroup)
    }

    fun deleteGroup(smallTalkGroup: SmallTalkGroup) {
        smallTalkDao.deleteGroup(smallTalkGroup)
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

    fun insertFile(smallTalkFile: SmallTalkFile) {
        smallTalkDao.insertFile(smallTalkFile)
    }

    fun watchUser(userId: Int): Flow<List<SmallTalkUser>> {
        return smallTalkDao.watchUser(userId)
    }

    fun watchContact(contactId: Int): Flow<List<SmallTalkContact>> {
        return smallTalkDao.watchContact(contactId)
    }

    fun watchGroup(groupId: Int): Flow<List<SmallTalkGroup>> {
        return smallTalkDao.watchGroup(groupId)
    }

    fun watchRequest(requestId: Int): Flow<List<SmallTalkRequest>> {
        return smallTalkDao.watchRequest(requestId)
    }

    fun watchMessage(messageId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.watchMessage(messageId)
    }

    fun watchMessageList(userId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.watchMessageList(userId)
    }

    fun watchRecentMessageList(userId: Int): Flow<List<SmallTalkRecentMessage>> {
        return smallTalkDao.watchRecentMessageList(userId)
    }

    fun watchChatMessageList(userId: Int, chatId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.watchChatMessageList(userId, chatId)
    }

    fun getContact(contactId: Int): SmallTalkContact? {
        val prepare = smallTalkDao.getContact(contactId)
        return if (prepare.isEmpty()) null else prepare[0]
    }

    fun getGroup(groupId: Int): SmallTalkGroup? {
        val prepare = smallTalkDao.getGroup(groupId)
        return if (prepare.isEmpty()) null else prepare[0]
    }

    fun watchFileList(firstSelector: Int, secondSelector: Int): Flow<List<SmallTalkFile>> {
        return smallTalkDao.watchFileList(firstSelector, secondSelector)
    }
}
