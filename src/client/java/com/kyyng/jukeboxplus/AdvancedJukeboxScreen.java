package com.kyyng.jukeboxplus;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AdvancedJukeboxScreen extends HandledScreen<AdvancedJukeboxScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(JukeboxPlus.MOD_ID, "textures/gui/advanced_jukebox.png");

    public AdvancedJukeboxScreen(AdvancedJukeboxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        titleX = 8;
        titleY = 6;
        playerInventoryTitleX = 8;
        playerInventoryTitleY = 129;
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
