package com.kyyng.jukeboxplus;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class BlockmanScreen extends HandledScreen<BlockmanScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(JukeboxPlus.MOD_ID, "textures/gui/blockman.png");

    public BlockmanScreen(BlockmanScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 261;
        backgroundHeight = 289;
    }

    @Override
    protected void init() {
        super.init();
        titleX = 49;
        titleY = 6;
        playerInventoryTitleX = 49;
        playerInventoryTitleY = 201;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 261, 289);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawSongList(context);
    }

    private void drawSongList(DrawContext context) {
        if (client == null || client.player == null) {
            return;
        }
        ItemStack blockman = handler.isMainHand()
                ? client.player.getMainHandStack()
                : client.player.getOffHandStack();
        ItemStack cassette = BlockmanData.getCassette(blockman, client.player.getEntityWorld().getRegistryManager());
        List<Identifier> songs = CassetteData.getSongs(cassette);

        int textX = 49;
        int textY = 18;

        if (songs.isEmpty()) {
            context.drawText(textRenderer, Text.literal("No songs"), x + textX, y + textY, 0xFFFFFF, false);
            return;
        }

        Registry<net.minecraft.block.jukebox.JukeboxSong> registry =
                client.player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG);
        for (int i = 0; i < songs.size(); i++) {
            Identifier id = songs.get(i);
            net.minecraft.block.jukebox.JukeboxSong song = registry.get(id);
            Text line = song != null ? song.description() : Text.literal(id.toString());
            context.drawText(textRenderer, line, x + textX, y + textY + i * 9, 0xFFFFFF, false);
        }
    }
}
