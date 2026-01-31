package com.kyyng.jukeboxplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class BlockmanItemInventory implements Inventory {

    private final PlayerEntity player;
    private final Hand hand;

    public BlockmanItemInventory(PlayerEntity player, Hand hand) {
        this.player = player;
        this.hand = hand;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getStack(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack blockman = player.getStackInHand(hand);
        return BlockmanData.getCassette(blockman, player.getEntityWorld().getRegistryManager());
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack current = getStack(slot);
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = current.split(amount);
        setStack(slot, current);
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack current = getStack(slot);
        setStack(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != 0) {
            return;
        }
        ItemStack blockman = player.getStackInHand(hand);
        BlockmanData.setCassette(blockman, stack, player.getEntityWorld().getRegistryManager());
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.getStackInHand(hand).isOf(ModItems.BLOCKMAN);
    }

    @Override
    public void clear() {
        setStack(0, ItemStack.EMPTY);
    }
}
