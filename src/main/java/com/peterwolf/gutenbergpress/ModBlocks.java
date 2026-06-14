package com.peterwolf.gutenbergpress;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
	public static final GutenbergPressBlock GUTENBERG_PRESS = registerBlock(
		"gutenberg_press",
		key -> new GutenbergPressBlock(baseWoodProperties(key).noOcclusion().dynamicShape().strength(2.5F, 6.0F)),
		(block, properties) -> new EducationalBlockItem(block, properties, "tooltip.gutenbergpress.gutenberg_press")
	);
	public static final Block PRINTING_DRAWER = registerBlock(
		"printing_drawer",
		key -> new Block(baseWoodProperties(key).strength(1.5F, 3.0F)),
		(block, properties) -> new EducationalBlockItem(block, properties, "tooltip.gutenbergpress.printing_drawer")
	);
	public static final Block PRESS_SCREW = registerBlock(
		"press_screw",
		key -> new Block(baseWoodProperties(key).strength(2.0F, 4.0F)),
		(block, properties) -> new EducationalBlockItem(block, properties, "tooltip.gutenbergpress.press_screw")
	);
	public static final Block WOODEN_FRAME_PART = registerBlock(
		"wooden_frame_part",
		key -> new Block(baseWoodProperties(key).strength(2.0F, 4.0F)),
		(block, properties) -> new EducationalBlockItem(block, properties, "tooltip.gutenbergpress.wooden_frame_part")
	);
	public static final PressPartBlock PRESS_PART = registerInternalBlock(
		"press_part",
		key -> new PressPartBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
			.setId(key)
			.sound(SoundType.WOOD)
			.noCollision()
			.noOcclusion()
			.noLootTable()
			.dynamicShape()
			.strength(1.0F, 3.0F))
	);

	private ModBlocks() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
			output.accept(GUTENBERG_PRESS);
			output.accept(PRINTING_DRAWER);
			output.accept(PRESS_SCREW);
			output.accept(WOODEN_FRAME_PART);
		});
	}

	private static BlockBehaviour.Properties baseWoodProperties(final ResourceKey<Block> key) {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
			.setId(key)
			.sound(SoundType.WOOD);
	}

	private static <T extends Block> T registerBlock(
		final String path,
		final Function<ResourceKey<Block>, T> blockFactory,
		final BiFunction<Block, Item.Properties, BlockItem> itemFactory
	) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, GutenbergPrintingPressMod.id(path));
		T block = Registry.register(BuiltInRegistries.BLOCK, blockKey, blockFactory.apply(blockKey));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, GutenbergPrintingPressMod.id(path));
		BlockItem item = itemFactory.apply(block, new Item.Properties().setId(itemKey));
		item.registerBlocks(Item.BY_BLOCK, item);
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		return block;
	}

	private static <T extends Block> T registerInternalBlock(final String path, final Function<ResourceKey<Block>, T> blockFactory) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, GutenbergPrintingPressMod.id(path));
		return Registry.register(BuiltInRegistries.BLOCK, blockKey, blockFactory.apply(blockKey));
	}
}
