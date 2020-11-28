package edu.syr.smalltalk.service

import edu.syr.smalltalk.service.model.logic.SmallTalkDao

interface ISmallTalkService {
    fun setDataAccessor(smallTalkDao: SmallTalkDao)
    fun connect()
    fun disconnect()
    fun userSignUp(userEmail: String, userPassword: String, passcode: String)
    fun userSignUpPasscodeRequest(userEmail: String)
    fun userRecoverPassword(userEmail: String, userPassword: String, passcode: String)
    fun userRecoverPasswordPasscodeRequest(userEmail: String)
    fun userSignIn(userEmail: String, userPassword: String)
    fun userSessionSignIn(sessionToken: String) // Last Session Token
    fun userSessionSignOut()
    fun userModifyName(newUserName: String)
    fun userModifyPassword(newUserPassword: String)
    fun loadUser() // Only work for current user
    fun loadContact(contactId: Int)
    fun loadGroup(groupId: Int)
    fun loadRequest(requestId: Int)
    fun messageForward(senderId: Int, receiverId: Int, content: String, contentType: String)
    fun messageForwardGroup(senderId: Int, receiverId: Int, content: String, contentType: String)
    fun contactAddRequest(contactEmail: String)
    fun contactAddConfirm(requestId: Int)
    fun contactAddRefuse(requestId: Int)
    fun groupCreateRequest(groupName: String)
    fun groupModifyName(groupId: Int, newGroupName: String)
    fun groupAddRequest(groupId: Int)
    fun groupAddConfirm(requestId: Int)
    fun groupAddRefuse(requestId: Int)
    fun webrtcCall(senderId: Int, receiverId: Int, webrtcCommand: String, webrtcSessionDescription: String)

    fun testSend(payload: Int)
}