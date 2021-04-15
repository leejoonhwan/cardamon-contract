package com.gravity9.cardamon.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;


public class EthereumService {

    private String from = "0x62e3019dc896694E7018f6411D69A78Ba9221553";

    private String contract = "0xE4550068a45a21a009e34c529f993e94B9252b6b";

    // hardcording because of testing
    private String pwd = "0x2eb072f1fff1007cef319e5f9c1bcd2fc1fc25d82e0763fb707216e6df6b7347";

    private Admin web3j = null;

    public EthereumService()
    {
        web3j = Admin.build(new HttpService("https://ropsten.infura.io/v3/184c26ddfb724b2189e054c66191b60c")); // default server : http://localhost:8545
    }

    public Object ethCall(Function function) throws IOException {
        Transaction transaction = Transaction.createEthCallTransaction(from, contract,
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

        // 1. Account Lock 해제
        PersonalUnlockAccount personalUnlockAccount = web3j.personalUnlockAccount(from, pwd).send();

        if (personalUnlockAccount.accountUnlocked()) { // unlock 일때

            //2. account에 대한 nonce값 가져오기.
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    from, DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            //3. Transaction값 제작
            Transaction transaction = Transaction.createFunctionCallTransaction(from, nonce,
                                                                                Transaction.DEFAULT_GAS,
                                                                                null, contract,
                                                                                FunctionEncoder.encode(function));

            // 4. ethereum Call &
            EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).send();

            // transaction에 대한 transaction Hash값 얻기.
            String transactionHash = ethSendTransaction.getTransactionHash();

            // ledger에 쓰여지기 까지 기다리기.
            Thread.sleep(5000);

            return transactionHash;
        }
        else {
            throw new PersonalLockException("check ethereum personal Lock");
        }
    }

    public TransactionReceipt getReceipt(String transactionHash) throws IOException {

        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();

        if(transactionReceipt.getTransactionReceipt().isPresent())
        {
            System.out.println("transactionReceipt.getResult().getContractAddress() = " +
                               transactionReceipt.getResult());
        }
        else
        {
            System.out.println("transaction complete not yet");
        }

        return transactionReceipt.getResult();
    }

    private class PersonalLockException extends RuntimeException
    {
        public PersonalLockException(String msg)
        {
            super(msg);
        }
    }

}
