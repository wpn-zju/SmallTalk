package edu.syr.smalltalk.service.blockchain

import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Utf8String

object Events {
    val messageSentEvent = Event(
        ClientConstant.CONTRACT_SEND_MESSAGE, listOf(
            TypeReference.create(Address::class.java, true),
            TypeReference.create(Address::class.java, true),
            TypeReference.create(Utf8String::class.java)
        )
    )
}