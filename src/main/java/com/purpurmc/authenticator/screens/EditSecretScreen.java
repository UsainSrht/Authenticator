package com.purpurmc.authenticator.screens;

import com.purpurmc.authenticator.Authenticator;
import com.purpurmc.authenticator.Secret;
import com.purpurmc.authenticator.commands.AuthenticatorCommand;
import joptsimple.internal.Strings;
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

import java.util.Timer;
import java.util.TimerTask;

public class EditSecretScreen extends Screen {

    private static final Text TITLE = Text.translatable("screen.authenticator.edit.title");
    private static final Text saveButtonText = Text.translatable("button.authenticator.edit.save");
    private static final Text cancelButtonText = Text.translatable("button.authenticator.edit.cancel");
    private final Screen background;
    private final Secret secret;

    public EditSecretScreen(Screen background, Secret secret) {
        super(TITLE);
        this.background = background;
        this.secret = secret;
    }

    @Override
    protected void init() {
        super.init();

        this.clearChildren();

        EditBoxWidget name = new EditBoxWidget(textRenderer,
                this.width / 2 - 100,
                this.height / 2 + 25,
                200,
                20,
                Text.literal("name"), Text.literal("name")
        );
        name.setText(this.secret.name);

        EditBoxWidget issuer = new EditBoxWidget(textRenderer,
                this.width / 2 - 100,
                this.height / 2,
                200,
                20,
                Text.literal("issuer"), Text.literal("issuer"));
        issuer.setText(this.secret.issuer);

        EditBoxWidget secret = new EditBoxWidget(textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 25,
                200,
                20,
                Text.literal("secret"), Text.literal("secret"));
        secret.setText(this.secret.secret);

        ButtonWidget saveButton = ButtonWidget.builder(saveButtonText, this::save).dimensions(this.width / 2 - 103, this.height - 40, 100, 20).build();

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

    public void save(ButtonWidget btn) {
        if (this.client == null) return;
        btn.active = false;
        String[] values = new String[3];
        int i = -1;
        for (Element element : this.children()) {
            if (element instanceof EditBoxWidget) {
                i++;
                values[i] = ((EditBoxWidget) element).getText();
            }
        }
        String name = values[0];
        String issuer = values[1];
        String secret = values[2];

        if (Strings.isNullOrEmpty(name)) {
            //todo highlight editbox
            this.client.getToastManager().add(new SystemToast(
                    SystemToast.Type.NARRATOR_TOGGLE,
                    Text.literal("Failed!"),
                    Text.literal("Name can't be left empty.")
            ));
            activateButtonDelayed(btn, 1000, true);
            return;
        }

        if (Strings.isNullOrEmpty(secret)) {
            this.client.getToastManager().add(new SystemToast(
                    SystemToast.Type.NARRATOR_TOGGLE,
                    Text.literal("Failed!"),
                    Text.literal("Secret can't be left empty.")
            ));
            activateButtonDelayed(btn, 1000, true);
            return;
        }

        Secret newSecret = new Secret(name, issuer, secret);

        Authenticator.getInstance().replaceSecret(this.secret, newSecret);
        ((AuthenticatorScreen)background).secretsWidget.setSecrets(Authenticator.getInstance().secrets);

        this.client.getToastManager().add(new SystemToast(
                SystemToast.Type.NARRATOR_TOGGLE,
                Text.literal("Saved"),
                Text.literal(name + " " + issuer)
        ));

        close();
    }

    public void activateButtonDelayed(ButtonWidget btn, long delay, boolean bool) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        btn.active = bool;
                    }
                },
                delay
        );
    }
}
