package com.purpurmc.authenticator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class Authenticator implements ModInitializer {

    public static Authenticator instance;
    public static final Logger LOGGER = LoggerFactory.getLogger("authenticator");
    public HashMap<String, String> secrets = new HashMap<>();

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
            fw.write("{}");
            fw.close();
        }
        Gson gson = new Gson();
        String data = Files.readString(config.toPath());
        JsonObject json = gson.fromJson(data, JsonObject.class);
        for (String name : json.keySet()) {
            secrets.put(name, json.get(name).getAsString());
        }
    }


}
