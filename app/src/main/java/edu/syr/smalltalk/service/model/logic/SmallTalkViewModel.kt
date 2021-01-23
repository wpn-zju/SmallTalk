package edu.syr.smalltalk.service.model.logic

import androidx.lifecycle.*
import edu.syr.smalltalk.service.model.entity.*
import java.util.stream.Collectors

// All return values in this class should be LiveData<T>
class SmallTalkViewModel(application: SmallTalkApplication, private val repository: SmallTalkRepository)
    : AndroidViewModel(application) {

    fun watchCurrentUserInfo(userId: Int): LiveData<List<SmallTalkUser>> {
        return repository.watchUser(userId).asLiveData()
    }

    fun watchCurrentContact(contactId: Int): LiveData<List<SmallTalkContact>> {
        return repository.watchContact(contactId).asLiveData()
    }

    private val contactList: LiveData<List<SmallTalkContact>> =
        repository.watchContactList.asLiveData()

    fun watchContactList(userId: Int): LiveData<List<SmallTalkContact>> {
        val contactListMediator = ContactListLiveData(watchCurrentUserInfo(userId), contactList)
        return Transformations.map(contactListMediator) { mediator ->
            when {
                mediator.first == null -> {
                    ArrayList()
                }
                mediator.second == null -> {
                    ArrayList()
                }
                else -> {
                    mediator.second!!.stream().filter { contact ->
                        mediator.first!![0].contactList.contains(contact.contactId)
                    }.collect(Collectors.toList())
                }
            }
        }
    }

    fun watchCurrentGroup(groupId: Int): LiveData<List<SmallTalkGroup>> {
        return repository.watchGroup(groupId).asLiveData()
    }

    private val groupList: LiveData<List<SmallTalkGroup>> = repository.watchGroupList.asLiveData()

    fun watchGroupList(userId: Int): LiveData<List<SmallTalkGroup>> {
        val groupListMediator = GroupListLiveData(watchCurrentUserInfo(userId), groupList)
        return Transformations.map(groupListMediator) { mediator ->
            when {
                mediator.first == null -> {
                    ArrayList()
                }
                mediator.second == null -> {
                    ArrayList()
                }
                else -> {
                    mediator.second!!.stream().filter { group ->
                        mediator.first!![0].groupList.contains(group.groupId)
                    }.collect(Collectors.toList())
                }
            }
        }
    }

    fun watchCurrentRequest(requestId: Int): LiveData<List<SmallTalkRequest>> {
        return repository.watchRequest(requestId).asLiveData()
    }

    private val requestList: LiveData<List<SmallTalkRequest>> =
        repository.watchRequestList.asLiveData()

    fun watchRequestList(userId: Int): LiveData<List<SmallTalkRequest>> {
        val requestListMediator = RequestListLiveData(watchCurrentUserInfo(userId), requestList)
        return Transformations.map(requestListMediator) { mediator ->
            when {
                mediator.first == null -> {
                    ArrayList()
                }
                mediator.second == null -> {
                    ArrayList()
                }
                else -> {
                    mediator.second!!.stream().filter { request ->
                        mediator.first!![0].requestList.contains(request.requestId)
                    }.collect(Collectors.toList())
                }
            }
        }
    }

    fun watchRecentMessageList(userId: Int): LiveData<List<SmallTalkRecentMessage>> {
        return repository.watchRecentMessageList(userId).asLiveData()
    }

    fun watchCurrentMessageList(userId: Int, chatId: Int): LiveData<List<SmallTalkMessage>> {
        return repository.watchChatMessageList(userId, chatId).asLiveData()
    }

    fun readAll(userId: Int) {
        repository.readAll(userId)
    }

    fun readMessage(userId: Int, chatId: Int) {
        repository.readMessage(userId, chatId)
    }

    fun watchFileList(firstSelector: Int, secondSelector: Int): LiveData<List<SmallTalkFile>> {
        return repository.watchFileList(firstSelector, secondSelector).asLiveData()
    }
}

class ContactListLiveData (
    userInfo: LiveData<List<SmallTalkUser>>, contactList: LiveData<List<SmallTalkContact>>
) : MediatorLiveData<Pair<List<SmallTalkUser>?, List<SmallTalkContact>?>>() {
    private var user: List<SmallTalkUser>? = null
    private var cList: List<SmallTalkContact>? = null

    init {
        addSource(userInfo) {
            user = it
            value = Pair(user, cList)
        }
        addSource(contactList) {
            cList = it
            value = Pair(user, cList)
        }
    }
}

class GroupListLiveData (
    userInfo: LiveData<List<SmallTalkUser>>, groupList: LiveData<List<SmallTalkGroup>>
) : MediatorLiveData<Pair<List<SmallTalkUser>?, List<SmallTalkGroup>?>>() {
    private var user: List<SmallTalkUser>? = null
    private var gList: List<SmallTalkGroup>? = null

    init {
        addSource(userInfo) {
            user = it
            value = Pair(user, gList)
        }
        addSource(groupList) {
            gList = it
            value = Pair(user, gList)
        }
    }
}

class RequestListLiveData (
    userInfo: LiveData<List<SmallTalkUser>>, requestList: LiveData<List<SmallTalkRequest>>
) : MediatorLiveData<Pair<List<SmallTalkUser>?, List<SmallTalkRequest>?>>() {
    private var user: List<SmallTalkUser>? = null
    private var rList: List<SmallTalkRequest>? = null

    init {
        addSource(userInfo) {
            user = it
            value = Pair(user, rList)
        }
        addSource(requestList) {
            rList = it
            value = Pair(user, rList)
        }
    }
}
