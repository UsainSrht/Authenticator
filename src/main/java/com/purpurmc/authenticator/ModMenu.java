package com.purpurmc.authenticator;

import com.purpurmc.authenticator.screens.AuthenticatorScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AuthenticatorScreen::new;
    }
}
