package com.purpurmc.authenticator.screens;

import com.purpurmc.authenticator.screens.widgets.SecretsWidget;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class AuthenticatorScreen extends Screen implements ConfigScreenFactory<Screen> {
    private static final Text TITLE = Text.translatable("screen.authenticator.title");
    private static final Text createButtonText = Text.translatable("button.authenticator.create");
    private static final Text deleteButtonText = Text.translatable("button.authenticator.delete");
    private static final Text editButtonText = Text.translatable("button.authenticator.edit");
    private static final Text backButtonText = Text.translatable("button.authenticator.back");

    private final Screen background;

    public AuthenticatorScreen(Screen background) {
        super(TITLE);
        this.background = background;
    }

    @Override
    protected void init() {
        super.init();

        this.clearChildren();

        ButtonWidget createButton = ButtonWidget.builder(createButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            this.client.player.sendMessage(Text.literal("create"));
        }).dimensions(this.width / 2 - 213, this.height - 40, 100, 20).build();

        ButtonWidget deleteButton = ButtonWidget.builder(deleteButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            this.client.player.sendMessage(Text.literal("delete"));
        }).dimensions(this.width / 2 - 103, this.height - 40, 100, 20).build();

        ButtonWidget editButton = ButtonWidget.builder(editButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            this.client.player.sendMessage(Text.literal("edit"));
        }).dimensions(this.width / 2 + 3, this.height - 40, 100, 20).build();

        ButtonWidget backButton = ButtonWidget.builder(backButtonText, (btn) -> {
            btn.active = false;
            close();
        }).dimensions(this.width / 2 + 113, this.height - 40, 100, 20).build();

        SecretsWidget secretsWidget = new SecretsWidget(
                this,
                this.client,
                0,
                0,
                0,
                0,
                0
        );

        this.addDrawableChild(createButton);
        this.addDrawableChild(deleteButton);
        this.addDrawableChild(editButton);
        this.addDrawableChild(backButton);
        this.addDrawableChild(secretsWidget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, title, this.width / 2, 10, 0xFFFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public Screen create(Screen parent) {
        return this;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(background);
    }


    public boolean shouldCloseOnEsc() {
        return true;
    }
}
