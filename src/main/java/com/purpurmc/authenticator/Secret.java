package com.purpurmc.authenticator;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Secret)) return false;
        Secret other = (Secret)o;
        return (name.equalsIgnoreCase(other.name) && issuer.equalsIgnoreCase(other.issuer) && secret.equalsIgnoreCase(other.secret));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, issuer, secret);
    }
}
