package com.purpurmc.authenticator.screens.widgets;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.purpurmc.authenticator.GAuth;
import com.purpurmc.authenticator.screens.AuthenticatorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SecretsWidget extends AlwaysSelectedEntryListWidget<SecretsWidget.Entry> {
    private final AuthenticatorScreen screen;
    private List<SecretEntry> secrets = Lists.newArrayList();
    private SecretEntry selectedEntry;
    public SecretsWidget(AuthenticatorScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.screen = screen;
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return super.hoveredElement(mouseX, mouseY);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

    /*public void addEntry(SecretEntry entry) {
        this.secrets.add(entry);
        updateEntries();
    }

    public void removeEntry(SecretEntry entry) {
        this.secrets.remove(entry);
        updateEntries();
    }*/

    private void updateEntries() {
        this.clearEntries();
        this.secrets.forEach(this::addEntry);
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
        private long clickTime;


        public SecretEntry(MinecraftClient client, String name, String issuer, String secret) {
            this.client = client;
            this.name = name;
            this.issuer = issuer;
            this.secret = secret;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer renderer = this.client.textRenderer;

            int color = -1;
            renderer.draw(matrices, this.name, x + 10, y + 1, color);
            int nameWidth = renderer.getWidth(this.name);
            renderer.draw(matrices, this.issuer, x + 15 + nameWidth, y + 1, color);
            code = GAuth.getCode(this.secret);
            String codeString = String.valueOf(code);
            int codeWidth = renderer.getWidth(codeString);
            renderer.draw(matrices, codeString, x + entryWidth - codeWidth - 5, y + 1, color);
            time = 30 - (Calendar.SECOND % 30);
            renderer.draw(matrices, String.valueOf(time), x + entryWidth - codeWidth - 5, y + 7, color);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.setFocused(true);
            if (Util.getMeasuringTimeMs() - this.clickTime < 250L) {
                String code = String.valueOf(this.code);
                this.client.keyboard.setClipboard(code);
                this.client.getToastManager().add(new SystemToast(
                        SystemToast.Type.NARRATOR_TOGGLE,
                        Text.literal(code),
                        Text.translatable("text.authenticator.copy")));
            } else {
                this.clickTime = Util.getMeasuringTimeMs();
            }
            return true;
        }

        public Text getNarration() {
            return LOADING_LIST_TEXT;
        }

        public boolean isAvailable() {
            return true;
        }

    }
}
