package com.kyyng.jukeboxplus;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class CassetteData {

    private static final String SONGS_KEY = "Songs";

    private CassetteData() {
    }

    public static List<Identifier> getSongs(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) {
            return List.of();
        }

        NbtCompound nbt = data.copyNbt();
        NbtList list = nbt.getListOrEmpty(SONGS_KEY);
        if (list.isEmpty()) {
            return List.of();
        }

        List<Identifier> songs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Identifier id = Identifier.tryParse(list.getString(i, ""));
            if (id != null) {
                songs.add(id);
            }
        }
        return songs;
    }

    public static void setSongs(ItemStack stack, List<Identifier> songs) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = data == null ? new NbtCompound() : data.copyNbt();
        NbtList list = new NbtList();
        for (Identifier id : songs) {
            list.add(NbtString.of(id.toString()));
        }
        nbt.put(SONGS_KEY, list);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}
