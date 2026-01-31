package com.kyyng.jukeboxplus;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TapeDeckScreen extends HandledScreen<TapeDeckScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(JukeboxPlus.MOD_ID, "textures/gui/tape_deck.png");
    private static final int OUTPUT_SQUARE_X = 113;
    private static final int OUTPUT_SQUARE_Y = 37;
    private static final int OUTPUT_SQUARE_SIZE = 24;
    private static final int BUTTON_WIDTH = 40;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_PADDING = 3;

    public TapeDeckScreen(TapeDeckScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 178;
        backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        titleX = 8;
        titleY = 6;
        playerInventoryTitleX = 9;
        playerInventoryTitleY = 72;

        int buttonX = x + OUTPUT_SQUARE_X + (OUTPUT_SQUARE_SIZE - BUTTON_WIDTH) / 2;
        int buttonY = y + OUTPUT_SQUARE_Y + OUTPUT_SQUARE_SIZE + BUTTON_PADDING;
        addDrawableChild(ButtonWidget.builder(Text.literal("Write"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, 0);
            }
        }).dimensions(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 4210752, false);
    }
}
