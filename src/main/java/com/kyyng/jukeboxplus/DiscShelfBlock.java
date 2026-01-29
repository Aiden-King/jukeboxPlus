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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.block.ShapeContext;
import net.minecraft.world.World;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Block;
import java.util.EnumMap;

public class DiscShelfBlock extends BlockWithEntity {

    public static final MapCodec<DiscShelfBlock> CODEC =
            createCodec(DiscShelfBlock::new);
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private static final EnumMap<Direction, VoxelShape> SHAPES = buildShapes();

    public DiscShelfBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
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
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state) {
        return SHAPES.get(state.get(FACING));
    }

    private static EnumMap<Direction, VoxelShape> buildShapes() {
        EnumMap<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        shapes.put(Direction.NORTH, buildShape(Direction.NORTH));
        shapes.put(Direction.EAST, buildShape(Direction.EAST));
        shapes.put(Direction.SOUTH, buildShape(Direction.SOUTH));
        shapes.put(Direction.WEST, buildShape(Direction.WEST));
        return shapes;
    }

    private static VoxelShape buildShape(Direction dir) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, rotateCuboid(dir, 0, 2, 7, 16, 3, 16));   // Bottom
        shape = VoxelShapes.union(shape, rotateCuboid(dir, 0, 15, 7, 16, 16, 16)); // Top
        shape = VoxelShapes.union(shape, rotateCuboid(dir, 0, 3, 7, 1, 15, 16));   // Left side
        shape = VoxelShapes.union(shape, rotateCuboid(dir, 15, 3, 7, 16, 15, 16)); // Right side
        shape = VoxelShapes.union(shape, rotateCuboid(dir, 1, 3, 10, 15, 15, 16)); // Back panel
        shape = VoxelShapes.union(shape, rotateCuboid(dir, 1, 8, 9, 15, 10, 10));  // Middle bar
        return shape;
    }

    private static VoxelShape rotateCuboid(Direction dir, double x1, double y1, double z1, double x2, double y2, double z2) {
        // Rotate around block center (y axis) from the NORTH orientation.
        double nx1;
        double nz1;
        double nx2;
        double nz2;
        switch (dir) {
            case EAST -> {
                nx1 = 16 - z2;
                nz1 = x1;
                nx2 = 16 - z1;
                nz2 = x2;
            }
            case SOUTH -> {
                nx1 = 16 - x2;
                nz1 = 16 - z2;
                nx2 = 16 - x1;
                nz2 = 16 - z1;
            }
            case WEST -> {
                nx1 = z1;
                nz1 = 16 - x2;
                nx2 = z2;
                nz2 = 16 - x1;
            }
            default -> {
                nx1 = x1;
                nz1 = z1;
                nx2 = x2;
                nz2 = z2;
            }
        }
        return createCuboidShape(nx1, y1, nz1, nx2, y2, nz2);
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

