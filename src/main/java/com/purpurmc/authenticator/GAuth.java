package com.purpurmc.authenticator;

import com.warrenstrange.googleauth.GoogleAuthenticator;

public class GAuth {

    public static GoogleAuthenticator authenticator = new GoogleAuthenticator();

    public static boolean checkCode(String secret, int key) {
        return authenticator.authorize(secret, key);
    }

    public static int getCode(String secret) {
        return authenticator.getTotpPassword(secret);
    }

}
