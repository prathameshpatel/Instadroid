package io.github.prathameshpatel.instadroid.model;


public class InstagramResponse {

    private Data[] data;
    private Meta meta;

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }
}