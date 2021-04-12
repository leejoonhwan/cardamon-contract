package com.gravity9.cardamon.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class EthServiceTest {

    private final String URL="HTTP://127.0.0.1:7545";

    @Test
    public JsonObject getEthWallet() throws Exception
    {
        JsonObject processJson = new JsonObject();

        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

            String sPrivatekeyInHex = privateKeyInDec.toString(16);

            WalletFile aWallet = Wallet.createLight("password", ecKeyPair);
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

        System.out.println(new Gson().toJson(processJson));
        return processJson;
    }

    @Test
    public void getEthClientVersionSync() throws Exception
    {
        Web3j web3j = Web3j.build(new HttpService(URL));
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        System.out.println(web3ClientVersion.getWeb3ClientVersion());
    }


    @Test
    public void getEthClientVersionASync() throws Exception
    {
        Web3j web3 = Web3j.build(new HttpService(URL));  // defaults to http://localhost:8545/
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
        System.out.println(web3ClientVersion.getWeb3ClientVersion());
    }

    @Test
    public void getEthClientVersionRx() throws Exception
    {
        Web3j web3 = Web3j.build(new HttpService(URL));
        web3.web3ClientVersion().flowable().subscribe(x -> {
            System.out.println(x.getWeb3ClientVersion());
        });

        Thread.sleep(5000);
    }
}