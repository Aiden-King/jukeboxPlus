package com.kyyng.jukeboxplus;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class TapeDeckScreenHandler extends ScreenHandler {

    private static final int DISC_X = 47;
    private static final int DISC_Y = 41;
    private static final int TAPE_X = 117;
    private static final int TAPE_Y = 41;
    private static final int PLAYER_INV_X = 9;
    private static final int PLAYER_INV_Y = 84;
    private static final int HOTBAR_Y = PLAYER_INV_Y + 58;

    private final Inventory inventory;
    private final BlockPos pos;
    private final PropertyDelegate propertyDelegate;

    public TapeDeckScreenHandler(int syncId, PlayerInventory playerInventory, TapeDeckBlockEntity deck) {
        this(syncId, playerInventory, (Inventory) deck, deck.getPos(), deck.propertyDelegate);
    }

    public TapeDeckScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, getInventory(playerInventory, pos), pos, new ArrayPropertyDelegate(3));
    }

    private TapeDeckScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BlockPos pos, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.TAPE_DECK, syncId);
        checkSize(inventory, TapeDeckBlockEntity.SLOT_COUNT);
        this.inventory = inventory;
        this.pos = pos;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);
        addProperties(propertyDelegate);

        addSlot(new DiscSlot(inventory, TapeDeckBlockEntity.DISC_SLOT, DISC_X, DISC_Y));
        addSlot(new TapeSlot(inventory, TapeDeckBlockEntity.TAPE_SLOT, TAPE_X, TAPE_Y));

        addPlayerInventorySlots(playerInventory, PLAYER_INV_X, PLAYER_INV_Y);
        addPlayerHotbarSlots(playerInventory, PLAYER_INV_X, HOTBAR_Y);
    }

    private static Inventory getInventory(PlayerInventory playerInventory, BlockPos pos) {
        if (playerInventory.player.getEntityWorld().getBlockEntity(pos) instanceof TapeDeckBlockEntity deck) {
            return deck;
        }
        return new SimpleInventory(TapeDeckBlockEntity.SLOT_COUNT);
    }

    public int getWriteTime() {
        return propertyDelegate.get(2);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == 0 && !player.getEntityWorld().isClient()) {
            if (player.getEntityWorld().getBlockEntity(pos) instanceof TapeDeckBlockEntity deck) {
                return deck.writeCassette();
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            original = stack.copy();

            int containerEnd = TapeDeckBlockEntity.SLOT_COUNT;
            int playerStart = TapeDeckBlockEntity.SLOT_COUNT;
            int playerEnd = slots.size();

            if (index < containerEnd) {
                if (!insertItem(stack, playerStart, playerEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
                if (!insertItem(stack, TapeDeckBlockEntity.DISC_SLOT, TapeDeckBlockEntity.DISC_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.isOf(ModItems.EMPTY_TAPE) || stack.isOf(ModItems.CASSETTE)) {
                if (!insertItem(stack, TapeDeckBlockEntity.TAPE_SLOT, TapeDeckBlockEntity.TAPE_SLOT + 1, false)) {
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

    private static class TapeSlot extends Slot {
        TapeSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(ModItems.EMPTY_TAPE) || stack.isOf(ModItems.CASSETTE);
        }
    }

    public int getWriteProgress() {
        return propertyDelegate.get(0);
    }

    public boolean isWriting() {
        return propertyDelegate.get(1) != 0;
    }

}
