package edu.syr.smalltalk.service.blockchain

import android.content.Context
import android.util.Log
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.web3j.abi.EventEncoder
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint32
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger


class BCContractManager(private val context: Context) {
    private val httpService = HttpService(ClientConstant.RPC_ENDPOINT)
    private val web3: Web3j = Web3j.build(httpService)
    private val admin: Admin = Admin.build(httpService)
    private val compositeDisposable = CompositeDisposable()

    private val currentCredential = Credentials.create(ClientConstant.ICO_PRIVATE_KEY)

    private val gasProvider = object : DefaultGasProvider() {
        override fun getGasPrice(contractFunc: String): BigInteger {
            return BigInteger("300")
        }

        override fun getGasLimit(contractFunc: String): BigInteger {
            return BigInteger("3000000")
        }
    }
    private val contract =
        Relay.deploy(web3, currentCredential, gasProvider).sendAsync().get()

    private lateinit var smalltalkDao: SmallTalkDao
    private lateinit var accounts: List<String>

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) {
            smalltalkDao = smallTalkDao
        }
    }

    fun connect() {
        getAllAccounts()
        // setupBouncyCastle()
        // subscribe()
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            contract.contractAddress
        ).addSingleTopic(EventEncoder.encode(Relay.REGISTERSUCCESS_EVENT))
            .addOptionalTopics(
                "0x" + TypeEncoder.encode(Address(accounts[0]))
            )

        val subscriber = contract.registerSuccessEventFlowable(filter).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                { e ->
                    // println("Subscribe: $e")
                    Log.i("Subscribe: registered successfully", e.who)
                    // FunctionReturnDecoder.decode(
                    //     e.data,
                    //     f1.outputParameters
                    // ).forEach {
                    //     if (it is Uint32) {
                    //         println("Subscribe Event: " + it.value)
                    //     } else {
                    //         println("Subscribe Event: " + it.value)
                    //     }
                    // }
                }, { e ->
                    Log.d("T", e.toString())
                }
            )
        compositeDisposable.add(subscriber)


    }

    fun test(param: String) {
        // val txHash = contract.test(BigInteger(param), "nmsl").sendAsync().get().transactionHash
        // Log.i(
        //     "BC: ",
        //     "${web3.ethGetTransactionReceipt(txHash).sendAsync().get().transactionReceipt.get()}"
        // )
        println(contract.register().sendAsync().get().transactionHash)
        val response = contract.user.sendAsync().get()
        println("name: " + response.component1())
    }

    private fun getAllAccounts() {
        accounts = web3.ethAccounts().sendAsync().get().accounts
    }

    fun disconnect() {
        compositeDisposable.dispose()
    }

    fun sendMessage(senderId: Int, receiverId: Int, message: String) {
        val f = Function(
            "sendMessage",
            listOf(Address(accounts[senderId])),
            listOf(
                TypeReference.create(Address::class.java, true),
                TypeReference.create(Address::class.java, true),
                TypeReference.create(Utf8String::class.java)
            )
        )
        sendTransaction(accounts[senderId], FunctionEncoder.encode(f))
    }

    fun subscribeMessage(receiverId: Int) {
        val eventEncoder = EventEncoder.encode(Events.messageSentEvent)
        // TODO: test 0x is necessary or not
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            ClientConstant.CONTRACT_ADDRESS
        ).addSingleTopic(eventEncoder)
            .addSingleTopic("0x" + TypeEncoder.encode(Address(accounts[receiverId])))

        compositeDisposable.add(
            web3.ethLogFlowable(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ e ->
                    println("Subscribe: $e")
                    // FunctionReturnDecoder.decode(
                    //     e.data,
                    //     f1.outputParameters
                    // ).forEach {
                    //     if (it is Uint32) {
                    //         println("Subscribe Event: " + it.value)
                    //     } else {
                    //         println("Subscribe Event: " + it.value)
                    //     }
                    // }
                }, { e ->
                    Log.d("T", e.toString())
                })
        )
    }

    // private fun prepareSendMessageFunction(
    //     senderId: Int,
    //     receiverId: Int,
    //     message: String
    // ): Function {
    //     return Function(
    //         ClientConstant.CONTRACT_SEND_MESSAGE, listOf(Address(accounts[receiverId])),
    //         emptyList()
    //     )
    // }

    private fun sendTransaction(sender: String, encodedFunction: String) {
        // val output = mutableListOf<TypeReference<*>>()
        // val function = Function(functionName, params, output)
        // val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3.ethGetTransactionCount(
            sender, DefaultBlockParameterName.LATEST
        ).sendAsync().get().transactionCount

        val transaction = Transaction.createFunctionCallTransaction(
            sender,
            nonce,
            BigInteger.valueOf(2),
            BigInteger.valueOf(3000000),
            ClientConstant.CONTRACT_ADDRESS,
            encodedFunction
        )
        val txHash = web3.ethSendTransaction(transaction)
            .sendAsync().get().transactionHash
        val receipt = web3.ethGetTransactionReceipt(txHash)
            .sendAsync().get().transactionReceipt
        if (receipt.isPresent) {
            Log.i("BC DEBUG", "${receipt.get()}")
        }
    }

    // val event = Event(
    //     "testEvent",
    //     listOf(
    //         TypeReference.create(Uint32::class.java),
    //         TypeReference.create(Utf8String::class.java)
    //     )
    // )
    //
    // private fun subscribe() {
    //     val eventEncoder = EventEncoder.encode(event)
    //     val filter = EthFilter(
    //         DefaultBlockParameterName.EARLIEST,
    //         DefaultBlockParameterName.LATEST,
    //         contract.contractAddress
    //     ).addSingleTopic(eventEncoder)
    //
    //     // .addSingleTopic("0x" + TypeEncoder.encode(Uint32(2)))
    //     // addOptionalTopics(eventEncoder, "0x" + TypeEncoder.encode(Uint32(2)))
    //
    //     // Log.i("Subscribe: ", "${filter.topics[0].value}")
    //     // Log.i("Subscribe: ", "${filter.topics[1].value}")
    //     // Log.i("Subscribe encode: ", TypeEncoder.encode(Uint32(12)))
    //
    //     // var t = Utf8String("")
    //
    //     compositeDisposable.add(
    //         web3.ethLogFlowable(filter)
    //             .subscribeOn(Schedulers.io())
    //             .observeOn(AndroidSchedulers.mainThread())
    //             .subscribe({ e ->
    //                 println("Subscribe: $e")
    //                 // FunctionReturnDecoder.decode(
    //                 //     e.data,
    //                 //     f1.outputParameters
    //                 // ).forEach {
    //                 //     if (it is Uint32) {
    //                 //         println("Subscribe Event: " + it.value)
    //                 //     } else {
    //                 //         println("Subscribe Event: " + it.value)
    //                 //     }
    //                 // }
    //             }, { e ->
    //                 Log.d("T", e.toString())
    //             })
    //     )
    // }


