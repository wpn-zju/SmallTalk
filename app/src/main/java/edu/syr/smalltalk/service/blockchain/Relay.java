package edu.syr.smalltalk.service.blockchain;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.8.1.
 */
@SuppressWarnings("rawtypes")
public class Relay extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610638806100206000396000f3fe608060405234801561001057600080fd5b5060043610610068577c010000000000000000000000000000000000000000000000000000000060003504631aa3a008811461006d578063832880e714610077578063a87430ba14610109578063b2dd5c07146101bb575b600080fd5b610075610202565b005b61007f610312565b604051808060200184815260200183151515158152602001828103825285818151815260200191508051906020019080838360005b838110156100cc5781810151838201526020016100b4565b50505050905090810190601f1680156100f95780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b61013c6004803603602081101561011f57600080fd5b503573ffffffffffffffffffffffffffffffffffffffff16610443565b6040518080602001838152602001828103825284818151815260200191508051906020019080838360005b8381101561017f578181015183820152602001610167565b50505050905090810190601f1680156101ac5780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b6101ee600480360360208110156101d157600080fd5b503573ffffffffffffffffffffffffffffffffffffffff166104e7565b604080519115158252519081900360200190f35b3360009081526001602052604090205460ff161561026b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602b8152602001806105b2602b913960400191505060405180910390fd5b6102736104fc565b5060408051608081018252600c8183019081527f64656661756c74206e616d650000000000000000000000000000000000000000606083015281526000602080830182905233825281815292902081518051929384936102d69284920190610516565b506020919091015160019091015560405133907f83e6340a2c2f790510eaa40c013792999b4c261c1371c5c7c280f990cc496fe090600090a250565b33600090815260016020819052604082205460609291829160ff16151514610385576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260278152602001806105dd6027913960400191505060405180910390fd5b33600090815260208181526040808320600180820154818552948390205482548451600261010094831615949094026000190190911692909204601f810186900486028301860190945283825291949360ff9092169290918591908301828280156104315780601f1061040657610100808354040283529160200191610431565b820191906000526020600020905b81548152906001019060200180831161041457829003601f168201915b50505050509250925092509250909192565b6000602081815291815260409081902080548251601f6002600019610100600186161502019093169290920491820185900485028101850190935280835290928391908301828280156104d75780601f106104ac576101008083540402835291602001916104d7565b820191906000526020600020905b8154815290600101906020018083116104ba57829003601f168201915b5050505050908060010154905082565b60016020526000908152604090205460ff1681565b604051806040016040528060608152602001600081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061055757805160ff1916838001178555610584565b82800160010185558215610584579182015b82811115610584578251825591602001919060010190610569565b50610590929150610594565b5090565b6105ae91905b80821115610590576000815560010161059a565b9056fe73656e646572206163636f756e7420697320616c726561647920612072656769737465726564207573657273656e646572206163636f756e74206973206e6f74206120726567697374657265642075736572a265627a7a72315820358aaff696891e5202cd294eacd2b3db8fdbdc42c664d014d7874eb1a604903b64736f6c63430005100032";

    public static final String FUNC_GETUSER = "getUser";

    public static final String FUNC_REGISTER = "register";

    public static final String FUNC_REGISTERED = "registered";

    public static final String FUNC_USERS = "users";

    public static final Event REGISTERSUCCESS_EVENT = new Event("RegisterSuccess",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected Relay(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Relay(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Relay(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Relay(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<RegisterSuccessEventResponse> getRegisterSuccessEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REGISTERSUCCESS_EVENT, transactionReceipt);
        ArrayList<RegisterSuccessEventResponse> responses = new ArrayList<RegisterSuccessEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RegisterSuccessEventResponse typedResponse = new RegisterSuccessEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.who = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RegisterSuccessEventResponse> registerSuccessEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RegisterSuccessEventResponse>() {
            @Override
            public RegisterSuccessEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REGISTERSUCCESS_EVENT, log);
                RegisterSuccessEventResponse typedResponse = new RegisterSuccessEventResponse();
                typedResponse.log = log;
                typedResponse.who = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RegisterSuccessEventResponse> registerSuccessEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REGISTERSUCCESS_EVENT));
        return registerSuccessEventFlowable(filter);
    }

    public RemoteFunctionCall<Tuple3<String, BigInteger, Boolean>> getUser() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUSER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple3<String, BigInteger, Boolean>>(function,
                new Callable<Tuple3<String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple3<String, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, BigInteger, Boolean>(
                                (String) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue(),
                                (Boolean) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> register() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REGISTER,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> registered(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REGISTERED,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple2<String, BigInteger>> users(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_USERS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<String, BigInteger>>(function,
                new Callable<Tuple2<String, BigInteger>>() {
                    @Override
                    public Tuple2<String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, BigInteger>(
                                (String) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    @Deprecated
    public static Relay load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Relay(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Relay load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Relay(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Relay load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Relay(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Relay load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Relay(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Relay> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Relay.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Relay> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Relay.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Relay> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Relay.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Relay> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Relay.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class RegisterSuccessEventResponse extends BaseEventResponse {
        public String who;
    }
}
