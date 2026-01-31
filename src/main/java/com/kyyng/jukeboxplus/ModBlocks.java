package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // ---------- IDENTIFIERS ----------
    public static final Identifier DISC_SHELF_ID =
            Identifier.of(JukeboxPlus.MOD_ID, "disc_shelf");
    public static final Identifier ADVANCED_JUKEBOX_ID =
            Identifier.of(JukeboxPlus.MOD_ID, "advanced_jukebox");
    public static final Identifier TAPE_DECK_ID =
            Identifier.of(JukeboxPlus.MOD_ID, "tape_deck");

    // ---------- REGISTRY KEYS ----------
    public static final RegistryKey<Block> DISC_SHELF_BLOCK_KEY =
            RegistryKey.of(Registries.BLOCK.getKey(), DISC_SHELF_ID);
    public static final RegistryKey<Block> ADVANCED_JUKEBOX_BLOCK_KEY =
            RegistryKey.of(Registries.BLOCK.getKey(), ADVANCED_JUKEBOX_ID);
    public static final RegistryKey<Block> TAPE_DECK_BLOCK_KEY =
            RegistryKey.of(Registries.BLOCK.getKey(), TAPE_DECK_ID);

    public static final RegistryKey<Item> DISC_SHELF_ITEM_KEY =
            RegistryKey.of(Registries.ITEM.getKey(), DISC_SHELF_ID);
    public static final RegistryKey<Item> ADVANCED_JUKEBOX_ITEM_KEY =
            RegistryKey.of(Registries.ITEM.getKey(), ADVANCED_JUKEBOX_ID);
    public static final RegistryKey<Item> TAPE_DECK_ITEM_KEY =
            RegistryKey.of(Registries.ITEM.getKey(), TAPE_DECK_ID);

    // ---------- BLOCK ----------
    public static final Block DISC_SHELF = new DiscShelfBlock(
            AbstractBlock.Settings
                    .copy(Blocks.OAK_PLANKS)
                    .nonOpaque()
                    .registryKey(DISC_SHELF_BLOCK_KEY)
    );
    public static final Block ADVANCED_JUKEBOX = new AdvancedJukeboxBlock(
            AbstractBlock.Settings
                    .copy(Blocks.JUKEBOX)
                    .registryKey(ADVANCED_JUKEBOX_BLOCK_KEY)
    );
    public static final Block TAPE_DECK = new TapeDeckBlock(
            AbstractBlock.Settings
                    .copy(Blocks.LECTERN)
                    .registryKey(TAPE_DECK_BLOCK_KEY)
    );

    // ---------- ITEM ----------
    public static final Item DISC_SHELF_ITEM = new BlockItem(
            DISC_SHELF,
            new Item.Settings().registryKey(DISC_SHELF_ITEM_KEY)
    );
    public static final Item ADVANCED_JUKEBOX_ITEM = new BlockItem(
            ADVANCED_JUKEBOX,
            new Item.Settings().registryKey(ADVANCED_JUKEBOX_ITEM_KEY)
    );
    public static final Item TAPE_DECK_ITEM = new BlockItem(
            TAPE_DECK,
            new Item.Settings().registryKey(TAPE_DECK_ITEM_KEY)
    );

    public static void register() {

        // Register block
        Registry.register(
                Registries.BLOCK,
                DISC_SHELF_ID,
                DISC_SHELF
        );
        Registry.register(
                Registries.BLOCK,
                ADVANCED_JUKEBOX_ID,
                ADVANCED_JUKEBOX
        );
        Registry.register(
                Registries.BLOCK,
                TAPE_DECK_ID,
                TAPE_DECK
        );

        // Register item
        Registry.register(
                Registries.ITEM,
                DISC_SHELF_ID,
                DISC_SHELF_ITEM
        );
        Registry.register(
                Registries.ITEM,
                ADVANCED_JUKEBOX_ID,
                ADVANCED_JUKEBOX_ITEM
        );
        Registry.register(
                Registries.ITEM,
                TAPE_DECK_ID,
                TAPE_DECK_ITEM
        );

        // Add to creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(entries -> {
                    entries.add(DISC_SHELF_ITEM);
                    entries.add(ADVANCED_JUKEBOX_ITEM);
                    entries.add(TAPE_DECK_ITEM);
                });
    }
}
