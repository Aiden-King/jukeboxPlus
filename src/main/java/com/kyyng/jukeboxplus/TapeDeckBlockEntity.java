package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TapeDeckBlockEntity extends BlockEntity implements Inventory, ExtendedScreenHandlerFactory<BlockPos> {

    public static final int SLOT_COUNT = 2;
    public static final int DISC_SLOT = 0;
    public static final int TAPE_SLOT = 1;
    private static final int MAX_SONGS = 5;

    private final DefaultedList<ItemStack> items =
            DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);

    public TapeDeckBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TAPE_DECK, pos, state);
    }

    public boolean writeCassette() {
        if (getWorld() == null) {
            return false;
        }
        ItemStack tape = items.get(TAPE_SLOT);
        if (tape.isEmpty()) {
            return false;
        }
        if (!tape.isOf(ModItems.EMPTY_TAPE) && !tape.isOf(ModItems.CASSETTE)) {
            return false;
        }

        ItemStack disc = items.get(DISC_SLOT);
        if (!disc.contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
            return false;
        }
        Optional<RegistryEntry<net.minecraft.block.jukebox.JukeboxSong>> songEntry =
                net.minecraft.block.jukebox.JukeboxSong.getSongEntryFromStack(
                        getWorld().getRegistryManager(), disc);
        if (songEntry.isEmpty()) {
            return false;
        }
        Identifier songId = songEntry.get().getKey()
                .map(key -> key.getValue())
                .orElse(null);
        if (songId == null) {
            return false;
        }

        if (tape.isOf(ModItems.EMPTY_TAPE)) {
            ItemStack cassette = new ItemStack(ModItems.CASSETTE);
            List<Identifier> songs = new ArrayList<>();
            songs.add(songId);
            CassetteData.setSongs(cassette, songs);
            items.set(TAPE_SLOT, cassette);
            markDirty();
            return true;
        }

        List<Identifier> songs = new ArrayList<>(CassetteData.getSongs(tape));
        if (songs.size() >= MAX_SONGS) {
            return false;
        }
        songs.add(songId);
        CassetteData.setSongs(tape, songs);
        markDirty();
        return true;
    }

    @Override
    protected void readData(ReadView readView) {
        super.readData(readView);
        Inventories.readData(readView, items);
    }

    @Override
    protected void writeData(WriteView writeView) {
        super.writeData(writeView);
        Inventories.writeData(writeView, items);
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
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(items, slot);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.jukeboxplus.tape_deck");
    }

    @Override
    public TapeDeckScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TapeDeckScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }
}
