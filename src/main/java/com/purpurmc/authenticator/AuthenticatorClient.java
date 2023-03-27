package com.purpurmc.authenticator;

import com.mojang.brigadier.CommandDispatcher;
import com.purpurmc.authenticator.commands.AuthenticatorCommand;
import com.purpurmc.authenticator.events.KeyPress;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class AuthenticatorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(AuthenticatorClient::registerCommands);
        KeyPress.register();
    }

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        AuthenticatorCommand.register(dispatcher);
    }
}
