package com.peterwolf.gutenbergpress;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GutenbergPressBlockEntity extends BlockEntity {
	private static final int PRESS_TICKS = 60;
	private PressState pressState = PressState.EMPTY;
	private String compositionText = PrintingTextData.DEFAULT_TEXT;
	private int pressingTicks = 0;

	public GutenbergPressBlockEntity(final BlockPos pos, final BlockState state) {
		super(ModBlockEntities.GUTENBERG_PRESS, pos, state);
	}

	public InteractionResult use(final Player player, final InteractionHand hand) {
		if (this.level == null || this.level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		ItemStack stack = player.getItemInHand(hand);
		return switch (this.pressState) {
			case EMPTY -> this.insertType(player, stack);
			case TYPE_COMPOSITION_INSERTED -> this.applyInk(player, hand, stack);
			case INKED -> this.placePaper(player, stack);
			case PAPER_PLACED -> this.pushDrawer(player, stack);
			case DRAWER_INSERTED -> this.tightenScrew(player, stack);
			case PRESS_READY -> this.startPressing(player, stack);
			case PRESSING -> this.waitForPress(player);
			case PRINT_FINISHED -> this.removePrintedSheet(player, stack);
		};
	}

	public static void tick(final Level level, final BlockPos pos, final BlockState state, final GutenbergPressBlockEntity press) {
		if (press.pressState != PressState.PRESSING) {
			return;
		}

		press.pressingTicks++;
		if (press.pressingTicks >= PRESS_TICKS) {
			press.pressingTicks = 0;
			press.setPressState(PressState.PRINT_FINISHED);
			level.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 0.8F, 0.9F);
		} else {
			press.setChanged();
		}
	}

	private InteractionResult insertType(final Player player, final ItemStack stack) {
		if (!stack.is(ModItems.MOVABLE_TYPE_COMPOSITION)) {
			return this.explain(player, "message.gutenbergpress.need_type");
		}

		this.compositionText = PrintingTextData.readTextOrDefault(stack);
		if (!player.getAbilities().instabuild) {
			stack.consume(1, player);
		}
		this.setPressState(PressState.TYPE_COMPOSITION_INSERTED);
		this.message(player, "message.gutenbergpress.type_inserted", this.compositionText);
		this.play(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.75F, 0.8F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult applyInk(final Player player, final InteractionHand hand, final ItemStack stack) {
		if (stack.is(ModItems.PRINTING_INK)) {
			if (!player.getAbilities().instabuild) {
				stack.consume(1, player);
			}
		} else if (stack.is(ModItems.INK_ROLLER)) {
			stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
		} else {
			return this.explain(player, "message.gutenbergpress.need_ink");
		}

		this.setPressState(PressState.INKED);
		this.message(player, "message.gutenbergpress.inked");
		this.play(SoundEvents.INK_SAC_USE, 0.9F, 0.8F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult placePaper(final Player player, final ItemStack stack) {
		if (!stack.is(ModItems.BLANK_PAPER_SHEET)) {
			return this.explain(player, "message.gutenbergpress.need_paper");
		}

		if (!player.getAbilities().instabuild) {
			stack.consume(1, player);
		}
		this.setPressState(PressState.PAPER_PLACED);
		this.message(player, "message.gutenbergpress.paper_placed");
		this.play(SoundEvents.BOOK_PAGE_TURN, 0.9F, 0.85F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult pushDrawer(final Player player, final ItemStack stack) {
		if (!stack.isEmpty()) {
			return this.explain(player, "message.gutenbergpress.use_empty_hand_drawer");
		}

		this.setPressState(PressState.DRAWER_INSERTED);
		this.message(player, "message.gutenbergpress.drawer_inserted");
		this.play(SoundEvents.WOOD_PLACE, 0.8F, 0.7F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult tightenScrew(final Player player, final ItemStack stack) {
		if (!stack.isEmpty()) {
			return this.explain(player, "message.gutenbergpress.use_empty_hand_screw");
		}

		this.setPressState(PressState.PRESS_READY);
		this.message(player, "message.gutenbergpress.press_ready");
		this.play(SoundEvents.LEVER_CLICK, 0.9F, 0.55F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult startPressing(final Player player, final ItemStack stack) {
		if (!stack.isEmpty()) {
			return this.explain(player, "message.gutenbergpress.use_empty_hand_press");
		}

		this.pressingTicks = 0;
		this.setPressState(PressState.PRESSING);
		this.message(player, "message.gutenbergpress.pressing");
		this.play(SoundEvents.GRINDSTONE_USE, 1.0F, 0.6F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult waitForPress(final Player player) {
		return this.explain(player, "message.gutenbergpress.wait_for_press");
	}

	private InteractionResult removePrintedSheet(final Player player, final ItemStack stack) {
		if (!stack.isEmpty()) {
			return this.explain(player, "message.gutenbergpress.use_empty_hand_remove");
		}

		ItemStack printedSheet = new ItemStack(ModItems.PRINTED_PAPER_SHEET);
		PrintingTextData.writePrintedText(printedSheet, this.compositionText);
		player.getInventory().placeItemBackInInventory(printedSheet);
		this.compositionText = PrintingTextData.DEFAULT_TEXT;
		this.pressingTicks = 0;
		this.setPressState(PressState.EMPTY);
		this.message(player, "message.gutenbergpress.print_finished");
		this.play(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.2F);
		return InteractionResult.SUCCESS;
	}

	private InteractionResult explain(final Player player, final String translationKey) {
		this.message(player, translationKey);
		return InteractionResult.SUCCESS;
	}

	private void message(final Player player, final String translationKey, final Object... args) {
		player.sendSystemMessage(Component.translatable(translationKey, args));
	}

	private void play(final net.minecraft.sounds.SoundEvent sound, final float volume, final float pitch) {
		if (this.level != null) {
			this.level.playSound(null, this.worldPosition, sound, SoundSource.BLOCKS, volume, pitch);
		}
	}

	private void setPressState(final PressState state) {
		this.pressState = state;
		this.setChanged();
		if (!(this.level instanceof ServerLevel serverLevel)) {
			return;
		}

		BlockState blockState = this.getBlockState();
		if (blockState.is(ModBlocks.GUTENBERG_PRESS)) {
			serverLevel.setBlock(this.worldPosition, blockState.setValue(GutenbergPressBlock.STATE, state), 3);
			PressStructure.refresh(serverLevel, this.worldPosition, blockState.getValue(GutenbergPressBlock.FACING), state);
		}
	}

	@Override
	protected void loadAdditional(final ValueInput input) {
		this.pressState = PressState.byName(input.getStringOr("PressState", PressState.EMPTY.getSerializedName()));
		this.compositionText = PrintingTextData.sanitize(input.getStringOr("CompositionText", PrintingTextData.DEFAULT_TEXT));
		this.pressingTicks = input.getIntOr("PressingTicks", 0);
	}

	@Override
	protected void saveAdditional(final ValueOutput output) {
		output.putString("PressState", this.pressState.getSerializedName());
		output.putString("CompositionText", this.compositionText);
		output.putInt("PressingTicks", this.pressingTicks);
	}
}
