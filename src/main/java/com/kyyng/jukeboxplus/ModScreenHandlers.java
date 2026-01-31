package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {

    public static final ExtendedScreenHandlerType<DiscShelfScreenHandler, BlockPos> DISC_SHELF =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(JukeboxPlus.MOD_ID, "disc_shelf"),
                    new ExtendedScreenHandlerType<>(DiscShelfScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static final ExtendedScreenHandlerType<AdvancedJukeboxScreenHandler, BlockPos> ADVANCED_JUKEBOX =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(JukeboxPlus.MOD_ID, "advanced_jukebox"),
                    new ExtendedScreenHandlerType<>(AdvancedJukeboxScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static final ExtendedScreenHandlerType<TapeDeckScreenHandler, BlockPos> TAPE_DECK =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(JukeboxPlus.MOD_ID, "tape_deck"),
                    new ExtendedScreenHandlerType<>(TapeDeckScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static final ExtendedScreenHandlerType<BlockmanScreenHandler, Boolean> BLOCKMAN =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(JukeboxPlus.MOD_ID, "blockman"),
                    new ExtendedScreenHandlerType<>(BlockmanScreenHandler::new, PacketCodecs.BOOLEAN)
            );

    public static void register() {
        // forces class loading
    }
}
