package com.purpurmc.authenticator.screens.widgets;

import com.google.common.collect.Lists;
import com.purpurmc.authenticator.screens.AuthenticatorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SecretsWidget extends AlwaysSelectedEntryListWidget<SecretsWidget.Entry> {
    private final AuthenticatorScreen screen;
    private List<SecretEntry> secrets = Lists.newArrayList();
    public SecretsWidget(AuthenticatorScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.screen = screen;
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return super.hoveredElement(mouseX, mouseY);
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<SecretsWidget.Entry> {
        public Entry() {
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SecretEntry extends SecretsWidget.Entry {
        private static final Text LOADING_LIST_TEXT = Text.translatable("text.authenticator.loading");
        private final MinecraftClient client;
        private final String name;
        private final String issuer;
        private final String secret;
        private int code;
        private int time;


        public SecretEntry(MinecraftClient client, String name, String issuer, String secret) {
            this.client = client;
            this.name = name;
            this.issuer = issuer;
            this.secret = secret;
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.client.textRenderer.draw(matrices, this.name, 10, 10, 16777215);
            int i = (this.client.currentScreen.width - this.client.textRenderer.getWidth(LOADING_LIST_TEXT)) / 2;
            Objects.requireNonNull(this.client.textRenderer);
            int j = y + (entryHeight - 9) / 2;
            this.client.textRenderer.draw(matrices, LOADING_LIST_TEXT, (float)i, (float)j, 16777215);
            String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
            int k = (this.client.currentScreen.width - this.client.textRenderer.getWidth(string)) / 2;
            Objects.requireNonNull(this.client.textRenderer);
            int l = j + 9;
            this.client.textRenderer.draw(matrices, string, (float)k, (float)l, 8421504);
        }

        public Text getNarration() {
            return LOADING_LIST_TEXT;
        }

        public boolean isAvailable() {
            return false;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void updateEntries() {
        this.clearEntries();
        this.secrets.forEach((secret) -> {
            this.addEntry(secret);
        });
    }

    public void setSelected(@Nullable SecretEntry entry) {
        super.setSelected(entry);
        this.screen.updateButtonActivationStates();
    }

    public void setSecrets(List<SecretEntry> secrets) {
        this.secrets = secrets;

        this.updateEntries();
    }

    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 30;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
