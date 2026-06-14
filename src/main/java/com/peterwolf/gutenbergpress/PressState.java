package com.peterwolf.gutenbergpress;

import net.minecraft.util.StringRepresentable;

public enum PressState implements StringRepresentable {
	EMPTY("empty"),
	TYPE_COMPOSITION_INSERTED("type_composition_inserted"),
	INKED("inked"),
	PAPER_PLACED("paper_placed"),
	DRAWER_INSERTED("drawer_inserted"),
	PRESS_READY("press_ready"),
	PRESSING("pressing"),
	PRINT_FINISHED("print_finished");

	private final String serializedName;

	PressState(final String serializedName) {
		this.serializedName = serializedName;
	}

	@Override
	public String getSerializedName() {
		return this.serializedName;
	}

	public boolean hasType() {
		return this != EMPTY;
	}

	public boolean isInked() {
		return this.ordinal() >= INKED.ordinal();
	}

	public boolean hasPaper() {
		return this.ordinal() >= PAPER_PLACED.ordinal();
	}

	public boolean isDrawerInserted() {
		return this.ordinal() >= DRAWER_INSERTED.ordinal();
	}

	public boolean isPlatenLowered() {
		return this == PRESSING || this == PRINT_FINISHED;
	}

	public static PressState byName(final String name) {
		for (PressState state : values()) {
			if (state.serializedName.equals(name)) {
				return state;
			}
		}
		return EMPTY;
	}
}
