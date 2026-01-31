package com.kyyng.jukeboxplus;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TapeDeckBlock extends BlockWithEntity {

    public static final Property<Direction> FACING = Properties.HORIZONTAL_FACING;

    public static final MapCodec<TapeDeckBlock> CODEC =
            createCodec(TapeDeckBlock::new);

    public TapeDeckBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TapeDeckBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit
    ) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TapeDeckBlockEntity deck) {
            player.openHandledScreen(deck);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (world.getBlockEntity(pos) instanceof TapeDeckBlockEntity deck) {
            ItemScatterer.spawn(world, pos, deck);
        }
        super.onStateReplaced(state, world, pos, moved);
    }
}
