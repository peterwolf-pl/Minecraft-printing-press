package com.peterwolf.gutenbergpress;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class EducationalBlockItem extends BlockItem {
	private final String tooltipKey;

	public EducationalBlockItem(final Block block, final Item.Properties properties, final String tooltipKey) {
		super(block, properties);
		this.tooltipKey = tooltipKey;
	}

	@Override
	public void appendHoverText(
		final ItemStack stack,
		final Item.TooltipContext context,
		final TooltipDisplay tooltipDisplay,
		final Consumer<Component> tooltipAdder,
		final TooltipFlag tooltipFlag
	) {
		tooltipAdder.accept(Component.translatable(this.tooltipKey).withStyle(ChatFormatting.GRAY));
	}
}
