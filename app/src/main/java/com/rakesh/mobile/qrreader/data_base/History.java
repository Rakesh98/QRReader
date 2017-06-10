package com.rakesh.mobile.qrreader.data_base;

/**
 * Created by rakesh.jnanagari on 07/05/17.
 */

public class History {
    private String result;
    private String date;
    private String type;

    public History() {
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
