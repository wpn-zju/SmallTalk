package edu.syr.smalltalk.service.model.logic

import androidx.lifecycle.*
import edu.syr.smalltalk.service.model.entity.*
import java.util.stream.Collectors

// All return values in this class should be LiveData<T>
class SmallTalkViewModel(private val application: SmallTalkApplication, private val repository: SmallTalkRepository) : AndroidViewModel(application) {
    val userList: LiveData<List<SmallTalkUser>> = repository.userList.asLiveData()

    fun getCurrentUserInfo(userId: Int): LiveData<List<SmallTalkUser>> {
        return repository.getUser(userId).asLiveData()
    }

    fun getCurrentContact(contactId: Int): LiveData<List<SmallTalkContact>> {
        return repository.getContact(contactId).asLiveData()
    }

    private val contactList: LiveData<List<SmallTalkContact>> = repository.contactList.asLiveData()

    fun getContactList(userId: Int): LiveData<List<SmallTalkContact>> {
        val contactListMediator = ContactListLiveData(getCurrentUserInfo(userId), contactList)
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

    fun getCurrentGroup(groupId: Int): LiveData<List<SmallTalkGroup>> {
        return repository.getGroup(groupId).asLiveData()
    }

    private val groupList: LiveData<List<SmallTalkGroup>> = repository.groupList.asLiveData()

    fun getGroupList(userId: Int): LiveData<List<SmallTalkGroup>> {
        val groupListMediator = GroupListLiveData(getCurrentUserInfo(userId), groupList)
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

    fun getCurrentRequest(requestId: Int): LiveData<List<SmallTalkRequest>> {
        return repository.getRequest(requestId).asLiveData()
    }

    private val requestList: LiveData<List<SmallTalkRequest>> = repository.requestList.asLiveData()

    fun getRequestList(userId: Int): LiveData<List<SmallTalkRequest>> {
        val requestListMediator = RequestListLiveData(getCurrentUserInfo(userId), requestList)
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

    fun getRecentMessageList(userId: Int): LiveData<List<SmallTalkMessage>> {
        return repository.getRecentMessageList(userId).asLiveData()
    }

    fun getCurrentMessageList(userId: Int, chatId: Int): LiveData<List<SmallTalkMessage>> {
        return repository.getChatMessageList(userId, chatId).asLiveData()
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
