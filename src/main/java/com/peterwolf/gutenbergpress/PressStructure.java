package com.peterwolf.gutenbergpress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PressStructure {
	private static final int HALF_WIDTH = 5;
	private static final int HALF_DEPTH = 4;
	private static final int TOP_Y = 8;

	private PressStructure() {
	}

	public static boolean canBuild(final Level level, final BlockPos masterPos, final Direction facing) {
		for (BlockPos pos : reservedPositions(masterPos, facing)) {
			if (pos.equals(masterPos)) {
				continue;
			}

			BlockState state = level.getBlockState(pos);
			if (!state.canBeReplaced() && !isOwnedPart(level, pos, masterPos)) {
				return false;
			}
		}
		return true;
	}

	public static void refresh(final ServerLevel level, final BlockPos masterPos, final Direction facing, final PressState state) {
		Map<BlockPos, PressPart> desired = partsForState(masterPos, facing, state);
		Set<BlockPos> reserved = reservedPositions(masterPos, facing);

		for (BlockPos pos : reserved) {
			if (!desired.containsKey(pos) && isOwnedPart(level, pos, masterPos)) {
				level.removeBlock(pos, false);
			}
		}

		for (Map.Entry<BlockPos, PressPart> entry : desired.entrySet()) {
			BlockPos pos = entry.getKey();
			PressPart part = entry.getValue();
			BlockState current = level.getBlockState(pos);
			BlockState desiredState = ModBlocks.PRESS_PART.defaultBlockState()
				.setValue(PressPartBlock.FACING, facing)
				.setValue(PressPartBlock.PART, part);

			if (!current.is(ModBlocks.PRESS_PART) || current.getValue(PressPartBlock.PART) != part) {
				if (!current.canBeReplaced() && !isOwnedPart(level, pos, masterPos)) {
					continue;
				}
				level.setBlock(pos, desiredState, 3);
			}

			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof PressPartBlockEntity partEntity) {
				partEntity.setMasterPos(masterPos);
			}
		}
	}

	public static void remove(final ServerLevel level, final BlockPos masterPos, final boolean includeMaster) {
		for (BlockPos pos : allCandidatePositions(level, masterPos)) {
			if (isOwnedPart(level, pos, masterPos)) {
				level.removeBlock(pos, false);
			}
		}

		if (includeMaster && level.getBlockState(masterPos).is(ModBlocks.GUTENBERG_PRESS)) {
			level.removeBlock(masterPos, false);
		}
	}

	private static Set<BlockPos> allCandidatePositions(final Level level, final BlockPos masterPos) {
		Set<BlockPos> positions = new HashSet<>();
		BlockState masterState = level.getBlockState(masterPos);
		if (masterState.is(ModBlocks.GUTENBERG_PRESS)) {
			positions.addAll(reservedPositions(masterPos, masterState.getValue(GutenbergPressBlock.FACING)));
			return positions;
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			positions.addAll(reservedPositions(masterPos, direction));
		}
		return positions;
	}

	private static boolean isOwnedPart(final Level level, final BlockPos pos, final BlockPos masterPos) {
		if (!level.getBlockState(pos).is(ModBlocks.PRESS_PART)) {
			return false;
		}
		BlockEntity blockEntity = level.getBlockEntity(pos);
		return blockEntity instanceof PressPartBlockEntity partEntity && partEntity.belongsTo(masterPos);
	}

	private static Set<BlockPos> reservedPositions(final BlockPos masterPos, final Direction facing) {
		Set<BlockPos> positions = new HashSet<>();
		for (PressState state : PressState.values()) {
			positions.addAll(partsForState(masterPos, facing, state).keySet());
		}
		return positions;
	}

	private static Map<BlockPos, PressPart> partsForState(final BlockPos masterPos, final Direction facing, final PressState state) {
		Map<BlockPos, PressPart> parts = new LinkedHashMap<>();
		addDynamic(parts, masterPos, facing, PressState.PRINT_FINISHED, true);
		addDynamic(parts, masterPos, facing, PressState.PRESSING, true);
		addDynamic(parts, masterPos, facing, PressState.PRESS_READY, true);
		addDynamic(parts, masterPos, facing, PressState.PAPER_PLACED, true);
		addFrame(parts, masterPos, facing);
		addDynamic(parts, masterPos, facing, state, false);
		parts.remove(masterPos);
		return new HashMap<>(parts);
	}

	private static void addFrame(final Map<BlockPos, PressPart> parts, final BlockPos masterPos, final Direction facing) {
		for (int x : new int[] { -HALF_WIDTH, HALF_WIDTH }) {
			for (int z : new int[] { -HALF_DEPTH, HALF_DEPTH }) {
				for (int y = 1; y <= TOP_Y; y++) {
					put(parts, masterPos, facing, x, y, z, PressPart.FRAME_POST);
				}
			}
		}

		for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
			put(parts, masterPos, facing, x, 0, -HALF_DEPTH, PressPart.BASE_BEAM);
			put(parts, masterPos, facing, x, 0, HALF_DEPTH, PressPart.BASE_BEAM);
			put(parts, masterPos, facing, x, TOP_Y, -HALF_DEPTH, PressPart.TOP_BEAM);
			put(parts, masterPos, facing, x, TOP_Y, HALF_DEPTH, PressPart.TOP_BEAM);
		}

		for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
			put(parts, masterPos, facing, -HALF_WIDTH, 0, z, PressPart.BASE_BEAM);
			put(parts, masterPos, facing, HALF_WIDTH, 0, z, PressPart.BASE_BEAM);
			put(parts, masterPos, facing, -HALF_WIDTH, TOP_Y, z, PressPart.TOP_BEAM);
			put(parts, masterPos, facing, HALF_WIDTH, TOP_Y, z, PressPart.TOP_BEAM);
		}

		for (int x = -4; x <= 4; x++) {
			for (int z = -2; z <= 2; z++) {
				put(parts, masterPos, facing, x, 1, z, PressPart.BED);
			}
		}

		for (int z = -3; z <= 3; z++) {
			put(parts, masterPos, facing, -4, 2, z, PressPart.BASE_BEAM);
			put(parts, masterPos, facing, 4, 2, z, PressPart.BASE_BEAM);
		}
	}

	private static void addDynamic(
		final Map<BlockPos, PressPart> parts,
		final BlockPos masterPos,
		final Direction facing,
		final PressState state,
		final boolean reserveOnly
	) {
		int drawerZ = state.isDrawerInserted() ? 0 : 3;
		PressPart drawerPart = state.isDrawerInserted() ? PressPart.DRAWER_IN : PressPart.DRAWER_OUT;
		addLine(parts, masterPos, facing, -3, 3, 2, drawerZ, reserveOnly ? PressPart.RESERVED : drawerPart);

		if (state.hasType()) {
			addLine(parts, masterPos, facing, -2, 2, 3, drawerZ, reserveOnly ? PressPart.RESERVED : (state.isInked() ? PressPart.TYPE_INKED : PressPart.TYPE_CLEAN));
		}

		if (state.hasPaper()) {
			PressPart paperPart = state == PressState.PRINT_FINISHED ? PressPart.PAPER_PRINTED : PressPart.PAPER_BLANK;
			addLine(parts, masterPos, facing, -2, 2, 4, drawerZ, reserveOnly ? PressPart.RESERVED : paperPart);
		}

		int platenY = state.isPlatenLowered() ? 5 : 6;
		addArea(parts, masterPos, facing, -3, 3, -1, 1, platenY, reserveOnly ? PressPart.RESERVED : (state.isPlatenLowered() ? PressPart.PLATEN_LOW : PressPart.PLATEN_HIGH));

		PressPart screwPart = state.ordinal() >= PressState.PRESS_READY.ordinal() ? PressPart.SCREW_TURNED : PressPart.SCREW;
		for (int y = 5; y <= TOP_Y; y++) {
			put(parts, masterPos, facing, 0, y, 0, reserveOnly ? PressPart.RESERVED : screwPart);
		}

		if (state.ordinal() >= PressState.PRESS_READY.ordinal()) {
			for (int z = -2; z <= 2; z++) {
				put(parts, masterPos, facing, 0, TOP_Y, z, reserveOnly ? PressPart.RESERVED : PressPart.SCREW_HANDLE_TURNED);
			}
		} else {
			for (int x = -2; x <= 2; x++) {
				put(parts, masterPos, facing, x, TOP_Y, 0, reserveOnly ? PressPart.RESERVED : PressPart.SCREW_HANDLE);
			}
		}
	}

	private static void addLine(
		final Map<BlockPos, PressPart> parts,
		final BlockPos masterPos,
		final Direction facing,
		final int minX,
		final int maxX,
		final int y,
		final int z,
		final PressPart part
	) {
		for (int x = minX; x <= maxX; x++) {
			put(parts, masterPos, facing, x, y, z, part);
		}
	}

	private static void addArea(
		final Map<BlockPos, PressPart> parts,
		final BlockPos masterPos,
		final Direction facing,
		final int minX,
		final int maxX,
		final int minZ,
		final int maxZ,
		final int y,
		final PressPart part
	) {
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				put(parts, masterPos, facing, x, y, z, part);
			}
		}
	}

	private static void put(
		final Map<BlockPos, PressPart> parts,
		final BlockPos masterPos,
		final Direction facing,
		final int x,
		final int y,
		final int z,
		final PressPart part
	) {
		Direction right = facing.getClockWise();
		BlockPos pos = masterPos.relative(right, x).relative(facing, z).above(y);
		parts.put(pos, part);
	}
}
