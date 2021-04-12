package com.gravity9.cardamon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Wallet {
    public static void main(String[] args) {
        Wallet wallet = new Wallet();
        wallet.genKey();
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

        System.out.println(new Gson().toJson(processJson));
        return processJson;
    }
}
