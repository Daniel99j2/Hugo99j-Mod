package com.daniel99j.hugo99j;

import com.daniel99j.hugo99j.fluid.ModFluids;
import com.daniel99j.hugo99j.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hugo99jMod implements ModInitializer {
	public static final String MOD_ID = "hugo99j";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.registerItems();
		ModFluids.registerFluids();
		LOGGER.info("Hugo99j Mod loaded!");
	}
}