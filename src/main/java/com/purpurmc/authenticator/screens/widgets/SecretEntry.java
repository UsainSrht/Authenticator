package com.purpurmc.authenticator.screens.widgets;

import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SecretEntry extends AlwaysSelectedEntryListWidget.Entry {

    public String name;
    public String issuer;
    public String secret;
    public int code;
    public int time;

    @Override
    public Text getNarration() {
        return null;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

    }
}
