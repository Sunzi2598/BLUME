package com.company.blumeshipper.Model;

public class TokenModel {
    private String phone, token;
    private boolean shippertoken,serverToken;

    public TokenModel() {
    }


    public TokenModel(String phone, String token, boolean serverToken,boolean shippertoken) {
        this.phone = phone;
        this.token = token;
        this.shippertoken = shippertoken;
        this.serverToken = serverToken;
    }

    public boolean isShippertoken() {
        return shippertoken;
    }

    public void setShippertoken(boolean shippertoken) {
        this.shippertoken = shippertoken;
    }

    public boolean isServerToken() {
        return serverToken;
    }

    public void setServerToken(boolean serverToken) {
        this.serverToken = serverToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
