package com.purpurmc.authenticator.screens.widgets;

import com.google.common.collect.Lists;
import com.purpurmc.authenticator.screens.AuthenticatorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SecretsWidget extends AlwaysSelectedEntryListWidget<AlwaysSelectedEntryListWidget.Entry> {

    private final AuthenticatorScreen screen;
    private final List<SecretEntry> secrets = Lists.newArrayList();
    public SecretsWidget(AuthenticatorScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.screen = screen;
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

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.getSelectedOrNull();
        return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setSecrets(List<SecretEntry> secrets) {
        this.secrets.clear();

        for(int i = 0; i < secrets.size(); ++i) {
            this.secrets.add(new SecretEntry(this.screen, secrets.get(i)));
        }

        this.updateEntries();
    }

    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 30;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }
}
