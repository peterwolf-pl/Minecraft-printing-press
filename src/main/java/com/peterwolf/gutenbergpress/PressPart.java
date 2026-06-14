package com.peterwolf.gutenbergpress;

import net.minecraft.util.StringRepresentable;

public enum PressPart implements StringRepresentable {
	RESERVED("reserved"),
	FRAME_POST("frame_post"),
	BASE_BEAM("base_beam"),
	TOP_BEAM("top_beam"),
	BED("bed"),
	DRAWER_OUT("drawer_out"),
	DRAWER_IN("drawer_in"),
	TYPE_CLEAN("type_clean"),
	TYPE_INKED("type_inked"),
	PAPER_BLANK("paper_blank"),
	PAPER_PRINTED("paper_printed"),
	PLATEN_HIGH("platen_high"),
	PLATEN_LOW("platen_low"),
	SCREW("screw"),
	SCREW_TURNED("screw_turned"),
	SCREW_HANDLE("screw_handle"),
	SCREW_HANDLE_TURNED("screw_handle_turned");

	private final String serializedName;

	PressPart(final String serializedName) {
		this.serializedName = serializedName;
	}

	@Override
	public String getSerializedName() {
		return this.serializedName;
	}
}
