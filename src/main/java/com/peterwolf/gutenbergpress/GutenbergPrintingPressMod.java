package com.peterwolf.gutenbergpress;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GutenbergPrintingPressMod implements ModInitializer {
	public static final String MOD_ID = "gutenbergpress";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModItems.initialize();
		ModBlockEntities.initialize();
		LOGGER.info("Gutenberg Printing Press initialized");
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
