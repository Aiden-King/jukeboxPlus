package com.kyyng.jukeboxplus;

import com.mojang.serialization.DataResult;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

import java.util.Optional;

public final class BlockmanData {

    private static final String CASSETTE_KEY = "Cassette";
    private static final String PLAY_INDEX_KEY = "PlayIndex";
    private static final String PLAYING_KEY = "Playing";

    private BlockmanData() {
    }

    public static ItemStack getCassette(ItemStack blockman, RegistryWrapper.WrapperLookup lookup) {
        NbtComponent data = blockman.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) {
            return ItemStack.EMPTY;
        }
        NbtCompound nbt = data.copyNbt();
        if (!nbt.contains(CASSETTE_KEY)) {
            return ItemStack.EMPTY;
        }
        NbtElement element = nbt.get(CASSETTE_KEY);
        if (element == null) {
            return ItemStack.EMPTY;
        }
        DataResult<ItemStack> decoded = ItemStack.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, lookup), element);
        Optional<ItemStack> result = decoded.result();
        return result.orElse(ItemStack.EMPTY);
    }

    public static void setCassette(ItemStack blockman, ItemStack cassette, RegistryWrapper.WrapperLookup lookup) {
        NbtComponent data = blockman.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = data == null ? new NbtCompound() : data.copyNbt();
        if (cassette.isEmpty()) {
            nbt.remove(CASSETTE_KEY);
        } else {
            DataResult<NbtElement> encoded = ItemStack.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, lookup), cassette);
            encoded.result().ifPresent(element -> nbt.put(CASSETTE_KEY, element));
        }
        blockman.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static int getPlayIndex(ItemStack blockman) {
        NbtComponent data = blockman.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        NbtCompound nbt = data.copyNbt();
        return nbt.getInt(PLAY_INDEX_KEY, 0);
    }

    public static void setPlayIndex(ItemStack blockman, int index) {
        NbtComponent data = blockman.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = data == null ? new NbtCompound() : data.copyNbt();
        nbt.putInt(PLAY_INDEX_KEY, index);
        blockman.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static boolean isPlaying(ItemStack blockman) {
        NbtComponent data = blockman.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) {
            return false;
        }
        NbtCompound nbt = data.copyNbt();
        return nbt.getBoolean(PLAYING_KEY, false);
    }

    public static void setPlaying(ItemStack blockman, boolean playing) {
        NbtComponent data = blockman.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = data == null ? new NbtCompound() : data.copyNbt();
        nbt.putBoolean(PLAYING_KEY, playing);
        blockman.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}
