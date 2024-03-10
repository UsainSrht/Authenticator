package com.purpurmc.authenticator.screens.widgets;

import com.google.common.collect.Lists;
import com.purpurmc.authenticator.Authenticator;
import com.purpurmc.authenticator.GAuth;
import com.purpurmc.authenticator.Secret;
import com.purpurmc.authenticator.screens.AuthenticatorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SecretsWidget extends AlwaysSelectedEntryListWidget<SecretsWidget.Entry> {
    private final AuthenticatorScreen screen;
    private final List<SecretEntry> secrets = Lists.newArrayList();
    public SecretsWidget(AuthenticatorScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom);
        this.screen = screen;
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return super.hoveredElement(mouseX, mouseY);
    }

    /*@Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }*/

    private void updateEntries() {
        this.clearEntries();
        secrets.forEach(this::addEntry);
    }

    public void setSelected(@Nullable SecretEntry entry) {
        super.setSelected(entry);
        screen.updateButtonActivationStates();
    }

    public void deleteEntry(SecretEntry entry) {
        Authenticator.LOGGER.info("deleteEntry before secrets " + secrets.size() + "entrycount " + super.getEntryCount());
        for (SecretEntry se : secrets) {
            if (se.getSecret().equals(entry.getSecret())) {
                Authenticator.LOGGER.info("found exact");
                secrets.remove(se);
                super.removeEntry(se);
                break;
            }
        }
        Authenticator.LOGGER.info("deleteEntry after secrets " + secrets.size() + "entrycount " + super.getEntryCount());
        updateEntries();
    }

    public void setSecrets(HashSet<Secret> secrets) {
        for (Secret secret : secrets) {
            this.secrets.add(new SecretEntry(this.client, secret));
        }
        this.updateEntries();
    }

    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 30;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {}*/

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<SecretsWidget.Entry> {
        public Entry() {
        }
    }

    @Environment(EnvType.CLIENT)
    public class SecretEntry extends SecretsWidget.Entry {
        private static final Text LOADING_LIST_TEXT = Text.translatable("text.authenticator.loading");
        private final MinecraftClient client;
        public final String name;
        public final String issuer;
        public final String secret;
        private int code;
        private long clickTime;

        public SecretEntry(MinecraftClient client, Secret secret) {
            this.client = client;
            this.name = secret.name;
            this.issuer = secret.issuer;
            this.secret = secret.secret;
        }

        public Secret getSecret() {
            return new Secret(name, issuer, secret);
        }

        @Override
        public void render(DrawContext matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer renderer = this.client.textRenderer;

            int color = -1;
            matrices.drawText(renderer, this.name, x + 6, y + 1, color, true);
            matrices.drawText(renderer, this.issuer, x + 6, y + 11, color, true);
            code = GAuth.getCode(this.secret);
            String codeString = String.valueOf(code);
            int codeWidth = renderer.getWidth(codeString);
            matrices.drawText(renderer, codeString, x + entryWidth - codeWidth - 10, y + 1, color, true);
            int time = (int) (30 - ((new Date().getTime() / 1000) % 30));
            String timeString = String.valueOf(time);
            int timeWidth = renderer.getWidth(timeString);
            matrices.drawText(renderer, timeString, x + entryWidth - timeWidth - 10, y + 11, color, true);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
            SecretsWidget.this.setSelected((SecretsWidget.Entry)this);
            ((AuthenticatorScreen)this.client.currentScreen).updateButtonActivationStates();
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SecretEntry)) return false;
            return this.getSecret().equals(((SecretEntry) o).getSecret());
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, issuer, secret);
        }

        public Text getNarration() {
            return LOADING_LIST_TEXT;
        }

        public boolean isAvailable() {
            return true;
        }

    }
}
