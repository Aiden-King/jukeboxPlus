package com.kyyng.jukeboxplus;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class DiscShelfScreenHandler extends ScreenHandler {

    private static final int SLOT_COUNT = 16;
    private static final int SLOT_COLUMNS = 4;
    private static final int SLOT_ROWS = 4;
    private static final int SLOT_SIZE = 18;
    private static final int INVENTORY_X = 52;
    private static final int INVENTORY_Y = 18;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 102;
    private static final int HOTBAR_Y = PLAYER_INV_Y + 58;

    private final Inventory inventory;

    public DiscShelfScreenHandler(int syncId, PlayerInventory playerInventory, DiscShelfBlockEntity shelf) {
        this(syncId, playerInventory, (Inventory) shelf);
    }

    public DiscShelfScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, getInventory(playerInventory, pos));
    }

    private DiscShelfScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.DISC_SHELF, syncId);
        checkSize(inventory, SLOT_COUNT);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        for (int row = 0; row < SLOT_ROWS; row++) {
            for (int col = 0; col < SLOT_COLUMNS; col++) {
                int index = col + row * SLOT_COLUMNS;
                int x = INVENTORY_X + col * SLOT_SIZE;
                int y = INVENTORY_Y + row * SLOT_SIZE;
                addSlot(new DiscSlot(inventory, index, x, y));
            }
        }

        addPlayerInventorySlots(playerInventory, PLAYER_INV_X, PLAYER_INV_Y);
        addPlayerHotbarSlots(playerInventory, PLAYER_INV_X, HOTBAR_Y);
    }

    private static Inventory getInventory(PlayerInventory playerInventory, BlockPos pos) {
        if (playerInventory.player.getEntityWorld().getBlockEntity(pos) instanceof DiscShelfBlockEntity shelf) {
            return shelf;
        }
        return new SimpleInventory(SLOT_COUNT);
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

            int containerEnd = SLOT_COUNT;
            int playerStart = SLOT_COUNT;
            int playerEnd = slots.size();

            if (index < containerEnd) {
                if (!insertItem(stack, playerStart, playerEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
                if (!insertItem(stack, 0, containerEnd, false)) {
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
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }

    private static class DiscSlot extends Slot {
        DiscSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE);
        }
    }
}
