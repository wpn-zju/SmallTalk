package edu.syr.smalltalk.service.blockchain

import android.util.Log
import android.widget.Toast
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import io.reactivex.disposables.CompositeDisposable
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.FastRawTransactionManager
import java.math.BigInteger
import java.util.*


class BCContractManager {
    private val httpService = HttpService(ClientConstant.RPC_ENDPOINT)
    private val web3: Web3j = Web3j.build(httpService)
    private val admin = Admin.build(httpService)

    private val compositeDisposable = CompositeDisposable()

    // private lateinit var transactionManager: FastRawTransactionManager
    private lateinit var smalltalkDao: SmallTalkDao

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) {
            smalltalkDao = smallTalkDao
        }
    }
    // fun setTransactionManager(credentials: Credentials) {
    //     transactionManager = FastRawTransactionManager(web3, credentials)
    // }

    private fun sendTransaction(from: String) {
        val output = mutableListOf<TypeReference<*>>()
        val function = Function("hello", mutableListOf(), output)
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3.ethGetTransactionCount(
            from, DefaultBlockParameterName.LATEST
        ).sendAsync().get().transactionCount

        val transaction = Transaction.createFunctionCallTransaction(
            from,
            nonce,
            BigInteger.valueOf(2),
            BigInteger.valueOf(3000000),
            ClientConstant.CONTRACT_ADDRESS,
            encodedFunction
        )
        val txHash = web3.ethSendTransaction(transaction).sendAsync().get().transactionHash
        val receipt = web3.ethGetTransactionReceipt(txHash).sendAsync()
            .get().transactionReceipt
        if (receipt.isPresent) {
            Log.i("BC DEBUG", "${receipt.get()}")
        }
    }

    private fun newAccount(password: String): Credentials {
        return Credentials.create(password)
    }

    private fun unLock(address: String) {
        if (admin.personalUnlockAccount(address, "").sendAsync().get().accountUnlocked()) {
            Log.i("BC DEBUG","success")
        }
    }

    private fun subscribe() {
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST, ClientConstant.CONTRACT_ADDRESS
        )
        compositeDisposable.add(web3.ethLogFlowable(filter).subscribe {
            // TODO
            Log.i("BC DEBUG", it.data)
        })

    }
}