package com.kyyng.jukeboxplus;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

public class AdvancedJukeboxScreenHandler extends ScreenHandler {

    private static final int SLOT_COUNT = 32;
    private static final int SLOT_COLUMNS = 8;
    private static final int SLOT_ROWS = 4;
    private static final int SLOT_SIZE = 18;
    private static final int INVENTORY_X = 17;
    private static final int INVENTORY_Y = 23;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 140;
    private static final int HOTBAR_Y = PLAYER_INV_Y + 58;

    private final Inventory inventory;
    private final BlockPos pos;

    public AdvancedJukeboxScreenHandler(int syncId, PlayerInventory playerInventory, AdvancedJukeboxBlockEntity jukebox) {
        this(syncId, playerInventory, (Inventory) jukebox, jukebox.getPos());
    }

    public AdvancedJukeboxScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, getInventory(playerInventory, pos), pos);
    }

    private AdvancedJukeboxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BlockPos pos) {
        super(ModScreenHandlers.ADVANCED_JUKEBOX, syncId);
        checkSize(inventory, SLOT_COUNT);
        this.inventory = inventory;
        this.pos = pos;
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
        if (playerInventory.player.getEntityWorld().getBlockEntity(pos) instanceof AdvancedJukeboxBlockEntity jukebox) {
            return jukebox;
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
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (actionType == SlotActionType.PICKUP
                && button == 1
                && slotIndex >= 0
                && slotIndex < SLOT_COUNT
                && getCursorStack().isEmpty()) {
            Slot slot = slots.get(slotIndex);
            if (slot != null && slot.hasStack() && slot.getStack().contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
                tryPlaySlot(player, slotIndex);
                return;
            }
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private void tryPlaySlot(PlayerEntity player, int slotIndex) {
        if (player.getEntityWorld().isClient()) {
            return;
        }
        if (player.getEntityWorld().getBlockEntity(pos) instanceof AdvancedJukeboxBlockEntity jukebox) {
            jukebox.playSlot(slotIndex);
        }
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
