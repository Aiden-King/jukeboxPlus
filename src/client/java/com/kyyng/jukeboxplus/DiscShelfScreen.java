package com.kyyng.jukeboxplus;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DiscShelfScreen extends HandledScreen<DiscShelfScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(JukeboxPlus.MOD_ID, "textures/gui/disc_shelf.png");
    private static final int PANEL_WIDTH = 86;
    private static final int PANEL_PADDING = 6;

    public DiscShelfScreen(DiscShelfScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 133;
    }

    @Override
    protected void init() {
        super.init();
        titleX = ((backgroundWidth - PANEL_WIDTH) / 2) + PANEL_PADDING;
        titleY = 6;
        playerInventoryTitleX = 8;
        playerInventoryTitleY = 41;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }
}
