package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.server.network.ServerPlayerEntity;

public class BlockmanScreenFactory implements ExtendedScreenHandlerFactory<Boolean> {

    private final Hand hand;

    public BlockmanScreenFactory(PlayerEntity player, Hand hand) {
        this.hand = hand;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("item.jukeboxplus.blockman");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BlockmanScreenHandler(syncId, inv, new BlockmanItemInventory(player, hand), hand == Hand.MAIN_HAND);
    }

    @Override
    public Boolean getScreenOpeningData(ServerPlayerEntity player) {
        return hand == Hand.MAIN_HAND;
    }
}
