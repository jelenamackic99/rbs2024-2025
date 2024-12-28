package com.zuehlke.securesoftwaredevelopment.domain;

public class Voucher {

    int id;
    private String code;
    int value;


    public Voucher(int id, String code, int value) {
        this.id = id;
        this.code = code;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
