package com.purpurmc.authenticator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashSet;

public class Authenticator implements ModInitializer {

    public static Authenticator instance;
    public static final Logger LOGGER = LoggerFactory.getLogger("authenticator");
    public HashSet<Secret> secrets = new HashSet<>();

    @Override
    public void onInitialize() {

        instance = this;

        try {
            loadConfig();
        }
        catch (IOException e) {
            LOGGER.error("an error occurred while loading the config", e);
        }

        LOGGER.info("Authenticator initialized.");
    }

    public static Authenticator getInstance() {
        return instance;
    }

    public void loadConfig() throws IOException {
        File config = new File(FabricLoader.getInstance().getConfigDir().toString(), "authenticator-secrets.json");
        if (!config.exists()) {
            boolean omg = config.createNewFile();
            FileWriter fw = new FileWriter(config);
            fw.write("[]");
            fw.close();
        }
        Gson gson = new Gson();
        String data = Files.readString(config.toPath());
        JsonArray jsonArray = gson.fromJson(data, JsonArray.class);
        jsonArray.forEach((jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String issuer = jsonObject.get("issuer").getAsString();
            String secret = jsonObject.get("secret").getAsString();
            Secret secretObject = new Secret(name, issuer, secret);
            secrets.add(secretObject);
        }));
    }

    public Secret getSecretFromName(String name) {
        for (Secret secret : secrets) {
            if (secret.name.equals(name)) return secret;
        }
        return null;
    }

    public void createSecret(String secret, String name, String issuer) {
        Secret secretObject = new Secret(name, issuer, secret);
        Authenticator.getInstance().secrets.add(secretObject);
        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashSet<Secret>>(){}.getType();
        String json = gson.toJson(Authenticator.getInstance().secrets, gsonType);
        File config = new File(FabricLoader.getInstance().getConfigDir().toString(), "authenticator-secrets.json");
        try {
            FileWriter fw = new FileWriter(config);
            fw.write(json);
            fw.close();
        }
        catch (IOException e) {
            Authenticator.LOGGER.error("An error occurred while saving the secret.", e);
        }
    }
}
