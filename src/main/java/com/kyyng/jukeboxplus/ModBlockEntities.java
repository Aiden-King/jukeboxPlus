package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<DiscShelfBlockEntity> DISC_SHELF =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(JukeboxPlus.MOD_ID, "disc_shelf"),
                    FabricBlockEntityTypeBuilder
                            .create(DiscShelfBlockEntity::new, ModBlocks.DISC_SHELF)
                            .build()
            );

    public static final BlockEntityType<AdvancedJukeboxBlockEntity> ADVANCED_JUKEBOX =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(JukeboxPlus.MOD_ID, "advanced_jukebox"),
                    FabricBlockEntityTypeBuilder
                            .create(AdvancedJukeboxBlockEntity::new, ModBlocks.ADVANCED_JUKEBOX)
                            .build()
            );

    public static final BlockEntityType<TapeDeckBlockEntity> TAPE_DECK =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(JukeboxPlus.MOD_ID, "tape_deck"),
                    FabricBlockEntityTypeBuilder
                            .create(TapeDeckBlockEntity::new, ModBlocks.TAPE_DECK)
                            .build()
            );

    public static void register() {
        // forces class loading
    }
}
