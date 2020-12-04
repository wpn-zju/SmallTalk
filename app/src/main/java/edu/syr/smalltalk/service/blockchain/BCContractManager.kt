package edu.syr.smalltalk.service.blockchain

import android.util.Log
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.web3j.abi.EventEncoder
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint32
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import java.math.BigInteger


class BCContractManager {
    private val httpService = HttpService(ClientConstant.RPC_ENDPOINT)
    private val web3: Web3j = Web3j.build(httpService)
    private val admin: Admin = Admin.build(httpService)
    private val compositeDisposable = CompositeDisposable()

    private var credentials: Credentials? = null

    private lateinit var smalltalkDao: SmallTalkDao

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) {
            smalltalkDao = smallTalkDao
        }
    }

    fun newAccount(password: String) {
        credentials = Credentials.create(password)
    }

    fun clear() {
        compositeDisposable.dispose()
    }

    fun unLock(address: String) {
        if (admin.personalUnlockAccount(address, "").sendAsync().get().accountUnlocked()) {
            Log.i("BC DEBUG","success")
        }
    }

    private val functionp = Function("test",
        listOf(Utf8String("CNM"), Uint32(250)),
        mutableListOf<TypeReference<*>>(
            TypeReference.create(Utf8String::class.java),
            TypeReference.create(Uint32::class.java)
        ))

    fun sendTransaction(functionName: String, params: List<Type<*>>) {
        val output = mutableListOf<TypeReference<*>>()
        val function = Function(functionName, params, output)
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3.ethGetTransactionCount(
            credentials!!.address!!, DefaultBlockParameterName.LATEST
        ).sendAsync().get().transactionCount

        val transaction = Transaction.createFunctionCallTransaction(
            credentials!!.address!!,
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

    val event = Event("Message",
        listOf(
            TypeReference.create(Uint32::class.java),
            TypeReference.create(Utf8String::class.java)))

    fun subscribe() {
        val eventEncoder = EventEncoder.encode(event)
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            ClientConstant.CONTRACT_ADDRESS).addSingleTopic(eventEncoder).addOptionalTopics("10000", null)
        compositeDisposable.add(web3.ethLogFlowable(filter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ e ->
                FunctionReturnDecoder.decode(e.data,
                functionp.outputParameters).forEach {
                    if (it is Uint32) {
                        println(it.value)
                    } else {
                        println(it.value)
                    }
                }
            }, { e ->
                    Log.d("T", e.toString())
            }))
    }
}
