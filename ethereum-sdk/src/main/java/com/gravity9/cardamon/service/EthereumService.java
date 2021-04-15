package com.gravity9.cardamon.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;


public class EthereumService {

    private String from = "0x62e3019dc896694E7018f6411D69A78Ba9221553";
    private String fromPrivateKey = "2eb072f1fff1007cef319e5f9c1bcd2fc1fc25d82e0763fb707216e6df6b7347";

    private String contractAddr = "0xE4550068a45a21a009e34c529f993e94B9252b6b";


    private Web3j web3j = null;

    public EthereumService()
    {
        web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/184c26ddfb724b2189e054c66191b60c")); // default server : http://localhost:8545

    }

    public Object ethCall(Function function) throws IOException {
        Transaction transaction = Transaction.createEthCallTransaction(from, contractAddr,
                FunctionEncoder.encode(function));

        //3. ethereum 호출후 결과 가져오기
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

        //4. 결과값 decode
        List<Type> decode = FunctionReturnDecoder.decode(ethCall.getResult(), function.getOutputParameters());
        System.out.println("ethCall.getResult() = " + decode.get(0));

        return decode.get(0).getValue();
    }

    public String ethSendTransaction(Function function)
            throws IOException, InterruptedException, ExecutionException {

        Credentials credential = getCredential(fromPrivateKey);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        BigInteger gasLimit = Contract.GAS_LIMIT;
        BigInteger gasPrice = Contract.GAS_PRICE;
        String txData = FunctionEncoder.encode(function);


        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddr, txData);


        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credential);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        return ethSendTransaction.getResult();
    }

    public String updateContract(Function function)
            throws IOException, InterruptedException, ExecutionException {

        String encodedFunction = FunctionEncoder.encode(function);

        EthSendTransaction ethCall = null;
        try {
            //Nonce
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    from, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = Transaction.DEFAULT_GAS;

            //Run
            ethCall = web3j.ethSendTransaction(
                    Transaction.createFunctionCallTransaction(
                            from,
                            nonce, //or nullL
                            gasPrice, //gasPrice
                            BigInteger.valueOf(900000), //gasLimit
                            contractAddr,
                            encodedFunction)
            ).sendAsync().get();
        }catch (Exception e) {
            e.printStackTrace();
        }

        String transactionHash = ethCall.getTransactionHash();

        return transactionHash;
    }

    private Credentials getCredential(String privateKeyInHex) {
        BigInteger privateKeyInBT = new BigInteger(privateKeyInHex, 16);
        ECKeyPair aPair = ECKeyPair.create(privateKeyInBT);
        Credentials aCredential = Credentials.create(aPair);
        return aCredential;
    }
}
