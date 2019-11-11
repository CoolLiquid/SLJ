package com.xw.peng;

public class Activtiy {
    public String path;
    public Data data;

    @Override
    public String toString() {
        return path;
    }

    public class Data {
        String dataOperation;
        String key;
        String data;
    }
}
