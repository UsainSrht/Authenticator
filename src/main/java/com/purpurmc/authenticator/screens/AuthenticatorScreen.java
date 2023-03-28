package com.purpurmc.authenticator.screens;

import com.purpurmc.authenticator.Authenticator;
import com.purpurmc.authenticator.screens.widgets.SecretsWidget;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

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

        EditBoxWidget editBoxWidget = new EditBoxWidget(textRenderer, this.width / 2 - 103, this.height - 70, 100, 20,
                Text.literal("placeholder"), Text.literal("message"));

        ButtonWidget createButton = ButtonWidget.builder(createButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            this.children().forEach((element -> {
                if (element instanceof SecretsWidget) {
                    ((SecretsWidget) element).updateSize(0, 0, 0, 0);
                }
            }));
        }).dimensions(this.width / 2 - 209, this.height - 40, 100, 20).build();

        ButtonWidget deleteButton = ButtonWidget.builder(deleteButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            this.children().forEach((element -> {
                String values = "";
                if (element instanceof EditBoxWidget) {
                    values = ((EditBoxWidget) element).getText();
                }
                if (values.equals("")) return;
                String[] valueArray = values.split(",");
                int width = Integer.parseInt(valueArray[0]);
                int height = Integer.parseInt(valueArray[1]);
                int top = Integer.parseInt(valueArray[2]);
                int bottom = Integer.parseInt(valueArray[3]);
                if (element instanceof SecretsWidget) {
                    ((SecretsWidget) element).updateSize(width, height, top, bottom);
                }
            }));
        }).dimensions(this.width / 2 - 103, this.height - 40, 100, 20).build();

        ButtonWidget editButton = ButtonWidget.builder(editButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            this.children().forEach((element -> {
                if (element instanceof SecretsWidget) {
                    ((SecretsWidget) element).updateSize(this.width / 2, this.height / 2, 200, 100);
                }
            }));
        }).dimensions(this.width / 2 + 3, this.height - 40, 100, 20).build();

        ButtonWidget backButton = ButtonWidget.builder(backButtonText, (btn) -> {
            btn.active = false;
            close();
        }).dimensions(this.width / 2 + 109, this.height - 40, 100, 20).build();

        SecretsWidget secretsWidget = new SecretsWidget(
                this,
                this.client,
                this.width - 60,
                this.height - 60,
                300,
                100,
                50
        );
        List<SecretsWidget.SecretEntry> secrets = new ArrayList<>();
        for (String name : Authenticator.getInstance().secrets.keySet()) {
            secrets.add(new SecretsWidget.SecretEntry(
                    this.client,
                    name,
                    "issuer",
                    Authenticator.getInstance().secrets.get(name)
                    ));
        }
        secretsWidget.setSecrets(secrets);

        this.addDrawableChild(createButton);
        this.addDrawableChild(deleteButton);
        this.addDrawableChild(editButton);
        this.addDrawableChild(backButton);
        this.addDrawableChild(secretsWidget);
        this.addDrawableChild(editBoxWidget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, title, this.width / 2, 10, 0xFFFFFFFF);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, Text.literal(mouseX + " " + mouseY + " " + delta), this.width / 2, 20, 0xFFFFFFFF);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("width " + this.width + " height " + this.height), this.width / 2, 30, 0xFFFFFFFF);
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

    public void updateButtonActivationStates() {

    }
}
