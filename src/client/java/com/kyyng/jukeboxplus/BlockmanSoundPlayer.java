package com.kyyng.jukeboxplus;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class BlockmanSoundPlayer {

    private static SoundInstance currentSound;

    private BlockmanSoundPlayer() {
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

        currentSound = new EntityTrackingSoundInstance(
                song.soundEvent().value(),
                SoundCategory.RECORDS,
                1.0f,
                1.0f,
                player,
                player.getRandom().nextLong()
        );
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
            currentSound = null;
        }
    }
}
