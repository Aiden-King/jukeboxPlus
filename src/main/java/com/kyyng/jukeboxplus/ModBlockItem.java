package com.kyyng.jukeboxplus;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ModBlockItem extends BlockItem {
    private final String tooltipKey;

    public ModBlockItem(Block block, Settings settings, String tooltipKey) {
        super(block, settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, net.minecraft.component.type.TooltipDisplayComponent displayComponent, java.util.function.Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable(tooltipKey).formatted(Formatting.GRAY));
    }
}
