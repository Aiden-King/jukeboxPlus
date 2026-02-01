package com.kyyng.jukeboxplus;

import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.item.tooltip.TooltipType;

import net.minecraft.util.Formatting;
import java.util.List;
import java.util.function.Consumer;

public class CassetteItem extends Item {

    public CassetteItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplayComponent displayComponent,
            Consumer<Text> tooltip,
            TooltipType type
    ) {
        List<Identifier> songs = CassetteData.getSongs(stack);
        if (songs.isEmpty()) {
            tooltip.accept(Text.translatable("tooltip.jukeboxplus.cassette").formatted(Formatting.GRAY));
            tooltip.accept(Text.literal("Empty").formatted(Formatting.GRAY));
            return;
        }

        tooltip.accept(Text.translatable("tooltip.jukeboxplus.cassette").formatted(Formatting.GRAY));

        for (Identifier id : songs) {
            boolean found = false;
            if (context.getRegistryLookup() != null) {
                var lookup = context.getRegistryLookup().getOptional(net.minecraft.registry.RegistryKeys.JUKEBOX_SONG);
                if (lookup.isPresent()) {
                    var entry = lookup.get().getOptional(net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.JUKEBOX_SONG, id));
                    if (entry.isPresent()) {
                        tooltip.accept(entry.get().value().description());
                        found = true;
                    }
                }
            }
            if (!found) {
                tooltip.accept(Text.literal(id.toString()).formatted(net.minecraft.util.Formatting.GRAY));
            }
        }
    }
}
