package com.kyyng.jukeboxplus;

import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.item.tooltip.TooltipType;

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
            tooltip.accept(Text.literal("Empty"));
            return;
        }
        for (Identifier id : songs) {
            tooltip.accept(Text.literal(id.toString()));
        }
    }
}
