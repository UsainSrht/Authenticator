package com.purpurmc.authenticator.commands;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.purpurmc.authenticator.Authenticator;
import com.purpurmc.authenticator.GAuth;
import com.purpurmc.authenticator.Secret;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.purpurmc.authenticator.commands.arguments.SecretsArgumentType.secretName;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AuthenticatorCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("authenticator")
                .then(literal("getCode")
                        .then(argument("secretname", secretName())
                        .executes(ctx -> getCode(ctx.getSource(), ctx.getArgument("secretname", String.class)))))
                .then(literal("create")
                        .then(argument("secret", string())
                                .then(argument("name", string())
                                        .executes(
                                                ctx -> createSecret(ctx.getSource(),
                                                        ctx.getArgument("secret", String.class),
                                                        ctx.getArgument("name", String.class)))
                                        .then(argument("issuer", string())
                                                .executes(ctx -> createSecret(ctx.getSource(),
                                                        ctx.getArgument("secret", String.class),
                                                        ctx.getArgument("name", String.class),
                                                        ctx.getArgument("issuer", String.class)
                                                ))))))
        );
    }

    private static int getCode(FabricClientCommandSource source, String secretName) {
        String secret = Authenticator.getInstance().getSecretFromName(secretName).secret;
        int code = GAuth.getCode(secret);
        String stringCode = String.valueOf(code);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, stringCode);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click"));
        Text textCode = Text.literal(stringCode).setStyle(Style.EMPTY
                .withUnderline(true)
                .withColor(Formatting.YELLOW)
                .withHoverEvent(hoverEvent)
                .withClickEvent(clickEvent)
        );
        source.sendFeedback(textCode);
        return Command.SINGLE_SUCCESS;
    }

    public static int createSecret(FabricClientCommandSource source, String secret, String name) {
        return createSecret(source, secret, name, "");
    }
    public static int createSecret(FabricClientCommandSource source, String secret, String name, String issuer) {

        if (Authenticator.getInstance().getSecretFromName(name) != null) {
            Text text = Text.literal("secret " + name + " already exists.");
            source.sendError(text);
            return 0;
        }
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
            Text text = Text.literal("An error occurred while saving the secret.");
            source.sendError(text);
            Authenticator.LOGGER.error("An error occurred while saving the secret.", e);
            return 0;
        }
        Text text = Text.literal("secret " + name + " has been saved.");
        source.sendFeedback(text);
        return Command.SINGLE_SUCCESS;
    }
}
