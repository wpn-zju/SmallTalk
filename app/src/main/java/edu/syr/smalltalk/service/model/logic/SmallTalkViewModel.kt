package edu.syr.smalltalk.service.model.logic

import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import edu.syr.smalltalk.service.model.entity.*
import kotlinx.coroutines.launch
import java.util.stream.Collectors

// All return values in this class should be LiveData<T>
class SmallTalkViewModel(private val application: SmallTalkApplication, private val repository: SmallTalkRepository) : AndroidViewModel(application) {
    private val currentUser: LiveData<Int> = PreferenceManager.getDefaultSharedPreferences(application.applicationContext).intLiveData("current_user", 0)

    val userList: LiveData<List<SmallTalkUser>> = repository.userList.asLiveData()

    val currentUserInfo: LiveData<SmallTalkUser> = Transformations.switchMap(currentUser) {
            userId -> repository.getUser(userId).asLiveData() }

    private val contactList: LiveData<List<SmallTalkContact>> = repository.contactList.asLiveData()
    private val contactListMediator: ContactListLiveData = ContactListLiveData(currentUserInfo, contactList)
    val actualContactList: LiveData<List<SmallTalkContact>> = Transformations.map(contactListMediator) { mediator ->
        mediator.second.stream().filter {
                contact -> mediator.first.contactList!!.contains(contact.userId)
        }.collect(Collectors.toList())
    }

    private val groupList: LiveData<List<SmallTalkGroup>> = repository.groupList.asLiveData()
    private val groupListMediator: GroupListLiveData = GroupListLiveData(currentUserInfo, groupList)
    val actualGroupList: LiveData<List<SmallTalkGroup>> = Transformations.map(groupListMediator) { mediator ->
        mediator.second.stream().filter {
            group -> mediator.first.groupList!!.contains(group.groupId)
        }.collect(Collectors.toList())
    }

    private val messageList: LiveData<List<SmallTalkMessage>> = Transformations.switchMap(currentUser) {
        userId -> repository.getMessageList(userId).asLiveData() }

    // Modifier Example
    fun insertUser(userInfo: SmallTalkUser) = viewModelScope.launch {
        repository.insertUser(userInfo)
    }
}

class ContactListLiveData (
    userInfo: LiveData<SmallTalkUser>, contactList: LiveData<List<SmallTalkContact>>
) : MediatorLiveData<Pair<SmallTalkUser, List<SmallTalkContact>>>() {
    init {
        addSource(userInfo) { first -> value = Pair(first, contactList.value!!) }
        addSource(contactList) { second -> value = Pair(userInfo.value!!, second) }
    }
}

class GroupListLiveData (
    userInfo: LiveData<SmallTalkUser>, groupList: LiveData<List<SmallTalkGroup>>
) : MediatorLiveData<Pair<SmallTalkUser, List<SmallTalkGroup>>>() {
    init {
        addSource(userInfo) { first -> value = Pair(first, groupList.value!!) }
        addSource(groupList) { second -> value = Pair(userInfo.value!!, second) }
    }
}

class RequestListLiveData (
    userInfo: LiveData<SmallTalkUser>, requestList: LiveData<List<SmallTalkRequest>>
) : MediatorLiveData<Pair<SmallTalkUser, List<SmallTalkRequest>>>() {
    init {
        addSource(userInfo) { first -> value = Pair(first, requestList.value!!) }
        addSource(requestList) { second -> value = Pair(userInfo.value!!, second) }
    }
}
