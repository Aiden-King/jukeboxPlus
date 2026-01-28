package com.kyyng.jukeboxplus;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DiscShelfBlock extends BlockWithEntity {

    public static final MapCodec<DiscShelfBlock> CODEC =
            createCodec(DiscShelfBlock::new);

    public DiscShelfBlock(Settings settings) {
        super(settings);
    }

    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DiscShelfBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUseWithItem(
            ItemStack held,
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        held = player.getStackInHand(hand);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof DiscShelfBlockEntity shelf)) {
            return ActionResult.PASS;
        }

        // Client: animation only
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        // INSERT disc
        if (!held.isEmpty() && held.contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
            for (int i = 0; i < shelf.size(); i++) {
                if (shelf.getStack(i).isEmpty()) {

                    ItemStack one = held.copy();
                    one.setCount(1);

                    shelf.setStack(i, one);
                    shelf.markDirty();

                    held.decrement(1);
                    player.setStackInHand(hand, held);

                    return ActionResult.CONSUME;
                }
            }
            return ActionResult.FAIL;
        }

        // REMOVE disc (sneak + empty hand)
        if (player.isSneaking() && held.isEmpty()) {
            for (int i = shelf.size() - 1; i >= 0; i--) {
                ItemStack stored = shelf.getStack(i);
                if (!stored.isEmpty()) {
                    shelf.setStack(i, ItemStack.EMPTY);
                    shelf.markDirty();
                    player.giveItemStack(stored);
                    return ActionResult.CONSUME;
                }
            }
        }

        player.openHandledScreen(shelf);
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit
    ) {
        return onUseWithItem(player.getMainHandStack(), state, world, pos, player, Hand.MAIN_HAND, hit);
    }
}

