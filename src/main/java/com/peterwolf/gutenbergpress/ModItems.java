package com.peterwolf.gutenbergpress;

import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {
	public static final TypeCompositionItem MOVABLE_TYPE_COMPOSITION = register(
		"movable_type_composition",
		key -> new TypeCompositionItem(new Item.Properties().stacksTo(16).setId(key))
	);
	public static final TooltipItem INK_ROLLER = register(
		"ink_roller",
		key -> new TooltipItem(new Item.Properties().durability(64).setId(key), "tooltip.gutenbergpress.ink_roller")
	);
	public static final TooltipItem PRINTING_INK = register(
		"printing_ink",
		key -> new TooltipItem(new Item.Properties().stacksTo(64).setId(key), "tooltip.gutenbergpress.printing_ink")
	);
	public static final TooltipItem BLANK_PAPER_SHEET = register(
		"blank_paper_sheet",
		key -> new TooltipItem(new Item.Properties().stacksTo(64).setId(key), "tooltip.gutenbergpress.blank_paper_sheet")
	);
	public static final PrintedPaperItem PRINTED_PAPER_SHEET = register(
		"printed_paper_sheet",
		key -> new PrintedPaperItem(new Item.Properties().stacksTo(16).setId(key))
	);

	private ModItems() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
			output.accept(MOVABLE_TYPE_COMPOSITION);
			output.accept(PRINTING_INK);
			output.accept(INK_ROLLER);
			output.accept(BLANK_PAPER_SHEET);
			output.accept(PRINTED_PAPER_SHEET);
		});
	}

	private static <T extends Item> T register(final String path, final Function<ResourceKey<Item>, T> itemFactory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, GutenbergPrintingPressMod.id(path));
		return Registry.register(BuiltInRegistries.ITEM, key, itemFactory.apply(key));
	}
}
