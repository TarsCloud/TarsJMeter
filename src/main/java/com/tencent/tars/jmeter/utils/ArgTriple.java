package com.tencent.tars.jmeter.utils;

public class ArgTriple extends Triple<String, String, String> {
    private String ml;
    private String mm;
    private String mr;

    public ArgTriple(String l, String m, String r) {
        ml = l;
        mm = m;
        mr = r;
    }

    @Override
    public String getLeft() {
        return ml;
    }

    @Override
    public String getMiddle() {
        return mm;
    }

    @Override
    public String getRight() {
        return mr;
    }

}