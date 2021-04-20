package com.gravity9.cardamon;

import com.gravity9.cardamon.service.EthereumService;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Nft {
    private String from = "0xF76c9B7012c0A3870801eaAddB93B6352c8893DB";

    private String contract = "0x55815f746a53574523296831B33c2277B2760562";

    // hardcording because of testing
    private String pwd = "0x8d22a0aa9c43da157ebc24bc7d70c26d198381e042ab93434757752e3f0ee8e5";

    private EthereumService ethereumService = null;

    public Nft() {
        ethereumService = new EthereumService();
    }

    public static void main(String[] args) {
        Nft nft = new Nft();

        //nft.send();
        nft.call();


    }

    private void send() {

        Type [] params = new Type[]{
                new Address("0x1b3fEC8C2f048c35590A47794051A603DE20F1d6"),
                new Uint256(0),
                new Utf8String("https://www.youtube.com/watch?v=yUDzKb_8GMk")
        };

        Function function = new Function(
                "mint",
                Arrays.asList(params),
                Collections.emptyList());

        String txHash = null;
        try {
            txHash = ethereumService.ethSendTransaction(function);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(txHash);
    }

    private void call () {
        Function function = new Function("guarantorOf",
                Arrays.asList(new Uint256(0)),
                Arrays.asList(new TypeReference<Address>() {}));

        try {
            ethereumService.ethCall(function);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    private void txinfo () {

    }
}
