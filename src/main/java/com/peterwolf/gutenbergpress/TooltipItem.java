package com.peterwolf.gutenbergpress;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class TooltipItem extends Item {
	private final String tooltipKey;

	public TooltipItem(final Item.Properties properties, final String tooltipKey) {
		super(properties);
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
