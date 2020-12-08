package edu.syr.smalltalk.service.blockchain

import android.content.Context
import android.util.Log
import edu.syr.smalltalk.service.model.logic.SmallTalkDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.web3j.abi.EventEncoder
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger


class BCContractManager(private val context: Context) {
    private val httpService = HttpService(ClientConstant.RPC_ENDPOINT)
    private val web3: Web3j = Web3j.build(httpService)
    private val admin: Admin = Admin.build(httpService)
    private val compositeDisposable = CompositeDisposable()
    private var currentCredential = Credentials.create(ClientConstant.ICO_PRIVATE_KEY)

    private val gasProvider = object : DefaultGasProvider() {
        override fun getGasPrice(contractFunc: String): BigInteger {
            return BigInteger("300")
        }

        override fun getGasLimit(contractFunc: String): BigInteger {
            return BigInteger("3000000")
        }
    }

    private val contract = Relay.deploy(web3, currentCredential, gasProvider).sendAsync().get()

    private lateinit var smalltalkDao: SmallTalkDao
    private var accounts: List<String>

    init {
        accounts = web3.ethAccounts().sendAsync().get().accounts

    }

    fun setDataAccessor(smallTalkDao: SmallTalkDao) {
        if (!this::smalltalkDao.isInitialized) {
            smalltalkDao = smallTalkDao
        }
    }

    fun connect() {
        val filter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            contract.contractAddress
        ).addSingleTopic(EventEncoder.encode(Relay.MESSAGEEVENT_EVENT))
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

    fun disconnect() {
        compositeDisposable.dispose()
    }

    fun forward(){

    }

    // private val keyFactory = KeyFactory.getInstance("RSA")
    //
    // // TODO: add encryption&decryption
    // private fun encrypt(data: String): String {
    //     val publicBytes: ByteArray =
    //         Base64.getDecoder().decode(currentCredential.ecKeyPair.publicKey.toString())
    //     val pubKey = keyFactory.generatePublic(X509EncodedKeySpec(publicBytes))
    //
    //     val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    //     cipher.init(Cipher.ENCRYPT_MODE, pubKey)
    //     val bytes = cipher.doFinal(data.toByteArray())
    //     return Base64.getEncoder().encodeToString(bytes)
    // }
    //
    // private fun decrypt(data: String): String {
    //     val privateBytes: ByteArray =
    //         Base64.getDecoder().decode(currentCredential.ecKeyPair.privateKey.toString())
    //     val privateKey = keyFactory.generatePublic(X509EncodedKeySpec(privateBytes))
    //
    //     val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    //     cipher.init(Cipher.DECRYPT_MODE, privateKey)
    //     val encryptedData = Base64.getDecoder().decode(data.toByteArray())
    //     val decodedData = cipher.doFinal(encryptedData)
    //     return String(decodedData)
    // }


}
