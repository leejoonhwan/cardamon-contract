package com.gravity9.cardamon;

import com.gravity9.cardamon.service.EthereumService;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Nft {
    private String from = "0xF76c9B7012c0A3870801eaAddB93B6352c8893DB";

    private String contract = "0x55815f746a53574523296831B33c2277B2760562";

    // hardcording because of testing
    private String pwd = "0x8d22a0aa9c43da157ebc24bc7d70c26d198381e042ab93434757752e3f0ee8e5";

    EthereumService ethereumService = null;


    public static void main(String[] args) {
        Nft nft = new Nft();

        nft.process();
    }

    private void process () {
        ethereumService = new EthereumService();
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
}
