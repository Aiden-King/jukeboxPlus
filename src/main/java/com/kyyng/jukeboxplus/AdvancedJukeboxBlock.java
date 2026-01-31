package com.kyyng.jukeboxplus;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.util.ItemScatterer;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;

public class AdvancedJukeboxBlock extends BlockWithEntity {

    public static final Property<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    public static final MapCodec<AdvancedJukeboxBlock> CODEC =
            createCodec(AdvancedJukeboxBlock::new);

    public AdvancedJukeboxBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HAS_RECORD, false));
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
        return new AdvancedJukeboxBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite().rotateYCounterclockwise();
        return getDefaultState().with(FACING, facing);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_RECORD);
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
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof AdvancedJukeboxBlockEntity jukebox) {
            if (player.isSneaking()) {
                jukebox.stopPlaying();
                return ActionResult.CONSUME;
            }
            player.openHandledScreen(jukebox);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
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

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (world.getBlockState(pos).isOf(this)) {
            super.onStateReplaced(state, world, pos, moved);
            return;
        }
        if (world.getBlockEntity(pos) instanceof AdvancedJukeboxBlockEntity jukebox) {
            ItemScatterer.spawn(world, pos, jukebox);
            jukebox.stopPlaying();
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (!state.get(HAS_RECORD)) {
            return null;
        }
        return world.isClient()
                ? null
                : validateTicker(type, ModBlockEntities.ADVANCED_JUKEBOX, AdvancedJukeboxBlockEntity::tick);
    }
}
