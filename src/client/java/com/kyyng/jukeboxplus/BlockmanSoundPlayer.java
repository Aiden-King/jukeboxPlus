package com.kyyng.jukeboxplus;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

import java.util.List;

public final class BlockmanSoundPlayer {

    private static SoundInstance currentSound;
    private static ItemStack currentBlockman;

    private BlockmanSoundPlayer() {
    }

    private static class BlockmanSoundInstance extends MovingSoundInstance {
        private final PlayerEntity player;
        private final ItemStack blockman;

        protected BlockmanSoundInstance(SoundEvent sound, PlayerEntity player, ItemStack blockman) {
            super(sound, SoundCategory.RECORDS, SoundInstance.createRandom());
            this.player = player;
            this.blockman = blockman;
            this.repeat = false;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.pitch = 1.0f;
        }

        public void stop() {
            this.setDone();
        }

        @Override
        public void tick() {
            if (this.player.isRemoved() || !this.player.getInventory().contains(this.blockman)) {
                // If it's not in the inventory, check if it's because it was swapped with a stack that has different NBT
                boolean found = false;
                for (int i = 0; i < this.player.getInventory().size(); i++) {
                    ItemStack stack = this.player.getInventory().getStack(i);
                    if (ItemStack.areItemsEqual(stack, this.blockman)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    this.setDone();
                    if (currentBlockman == this.blockman) {
                        currentBlockman = null;
                        currentSound = null;
                    }
                    return;
                }
            }
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
        }
    }

    public static void playFromBlockman(PlayerEntity player, ItemStack blockman) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return;
        }

        ItemStack cassette = BlockmanData.getCassette(blockman, client.world.getRegistryManager());
        List<Identifier> songs = CassetteData.getSongs(cassette);
        if (songs.isEmpty()) {
            return;
        }

        int index = BlockmanData.getPlayIndex(blockman);
        if (index < 0 || index >= songs.size()) {
            index = 0;
        }

        Identifier songId = songs.get(index);
        Registry<JukeboxSong> registry = client.world.getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG);
        JukeboxSong song = registry.get(songId);
        if (song == null) {
            return;
        }

        SoundManager soundManager = client.getSoundManager();
        if (currentSound != null) {
            soundManager.stop(currentSound);
            currentSound = null;
        }

        currentBlockman = blockman;
        currentSound = new BlockmanSoundInstance(song.soundEvent().value(), player, blockman);
        soundManager.play(currentSound);
        client.inGameHud.setRecordPlayingOverlay(song.description());
    }

    public static void stop() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        SoundManager soundManager = client.getSoundManager();
        if (currentSound != null) {
            soundManager.stop(currentSound);
            if (currentSound instanceof BlockmanSoundInstance instance) {
                instance.stop();
            }
            currentSound = null;
        }
        currentBlockman = null;
    }

    public static boolean isPlaying(ItemStack blockman) {
        return currentSound != null && currentBlockman != null && ItemStack.areItemsEqual(currentBlockman, blockman);
    }
}
