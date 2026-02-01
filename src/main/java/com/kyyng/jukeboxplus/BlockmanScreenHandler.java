package com.kyyng.jukeboxplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;

public class BlockmanScreenHandler extends ScreenHandler {

    private static final int SLOT_X = 86;
    private static final int SLOT_Y = 66;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 155;
    private static final int HOTBAR_Y = 213;

    private final Inventory inventory;
    private final boolean mainHand;

    public static final int PREVIOUS_BUTTON_ID = 0;
    public static final int NEXT_BUTTON_ID = 1;
    public static final int PLAY_BUTTON_ID = 2;

    public BlockmanScreenHandler(int syncId, PlayerInventory playerInventory, boolean mainHand) {
        this(syncId, playerInventory, getInventory(playerInventory, mainHand), mainHand);
    }

    public BlockmanScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, boolean mainHand) {
        super(ModScreenHandlers.BLOCKMAN, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        this.mainHand = mainHand;
        inventory.onOpen(playerInventory.player);

        addSlot(new CassetteSlot(inventory, 0, SLOT_X, SLOT_Y));
        addPlayerInventorySlots(playerInventory, PLAYER_INV_X, PLAYER_INV_Y);
        addPlayerHotbarSlots(playerInventory, PLAYER_INV_X, HOTBAR_Y);

        // Add the offhand slot to ensure it syncs when modified on the server
        // The slot is added even if it's not the main hand to keep slot indices consistent
        this.addSlot(new Slot(playerInventory, 40, -1000, -1000));
    }

    public boolean isMainHand() {
        return mainHand;
    }

    private static Inventory getInventory(PlayerInventory playerInventory, boolean mainHand) {
        Hand hand = mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
        return new BlockmanItemInventory(playerInventory.player, hand);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            original = stack.copy();

            // Ignore the offhand slot (the last slot added) for quick move
            int playerEnd = slots.size() - 1;

            if (index == 0) {
                if (!insertItem(stack, 1, playerEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.isOf(ModItems.CASSETTE)) {
                if (!insertItem(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return original;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        ItemStack blockman = player.getStackInHand(mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND);
        if (!blockman.isOf(ModItems.BLOCKMAN)) {
            return false;
        }

        ItemStack cassette = BlockmanData.getCassette(blockman, player.getEntityWorld().getRegistryManager());
        if (cassette.isEmpty()) {
            return false;
        }

        java.util.List<net.minecraft.util.Identifier> songs = CassetteData.getSongs(cassette);
        if (songs.isEmpty()) {
            return false;
        }

        int index = BlockmanData.getPlayIndex(blockman);
        if (id == PREVIOUS_BUTTON_ID) {
            index = (index - 1 + songs.size()) % songs.size();
            BlockmanData.setPlayIndex(blockman, index);
        } else if (id == NEXT_BUTTON_ID) {
            index = (index + 1) % songs.size();
            BlockmanData.setPlayIndex(blockman, index);
        } else if (id == PLAY_BUTTON_ID) {
            boolean playing = BlockmanData.isPlaying(blockman);
            BlockmanData.setPlaying(blockman, !playing);
        } else {
            return false;
        }

        // Sync the changes to client. Since we're changing CUSTOM_DATA on the item in hand,
        // we need to make sure the client sees this.
        if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            serverPlayer.currentScreenHandler.sendContentUpdates();
            // If it's in the offhand, we also need to sync the offhand slot explicitly
            // because standard sendContentUpdates might not include it if it's not a standard slot.
            // But we added the offhand slot to the handler, so sendContentUpdates should cover it now.
        }
        
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }

    private static class CassetteSlot extends Slot {
        CassetteSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(ModItems.CASSETTE);
        }
    }
}
