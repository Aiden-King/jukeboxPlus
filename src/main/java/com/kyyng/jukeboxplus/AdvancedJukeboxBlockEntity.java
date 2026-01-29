package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class AdvancedJukeboxBlockEntity extends BlockEntity implements Inventory, ExtendedScreenHandlerFactory<BlockPos> {

    private static final int SLOT_COUNT = 36;

    private final DefaultedList<ItemStack> items =
            DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);

    private final JukeboxManager manager =
            new JukeboxManager(this::onManagerChange, pos);

    private int playingSlot = -1;

    public AdvancedJukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_JUKEBOX, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AdvancedJukeboxBlockEntity be) {
        if (!world.isClient()) {
            be.manager.tick(world, state);
        }
    }

    private void onManagerChange() {
        markDirty();
        updateHasRecord(manager.isPlaying());
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public boolean playSlot(int slot) {
        if (world == null || slot < 0 || slot >= size()) {
            return false;
        }

        ItemStack stack = items.get(slot);
        if (stack.isEmpty() || !stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
            return false;
        }

        Optional<RegistryEntry<JukeboxSong>> songEntry =
                JukeboxSong.getSongEntryFromStack(world.getRegistryManager(), stack);
        if (songEntry.isEmpty()) {
            return false;
        }

        manager.startPlaying(world, songEntry.get());
        playingSlot = slot;
        markDirty();
        updateHasRecord(true);
        return true;
    }

    public void stopPlaying() {
        if (world != null && manager.isPlaying()) {
            manager.stopPlaying(world, getCachedState());
        }
        playingSlot = -1;
        markDirty();
        updateHasRecord(false);
    }

    @Override
    public void markRemoved() {
        stopPlaying();
        super.markRemoved();
    }

    @Override
    protected void readData(ReadView readView) {
        super.readData(readView);
        Inventories.readData(readView, items);
        playingSlot = readView.getInt("PlayingSlot", -1);
    }

    @Override
    protected void writeData(WriteView writeView) {
        super.writeData(writeView);
        Inventories.writeData(writeView, items);
        writeView.putInt("PlayingSlot", playingSlot);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(items, slot, amount);
        if (!result.isEmpty()) {
            if (playingSlot == slot && items.get(slot).isEmpty()) {
                stopPlaying();
            }
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(items, slot);
        if (!result.isEmpty()) {
            if (playingSlot == slot) {
                stopPlaying();
            }
            markDirty();
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (playingSlot == slot && stack.isEmpty()) {
            stopPlaying();
        }
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private void updateHasRecord(boolean hasRecord) {
        if (world == null || world.isClient()) {
            return;
        }
        if (!world.getBlockState(pos).isOf(ModBlocks.ADVANCED_JUKEBOX)) {
            return;
        }
        BlockState state = getCachedState();
        if (state.contains(AdvancedJukeboxBlock.HAS_RECORD) && state.get(AdvancedJukeboxBlock.HAS_RECORD) != hasRecord) {
            world.setBlockState(pos, state.with(AdvancedJukeboxBlock.HAS_RECORD, hasRecord), 3);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Advanced Jukebox");
    }

    @Override
    public AdvancedJukeboxScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AdvancedJukeboxScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }
}