// val f1 = Function(
//     "test",
//     listOf(Utf8String("NMSL"), Uint32(1)),
//     mutableListOf<TypeReference<*>>(
//         TypeReference.create(Utf8String::class.java),
//         TypeReference.create(Uint32::class.java)
//     )
// )
// val f2 = Function(
//     "test",
//     listOf(Utf8String("NMSL"), Uint32(2)),
//     mutableListOf<TypeReference<*>>(
//         TypeReference.create(Utf8String::class.java),
//         TypeReference.create(Uint32::class.java)
//     )
// )


// TODO: add encryption&decryption
// private val Algorithm = "RSA/ECB/PKCS1Padding"
// private fun encrypt(data: String): String {
//     val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
//     cipher.init(Cipher.ENCRYPT_MODE, )
//     val bytes = cipher.doFinal(data.toByteArray())
//     return Base64.encodeToString(bytes, Base64.DEFAULT)
// }
//
// private fun decrypt(data: String, privateKey: Key?): String {
//     val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
//     cipher.init(Cipher.DECRYPT_MODE, privateKey)
//     val encryptedData = Base64.decode(data, Base64.DEFAULT)
//     val decodedData = cipher.doFinal(encryptedData)
//     return String(decodedData)
// }
//
// private fun getPublicKey()Key{
//     return KeyFactory.getInstance(Algorithm).generatePublic(ECPublicKeySpec)
// }

// private fun setupBouncyCastle() {
//     val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
//         ?: // Web3j will set up the provider lazily when it's first used.
//         return
//
//     if (provider::class.java == BouncyCastleProvider::class.java) {
//         // BC with same package name, shouldn't happen in real life.
//         return
//     }
//
//     // Android registers its own BC provider. As it might be outdated and might not include
//     // all needed ciphers, we substitute it with a known BC bundled in the app.
//     // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
//     // of that it's possible to have another BC implementation loaded in VM.
//     Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
//     Security.insertProviderAt(BouncyCastleProvider(), 1)
// }


// fun newAccount(password: String) {
//     currentCredentials = Credentials.create(password)
//
//
//     // wallet json file is generated with address, public key, private key
//     // also the account would be given initial ether
//
//     // val walletName = WalletUtils.generateNewWalletFile(password, context.filesDir)
//     //
//     // val walletPath = "${context.filesDir}/$walletName"
//     // currentCredentials = WalletUtils.loadCredentials(password, walletPath)
//     //
//     // val transactionReceipt = Transfer.sendFunds(
//     //     web3, ICOCredentials, currentCredentials!!.address,
//     //     BigDecimal.valueOf(2.0), Convert.Unit.ETHER
//     // ).sendAsync().get()
//     // Log.i("newAccount: ", "$transactionReceipt")
//     // Log.i(
//     //     "newAccount: ",
//     //     "${
//     //         Convert.fromWei(
//     //             "${
//     //                 web3.ethGetBalance(
//     //                     currentCredentials!!.address,
//     //                     DefaultBlockParameterName.LATEST
//     //                 )
//     //                     .sendAsync().get().balance
//     //             }", Convert.Unit.ETHER
//     //         )
//     //     }"
//     // )
// }
//
//
// // TODO: Test
// fun invokeTest(function: Function) {
//
//     val nonce = web3.ethGetTransactionCount(
//         currentCredentials!!.address!!, DefaultBlockParameterName.LATEST
//     ).sendAsync().get().transactionCount
//     val transaction = Transaction.createFunctionCallTransaction(
//         currentCredentials!!.address!!,
//         nonce,
//         BigInteger.valueOf(2),
//         BigInteger.valueOf(3000000),
//         ClientConstant.CONTRACT_ADDRESS,
//         FunctionEncoder.encode(function)
//     )
//
//     val txHash = web3.ethSendTransaction(transaction).sendAsync().get().transactionHash
//
//     val receipt = web3.ethGetTransactionReceipt(txHash)
//         .sendAsync().get().transactionReceipt
//     if (receipt.isPresent) {
//         Log.i("invokeTest", "${receipt.get()}")
//     }
// }
//
// fun unLock(address: String) {
//     // admin.ethAccounts().sendAsync().get().accounts
//     if (admin.personalUnlockAccount(address, "").sendAsync().get().accountUnlocked()) {
//         Log.i("BC DEBUG", "success")
//     }
// }
}
