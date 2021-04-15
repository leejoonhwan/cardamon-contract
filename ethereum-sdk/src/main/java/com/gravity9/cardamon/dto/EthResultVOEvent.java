package com.gravity9.cardamon.dto;

import java.util.Arrays;

public class EthResultVOEvent implements EthResultInterface{
    private String jsonrpc;
    private String id;
    private Result[] result;

    @Override
    public String toString() {
        return "\nGethResultVO{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id='" + id + '\'' +
                ", result=" + Arrays.toString(result)+ '\'' +
                '}';
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Result[] getResult() {
        return result;
    }

    public void setResult(Result[] result) {
        this.result = result;
    }


}