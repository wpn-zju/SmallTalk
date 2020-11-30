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
            if (mediator.first == null) {
                ArrayList()
            } else {
                mediator.second.stream().filter { contact ->
                    mediator.first!!.contactList.contains(contact.contactId)
                }.collect(Collectors.toList())
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
            if (mediator.first == null) {
                ArrayList()
            } else {
                mediator.second.stream().filter { group ->
                    mediator.first!!.groupList.contains(group.groupId)
                }.collect(Collectors.toList())
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
            if (mediator.first == null) {
                ArrayList()
            } else {
                mediator.second.stream().filter { request ->
                    mediator.first!!.requestList.contains(request.requestId)
                }.collect(Collectors.toList())
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
) : MediatorLiveData<Pair<SmallTalkUser?, List<SmallTalkContact>>>() {
    init {
        addSource(userInfo) {
                first -> value = if (first.isEmpty()) Pair(null, ArrayList()) else Pair(first[0], contactList.value!!) }
        addSource(contactList) {
                second -> value = if (userInfo.value!!.isEmpty()) Pair(null, ArrayList()) else Pair(userInfo.value!![0], second) }
    }
}

class GroupListLiveData (
    userInfo: LiveData<List<SmallTalkUser>>, groupList: LiveData<List<SmallTalkGroup>>
) : MediatorLiveData<Pair<SmallTalkUser?, List<SmallTalkGroup>>>() {
    init {
        addSource(userInfo) {
                first -> value = if (first.isEmpty()) Pair(null, ArrayList()) else Pair(first[0], groupList.value!!) }
        addSource(groupList) {
                second -> value = if (userInfo.value!!.isEmpty()) Pair(null, ArrayList()) else Pair(userInfo.value!![0], second) }
    }
}

class RequestListLiveData (
    userInfo: LiveData<List<SmallTalkUser>>, requestList: LiveData<List<SmallTalkRequest>>
) : MediatorLiveData<Pair<SmallTalkUser?, List<SmallTalkRequest>>>() {
    init {
        addSource(userInfo) {
                first -> value = if (first.isEmpty()) Pair(null, ArrayList()) else Pair(first[0], requestList.value!!) }
        addSource(requestList) {
                second -> value = if (userInfo.value!!.isEmpty()) Pair(null, ArrayList()) else Pair(userInfo.value!![0], second) }
    }
}
