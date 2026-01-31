package com.kyyng.jukeboxplus;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class JukeboxPlusClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.DISC_SHELF, DiscShelfScreen::new);
		HandledScreens.register(ModScreenHandlers.ADVANCED_JUKEBOX, AdvancedJukeboxScreen::new);
		HandledScreens.register(ModScreenHandlers.TAPE_DECK, TapeDeckScreen::new);
		HandledScreens.register(ModScreenHandlers.BLOCKMAN, BlockmanScreen::new);
	}
}
