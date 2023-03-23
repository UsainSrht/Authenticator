package com.purpurmc.authenticator.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.purpurmc.authenticator.Authenticator;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SecretsArgumentType implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("UsainSrht", "uTester", "SomeRandomName", "cs2");
    private static final DynamicCommandExceptionType INVALID_SECRET_NAME = new DynamicCommandExceptionType(name -> Text.translatable("argument.enum.invalid", name));

    public static SecretsArgumentType secretName() {
        return new SecretsArgumentType();
    }

    @Override
    public String parse(final StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        if (!Authenticator.getInstance().secrets.containsKey(string)) {
            throw INVALID_SECRET_NAME.create(string);
        }
        return string;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Authenticator.getInstance().secrets.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
