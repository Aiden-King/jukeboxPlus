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
    private static final int PROGRESS_X = 80;
    private static final int PROGRESS_Y = 41;
    private static final int PROGRESS_WIDTH = 26;
    private static final int PROGRESS_HEIGHT = 19;
    private static final int PROGRESS_U = 178;
    private static final int PROGRESS_V = 41;

    private ButtonWidget writeButton;

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
        int buttonY = y + OUTPUT_SQUARE_Y + OUTPUT_SQUARE_SIZE + 3;
        this.writeButton = addDrawableChild(ButtonWidget.builder(Text.literal("Write"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, 0);
            }
        }).dimensions(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.writeButton != null) {
            this.writeButton.active = !handler.isWriting();
        }
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        if (handler.isWriting()) {
            int progress = handler.getWriteProgress();
            int maxProgress = handler.getWriteTime();
            if (maxProgress > 0) {
                int scaledProgress = (int) (((double) progress / maxProgress) * PROGRESS_WIDTH);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x + PROGRESS_X, y + PROGRESS_Y, PROGRESS_U, PROGRESS_V, Math.min(scaledProgress, PROGRESS_WIDTH), PROGRESS_HEIGHT, 256, 256);
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 4210752, false);
    }
}
