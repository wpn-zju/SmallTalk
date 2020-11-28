package edu.syr.smalltalk.service.model.logic

import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.flow.Flow

// All return values in this class should be Flow<T>
class SmallTalkRepository(private val smallTalkDao: SmallTalkDao) {
    val userList: Flow<List<SmallTalkUser>> = smallTalkDao.getAllUsers()
    val contactList: Flow<List<SmallTalkContact>> = smallTalkDao.getContactList()
    val groupList: Flow<List<SmallTalkGroup>> = smallTalkDao.getGroupList()
    val requestList: Flow<List<SmallTalkRequest>> = smallTalkDao.getRequestList()

    fun getDataAccessor(): SmallTalkDao {
        return smallTalkDao
    }

    fun getUser(userId: Int): Flow<SmallTalkUser> {
        return smallTalkDao.getUser(userId)
    }

    fun getMessageList(userId: Int): Flow<List<SmallTalkMessage>> {
        return smallTalkDao.getMessageList(userId)
    }

    fun insertUser(userInfo: SmallTalkUser) {
        smallTalkDao.insertUser(userInfo)
    }
}