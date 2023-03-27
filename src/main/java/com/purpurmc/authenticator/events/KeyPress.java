package com.purpurmc.authenticator.events;

import com.purpurmc.authenticator.screens.AuthenticatorScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyPress {
    public static final String KEY_CATEGORY = "key.categories.authenticator";
    public static final String KEY = "key.open_authenticator";

    public static KeyBinding authenticatorKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (authenticatorKey.wasPressed()) {
                client.setScreen(new AuthenticatorScreen(client.currentScreen));
            }
        });
    }

    public static void register() {
        authenticatorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY
        ));

        registerKeyInputs();
    }
}
