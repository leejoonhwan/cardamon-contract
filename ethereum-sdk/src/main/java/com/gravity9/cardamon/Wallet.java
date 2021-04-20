package com.gravity9.cardamon;

import com.google.gson.JsonObject;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Wallet {

    private final String URL="HTTP://127.0.0.1:7545";

    Web3j web3j = Web3j.build(new HttpService(URL));

    public static void main(String[] args) {
        Wallet wallet = new Wallet();
//        JsonObject processJson = wallet.genKey();
//        System.out.println(new Gson().toJson(processJson));
//
//        String balance = wallet.getBalance("0x5775065694Cb35e0e758638B6F3563C6a5F8D2DA");
//        System.out.println(balance);

        wallet.transfer("9ea5c0de8636a12364d89d9de5638615b855ef6bbabaee9169b00394001777cd", "0x5775065694Cb35e0e758638B6F3563C6a5F8D2DA", "3", new BigDecimal(3));
    }

    public JsonObject genKey() {
        JsonObject processJson = new JsonObject();

        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

            String sPrivatekeyInHex = privateKeyInDec.toString(16);

            WalletFile aWallet = org.web3j.crypto.Wallet.createLight("password", ecKeyPair);
            String sAddress = aWallet.getAddress();

            processJson.addProperty("address", "0x" + sAddress);
            processJson.addProperty("privatekey", sPrivatekeyInHex);


        } catch (CipherException e) {
            //
        } catch (InvalidAlgorithmParameterException e) {
            //
        } catch (NoSuchAlgorithmException e) {
            //
        } catch (NoSuchProviderException e) {
            //
        }
        return processJson;
    }

    public String getBalance(String address) {
        BigInteger balance = null;
        try {
            EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.PENDING).send();
            balance = ethGetBalance.getBalance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return balance.toString();
    }

    public void transfer (String senderPrivateKey, String receiverAddress,String chainId, BigDecimal amountInWei) {
        // get sender wallet credential info
        Credentials credential = getCredential(senderPrivateKey);

        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(credential.getAddress(), DefaultBlockParameterName.LATEST).send();

            // 트랜잭션 번호, address생성 이후부터 거래마다 nonce 1씩 증가 (고유값)
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            // 보낼 이더 값
            BigInteger value = Convert.toWei("0.001", Convert.Unit.ETHER).toBigInteger();

            // 가스제한, 이부분 수기로 확인해야함
            BigInteger gasLimit = BigInteger.valueOf(21000);
            BigInteger gasPrice = Convert.toWei("1", Convert.Unit.GWEI).toBigInteger();

            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, receiverAddress, value);


        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/walletfile");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CipherException e) {
//            e.printStackTrace();
//        }

    }


    private Credentials getCredential(String privateKeyInHex) {
        BigInteger privateKeyInBT = new BigInteger(privateKeyInHex, 16);
        ECKeyPair aPair = ECKeyPair.create(privateKeyInBT);
        Credentials aCredential = Credentials.create(aPair);
        return aCredential;
    }
}
