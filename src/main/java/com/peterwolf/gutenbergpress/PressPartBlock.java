package com.peterwolf.gutenbergpress;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class PressPartBlock extends Block implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<PressPart> PART = EnumProperty.create("part", PressPart.class);

	public PressPartBlock(final BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(PART, PressPart.RESERVED));
	}

	@Override
	protected InteractionResult useItemOn(
		final ItemStack stack,
		final BlockState state,
		final Level level,
		final BlockPos pos,
		final Player player,
		final InteractionHand hand,
		final BlockHitResult hitResult
	) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		return this.routeToMaster(level, pos, player, hand);
	}

	@Override
	protected InteractionResult useWithoutItem(
		final BlockState state,
		final Level level,
		final BlockPos pos,
		final Player player,
		final BlockHitResult hitResult
	) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		return this.routeToMaster(level, pos, player, InteractionHand.MAIN_HAND);
	}

	@Override
	public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
		if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof PressPartBlockEntity partEntity) {
			BlockPos masterPos = partEntity.masterPos();
			if (serverLevel.getBlockState(masterPos).is(ModBlocks.GUTENBERG_PRESS)) {
				serverLevel.destroyBlock(masterPos, true, player);
				PressStructure.remove(serverLevel, masterPos, false);
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	private InteractionResult routeToMaster(final Level level, final BlockPos pos, final Player player, final InteractionHand hand) {
		if (!(level.getBlockEntity(pos) instanceof PressPartBlockEntity partEntity)) {
			return InteractionResult.PASS;
		}

		BlockEntity master = level.getBlockEntity(partEntity.masterPos());
		if (master instanceof GutenbergPressBlockEntity press) {
			return press.use(player, hand);
		}

		player.sendSystemMessage(Component.translatable("message.gutenbergpress.master_missing"));
		return InteractionResult.SUCCESS;
	}

	@Override
	protected RenderShape getRenderShape(final BlockState state) {
		return state.getValue(PART) == PressPart.RESERVED ? RenderShape.INVISIBLE : RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return new PressPartBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART);
	}
}
