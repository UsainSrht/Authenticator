package com.purpurmc.authenticator;

public class Secret {
    public String name;
    public String issuer;
    public String secret;

    public Secret(String name, String issuer, String secret) {
        this.name = name;
        this.issuer = issuer;
        this.secret = secret;
    }

    public int getCode() {
        return GAuth.getCode(secret);
    }
}
