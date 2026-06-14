package com.peterwolf.gutenbergpress;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<GutenbergPressBlockEntity> GUTENBERG_PRESS = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		GutenbergPrintingPressMod.id("gutenberg_press"),
		FabricBlockEntityTypeBuilder.create(GutenbergPressBlockEntity::new, ModBlocks.GUTENBERG_PRESS).build()
	);
	public static final BlockEntityType<PressPartBlockEntity> PRESS_PART = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		GutenbergPrintingPressMod.id("press_part"),
		FabricBlockEntityTypeBuilder.create(PressPartBlockEntity::new, ModBlocks.PRESS_PART).build()
	);

	private ModBlockEntities() {
	}

	public static void initialize() {
	}
}
