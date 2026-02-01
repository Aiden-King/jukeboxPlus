package com.kyyng.jukeboxplus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.item.tooltip.TooltipType;
import java.util.List;

public class BlockmanItem extends Item {

    public BlockmanItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, net.minecraft.component.type.TooltipDisplayComponent displayComponent, java.util.function.Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("tooltip.jukeboxplus.blockman").formatted(Formatting.GRAY));
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && BlockmanData.isPlaying(stack)) {
            if (!(entity instanceof PlayerEntity player) || !player.getInventory().contains(stack)) {
                BlockmanData.setPlaying(stack, false);
            }
        }
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

        ItemStack cassette = BlockmanData.getCassette(stack, world.getRegistryManager());
        if (cassette.isEmpty()) {
            return ActionResult.PASS;
        }

        List<Identifier> songs = CassetteData.getSongs(cassette);
        if (songs.isEmpty()) {
            return ActionResult.PASS;
        }

        boolean playing = BlockmanData.isPlaying(stack);
        int index = BlockmanData.getPlayIndex(stack);
        if (index < 0 || index >= songs.size()) {
            index = 0;
        }

        if (world.isClient()) {
            if (playing) {
                stopClientSound(stack);
            } else {
                playClientSound(player, stack);
            }
            return ActionResult.SUCCESS;
        }

        if (!playing) {
            BlockmanData.setPlayIndex(stack, (index + 1) % songs.size());
        }
        BlockmanData.setPlaying(stack, !playing);
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

    private static void stopClientSound(ItemStack stack) {
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
