package com.kyyng.jukeboxplus;

import net.fabricmc.api.ModInitializer;

public class JukeboxPlus implements ModInitializer {

	public static final String MOD_ID = "jukeboxplus";

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();
		ModScreenHandlers.register();
	}
}

