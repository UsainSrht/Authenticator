package com.purpurmc.authenticator.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CreateSecretScreen extends Screen {

    private static final Text TITLE = Text.translatable("screen.authenticator.create.title");
    private static final Text saveButtonText = Text.translatable("button.authenticator.create.save");
    private static final Text cancelButtonText = Text.translatable("button.authenticator.create.cancel");
    private final Screen background;

    public CreateSecretScreen(Screen background) {
        super(TITLE);
        this.background = background;
    }

    @Override
    protected void init() {
        super.init();

        this.clearChildren();

        EditBoxWidget name = new EditBoxWidget(textRenderer,
                this.width / 2,
                this.height / 2 + 25,
                100,
                20,
                Text.literal("name"), Text.literal("name"));

        EditBoxWidget issuer = new EditBoxWidget(textRenderer,
                this.width / 2,
                this.height / 2,
                100,
                20,
                Text.literal("issuer"), Text.literal("name"));

        EditBoxWidget secret = new EditBoxWidget(textRenderer,
                this.width / 2,
                this.height / 2 - 25,
                100,
                20,
                Text.literal("secret"), Text.literal("name"));

        ButtonWidget saveButton = ButtonWidget.builder(saveButtonText, (btn) -> {
            if (this.client == null) return;
            btn.active = false;
            String[] values = { };
            int i = -1;
            for (Element element : this.children()) {
                if (element instanceof EditBoxWidget) {
                    i++;
                    values[i] = ((EditBoxWidget) element).getText();
                }
            }
            this.client.getToastManager().add(new SystemToast(
                    SystemToast.Type.NARRATOR_TOGGLE,
                    Text.literal("Save"),
                    Text.literal(String.join(" ", values))
                    ));
        }).dimensions(this.width / 2 - 103, this.height - 40, 100, 20).build();

        ButtonWidget cancelButton = ButtonWidget.builder(cancelButtonText, (btn) -> {
            btn.active = false;
            close();
        }).dimensions(this.width / 2 + 3, this.height - 40, 100, 20).build();

        this.addDrawableChild(name);
        this.addDrawableChild(issuer);
        this.addDrawableChild(secret);
        this.addDrawableChild(saveButton);
        this.addDrawableChild(cancelButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, TITLE, this.width / 2, 10, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(background);
    }


    public boolean shouldCloseOnEsc() {
        return true;
    }
}
