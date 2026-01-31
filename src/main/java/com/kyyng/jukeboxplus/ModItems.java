package com.kyyng.jukeboxplus;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Identifier BLOCKMAN_ID =
            Identifier.of(JukeboxPlus.MOD_ID, "blockman");
    public static final Identifier CASSETTE_ID =
            Identifier.of(JukeboxPlus.MOD_ID, "cassette");
    public static final Identifier EMPTY_TAPE_ID =
            Identifier.of(JukeboxPlus.MOD_ID, "empty_tape");

    public static final RegistryKey<Item> BLOCKMAN_KEY =
            RegistryKey.of(Registries.ITEM.getKey(), BLOCKMAN_ID);
    public static final RegistryKey<Item> CASSETTE_KEY =
            RegistryKey.of(Registries.ITEM.getKey(), CASSETTE_ID);
    public static final RegistryKey<Item> EMPTY_TAPE_KEY =
            RegistryKey.of(Registries.ITEM.getKey(), EMPTY_TAPE_ID);

    public static final Item BLOCKMAN = new BlockmanItem(
            new Item.Settings().registryKey(BLOCKMAN_KEY).maxCount(1)
    );
    public static final Item CASSETTE = new CassetteItem(
            new Item.Settings().registryKey(CASSETTE_KEY).maxCount(1)
    );
    public static final Item EMPTY_TAPE = new Item(
            new Item.Settings().registryKey(EMPTY_TAPE_KEY)
    );

    public static void register() {
        Registry.register(Registries.ITEM, BLOCKMAN_ID, BLOCKMAN);
        Registry.register(Registries.ITEM, CASSETTE_ID, CASSETTE);
        Registry.register(Registries.ITEM, EMPTY_TAPE_ID, EMPTY_TAPE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(entries -> {
                    entries.add(BLOCKMAN);
                    entries.add(CASSETTE);
                    entries.add(EMPTY_TAPE);
                });
    }
}
