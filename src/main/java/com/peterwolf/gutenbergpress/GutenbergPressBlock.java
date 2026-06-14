package com.peterwolf.gutenbergpress;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class GutenbergPressBlock extends Block implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<PressState> STATE = EnumProperty.create("state", PressState.class);

	public GutenbergPressBlock(final BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(STATE, PressState.EMPTY));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(final BlockPlaceContext context) {
		Direction facing = context.getHorizontalDirection().getOpposite();
		if (!PressStructure.canBuild(context.getLevel(), context.getClickedPos(), facing)) {
			Player player = context.getPlayer();
			if (player != null && !context.getLevel().isClientSide()) {
				player.sendSystemMessage(Component.translatable("message.gutenbergpress.structure_blocked"));
			}
			return null;
		}
		return this.defaultBlockState().setValue(FACING, facing).setValue(STATE, PressState.EMPTY);
	}

	@Override
	public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, @Nullable final LivingEntity by, final ItemStack stack) {
		if (level instanceof ServerLevel serverLevel) {
			PressStructure.refresh(serverLevel, pos, state.getValue(FACING), PressState.EMPTY);
		}
	}

	@Override
	public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
		if (level instanceof ServerLevel serverLevel) {
			PressStructure.remove(serverLevel, pos, false);
		}
		return super.playerWillDestroy(level, pos, state, player);
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
		return this.usePress(level, pos, player, hand);
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
		return this.usePress(level, pos, player, InteractionHand.MAIN_HAND);
	}

	private InteractionResult usePress(final Level level, final BlockPos pos, final Player player, final InteractionHand hand) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof GutenbergPressBlockEntity press) {
			return press.use(player, hand);
		}
		return InteractionResult.PASS;
	}

	@Override
	protected RenderShape getRenderShape(final BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return new GutenbergPressBlockEntity(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
		if (level.isClientSide() || type != ModBlockEntities.GUTENBERG_PRESS) {
			return null;
		}
		return (tickLevel, tickPos, tickState, blockEntity) ->
			GutenbergPressBlockEntity.tick(tickLevel, tickPos, tickState, (GutenbergPressBlockEntity)blockEntity);
	}

	@Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, STATE);
	}
}
