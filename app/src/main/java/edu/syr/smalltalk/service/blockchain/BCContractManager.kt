package edu.syr.smalltalk.service.blockchain

import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Function
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.FastRawTransactionManager
import java.math.BigInteger
import java.util.*


class BCContractManager {
    var web3: Web3j =
        Web3j.build(HttpService(ClientConstant.RPC_ENDPOINT))
    private lateinit var transactionManager: FastRawTransactionManager
    private lateinit var smalltalkDao: SmallTalkDao

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) {
            smalltalkDao = smallTalkDao
        }
    }

    fun setTransactionManager(credentials: Credentials) {
        transactionManager = FastRawTransactionManager(web3, credentials)
    }

    fun sendTransaction(credentials: Credentials, toAddress: String, input: List<Int>) {
        // val output = emptyList<Int>()
        val function = Function("functionName", mutableListOf(), mutableListOf())
        val from = "TODo"
        val gasPrice = BigInteger("3")
        val gasLimit = BigInteger("300000")
        val value = BigInteger("10000000")
        val data = FunctionEncoder.encode(function)
        val transaction = Transaction.createFunctionCallTransaction(
            from,
            BigInteger("0"),
            gasPrice,
            gasLimit,
            ClientConstant.CONTRACT_ADDRESS,
            value,
            FunctionEncoder.encode(function)
        )
        val transactionResponse =
            web3.ethSendTransaction(transaction).sendAsync().get();
        val transactionHash = transactionResponse.transactionHash

    }


}