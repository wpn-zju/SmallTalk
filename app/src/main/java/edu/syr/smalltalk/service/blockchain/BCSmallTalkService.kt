package edu.syr.smalltalk.service.blockchain

import android.content.Context
import edu.syr.smalltalk.service.ISmallTalkService
import edu.syr.smalltalk.service.model.logic.SmallTalkDao


class BCSmallTalkService(context: Context) : ISmallTalkService {
    private val manager = BCContractManager(context)

    // init functions invoked in ISmallTalkService.onCreate()
    override fun connect() {
        manager.connect()
        // println(Thread.currentThread().id)
    }

    override fun disconnect() {
        manager.disconnect()
    }

    override fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        manager.setDataAccessor(smallTalkDao)
    }


    override fun userSignUp(userEmail: String, userPassword: String, passcode: String) {


    }

    override fun userSignUpPasscodeRequest(userEmail: String) {

    }

    override fun userRecoverPassword(userEmail: String, userPassword: String, passcode: String) {

    }

    override fun userRecoverPasswordPasscodeRequest(userEmail: String) {

    }

    // here userEmail stands for path of wallet file
    override fun userSignIn(userEmail: String, userPassword: String) {

    }

    override fun userSessionSignIn(sessionToken: String) {

    }

    override fun userSessionSignOut() {

    }

    override fun userModifyName(newUserName: String) {

    }

    override fun userModifyPassword(newUserPassword: String) {

    }

    override fun loadUser() {
    }

    override fun loadContact(contactId: Int) {

    }

    override fun loadContactByEmail(contactEmail: String) {

    }

    override fun loadGroup(groupId: Int) {

    }

    override fun loadRequest(requestId: Int) {

    }

    override fun messageForward(
        senderId: Int,
        receiverId: Int,
        content: String,
        contentType: String
    ) {

    }

    override fun messageForwardGroup(
        senderId: Int,
        receiverId: Int,
        content: String,
        contentType: String
    ) {

    }

    override fun contactAddRequest(contactEmail: String) {

    }

    override fun contactAddConfirm(requestId: Int) {

    }

    override fun contactAddRefuse(requestId: Int) {

    }

    override fun groupCreateRequest(groupName: String) {

    }

    override fun groupModifyName(groupId: Int, newGroupName: String) {

    }

    override fun groupAddRequest(groupId: Int) {

    }

    override fun groupAddConfirm(requestId: Int) {

    }

    override fun groupAddRefuse(requestId: Int) {

    }

    override fun webrtcCall(
        senderId: Int,
        receiverId: Int,
        webrtcCommand: String,
        webrtcSessionDescription: String
    ) {

    }

}
