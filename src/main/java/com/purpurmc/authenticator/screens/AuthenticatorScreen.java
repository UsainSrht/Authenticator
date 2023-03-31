package com.purpurmc.authenticator.screens;

import com.purpurmc.authenticator.Authenticator;
import com.purpurmc.authenticator.screens.widgets.SecretsWidget;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class AuthenticatorScreen extends Screen implements ConfigScreenFactory<Screen> {
    private static final Text TITLE = Text.translatable("screen.authenticator.title");
    private static final Text createButtonText = Text.translatable("button.authenticator.create");
    private static final Text deleteButtonText = Text.translatable("button.authenticator.delete");
    private static final Text editButtonText = Text.translatable("button.authenticator.edit");
    private static final Text backButtonText = Text.translatable("button.authenticator.back");

    private final Screen background;
    private SecretsWidget.SecretEntry selected;
    private SecretsWidget secretsWidget;
    private ButtonWidget editButton;
    private ButtonWidget deleteButton;

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
            this.client.setScreen(new CreateSecretScreen(this));
        }).dimensions(this.width / 2 - 209, this.height - 40, 100, 20).build();

        ButtonWidget deleteButton = ButtonWidget.builder(deleteButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
        }).dimensions(this.width / 2 - 103, this.height - 40, 100, 20).build();
        deleteButton.active = false;
        this.deleteButton = deleteButton;

        ButtonWidget editButton = ButtonWidget.builder(editButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            AlwaysSelectedEntryListWidget.Entry entry = secretsWidget.getSelectedOrNull();
            if (entry != null) {
                SecretsWidget.SecretEntry secretEntry = (SecretsWidget.SecretEntry) entry;
                //Authenticator.getInstance().removeSecret(secretEntry)
            }
        }).dimensions(this.width / 2 + 3, this.height - 40, 100, 20).build();
        editButton.active = false;
        this.editButton = editButton;

        ButtonWidget backButton = ButtonWidget.builder(backButtonText, (btn) -> {
            btn.active = false;
            close();
        }).dimensions(this.width / 2 + 109, this.height - 40, 100, 20).build();

        SecretsWidget secretsWidget = new SecretsWidget(
                this,
                this.client,
                this.width,
                this.height - 100,
                50,
                this.height - 50,
                25
        );
        secretsWidget.setSecrets(Authenticator.getInstance().secrets);
        this.secretsWidget = secretsWidget;

        this.addDrawableChild(secretsWidget);
        this.addDrawableChild(createButton);
        this.addDrawableChild(deleteButton);
        this.addDrawableChild(editButton);
        this.addDrawableChild(backButton);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, title, this.width / 2, 15, 0xFFFFFFFF);
        //DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, Text.literal(mouseX + " " + mouseY + " " + delta), this.width / 2, 20, 0xFFFFFFFF);
        //DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("width " + this.width + " height " + this.height), this.width / 2, 30, 0xFFFFFFFF);
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

    public void updateButtonActivationStates() {
        AlwaysSelectedEntryListWidget.Entry entry = secretsWidget.getSelectedOrNull();
        if (entry == null) {
            editButton.active = false;
            deleteButton.active = false;
        }
        else {
            editButton.active = true;
            deleteButton.active = true;
        }
    }
}
