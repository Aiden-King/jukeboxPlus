package com.kyyng.jukeboxplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class BlockmanItem extends Item {

    public BlockmanItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            if (!world.isClient()) {
                openBlockmanScreen((ServerPlayerEntity) player, hand);
            }
            return ActionResult.SUCCESS;
        }

        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }

        ItemStack cassette = BlockmanData.getCassette(stack, world.getRegistryManager());
        if (cassette.isEmpty()) {
            return ActionResult.PASS;
        }

        List<Identifier> songs = CassetteData.getSongs(cassette);
        if (songs.isEmpty()) {
            return ActionResult.PASS;
        }

        boolean playing = BlockmanData.isPlaying(stack);
        if (playing) {
            if (world.isClient()) {
                stopClientSound();
                return ActionResult.SUCCESS;
            }
            BlockmanData.setPlaying(stack, false);
            return ActionResult.SUCCESS;
        }

        int index = BlockmanData.getPlayIndex(stack);
        if (index < 0 || index >= songs.size()) {
            index = 0;
        }

        if (world.isClient()) {
            playClientSound(player, stack);
            return ActionResult.SUCCESS;
        }

        BlockmanData.setPlayIndex(stack, (index + 1) % songs.size());
        BlockmanData.setPlaying(stack, true);
        return ActionResult.SUCCESS;
    }

    private static void playClientSound(PlayerEntity player, ItemStack stack) {
        try {
            Class<?> cls = Class.forName("com.kyyng.jukeboxplus.BlockmanSoundPlayer");
            cls.getMethod("playFromBlockman", PlayerEntity.class, ItemStack.class)
                    .invoke(null, player, stack);
        } catch (Exception ignored) {
        }
    }

    private static void stopClientSound() {
        try {
            Class<?> cls = Class.forName("com.kyyng.jukeboxplus.BlockmanSoundPlayer");
            cls.getMethod("stop").invoke(null);
        } catch (Exception ignored) {
        }
    }
    private void openBlockmanScreen(ServerPlayerEntity player, Hand hand) {
        player.openHandledScreen(new BlockmanScreenFactory(player, hand));
    }
}
