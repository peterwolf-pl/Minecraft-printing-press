package com.peterwolf.gutenbergpress;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class PrintingTextData {
	public static final String DEFAULT_TEXT = "GUTENBERG PRESS";
	private static final String TEXT_KEY = "printed_text";
	private static final int MAX_TEXT_LENGTH = 48;

	private PrintingTextData() {
	}

	public static void writePrintedText(final ItemStack stack, final String text) {
		String cleanText = writeTextData(stack, text);
		stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.gutenbergpress.printed_paper_sheet.named", cleanText));
	}

	public static String writeTextData(final ItemStack stack, final String text) {
		String cleanText = sanitize(text);
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putString(TEXT_KEY, cleanText);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		return cleanText;
	}

	public static String readTextOrDefault(final ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data != null) {
			String text = sanitize(data.copyTag().getStringOr(TEXT_KEY, DEFAULT_TEXT));
			if (!text.isBlank()) {
				return text;
			}
		}

		Component customName = stack.get(DataComponents.CUSTOM_NAME);
		if (customName != null) {
			String text = sanitize(customName.getString());
			if (!text.isBlank()) {
				return text;
			}
		}
		return DEFAULT_TEXT;
	}

	public static String sanitize(final String text) {
		String cleanText = text == null ? DEFAULT_TEXT : text.strip().replaceAll("\\s+", " ");
		if (cleanText.isBlank()) {
			cleanText = DEFAULT_TEXT;
		}
		return cleanText.length() <= MAX_TEXT_LENGTH ? cleanText : cleanText.substring(0, MAX_TEXT_LENGTH);
	}
}
