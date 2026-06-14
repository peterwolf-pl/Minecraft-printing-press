package com.peterwolf.gutenbergpress;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PressPartBlockEntity extends BlockEntity {
	private BlockPos masterPos = BlockPos.ZERO;

	public PressPartBlockEntity(final BlockPos pos, final BlockState state) {
		super(ModBlockEntities.PRESS_PART, pos, state);
	}

	public void setMasterPos(final BlockPos masterPos) {
		this.masterPos = masterPos.immutable();
		this.setChanged();
	}

	public BlockPos masterPos() {
		return this.masterPos;
	}

	public boolean belongsTo(final BlockPos pos) {
		return this.masterPos.equals(pos);
	}

	@Override
	protected void loadAdditional(final ValueInput input) {
		this.masterPos = new BlockPos(
			input.getIntOr("MasterX", 0),
			input.getIntOr("MasterY", 0),
			input.getIntOr("MasterZ", 0)
		);
	}

	@Override
	protected void saveAdditional(final ValueOutput output) {
		output.putInt("MasterX", this.masterPos.getX());
		output.putInt("MasterY", this.masterPos.getY());
		output.putInt("MasterZ", this.masterPos.getZ());
	}
}
