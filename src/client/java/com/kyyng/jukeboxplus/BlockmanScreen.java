package com.kyyng.jukeboxplus;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

public class BlockmanScreen extends HandledScreen<BlockmanScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(JukeboxPlus.MOD_ID, "textures/gui/blockman.png");

    private int lastPlayIndex = -1;
    private boolean lastPlayingState = false;

    public BlockmanScreen(BlockmanScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 240;
    }

    @Override
    protected void init() {
        super.init();
        titleX = -1000; // Hide the title
        playerInventoryTitleX = 64;
        playerInventoryTitleY = 144;

        // Previous button (^)
        addDrawableChild(ButtonWidget.builder(Text.literal("^"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, BlockmanScreenHandler.PREVIOUS_BUTTON_ID);
            }
        }).dimensions(x + 114, y + 43, 14, 14).build());

        // Next button (v)
        addDrawableChild(ButtonWidget.builder(Text.literal("v"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, BlockmanScreenHandler.NEXT_BUTTON_ID);
            }
        }).dimensions(x + 114, y + 83, 14, 14).build());

        // Play button (>)
        addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, BlockmanScreenHandler.PLAY_BUTTON_ID);
            }
        }).dimensions(x + 114, y + 63, 14, 14).build());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        checkTrackChange();
        drawSongList(context);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void checkTrackChange() {
        if (client == null || client.player == null) return;
        
        ItemStack blockman = client.player.getStackInHand(handler.isMainHand() ? Hand.MAIN_HAND : Hand.OFF_HAND);
                
        if (!blockman.isOf(ModItems.BLOCKMAN)) return;
        
        int currentPlayIndex = BlockmanData.getPlayIndex(blockman);
        boolean playing = BlockmanData.isPlaying(blockman);
        
        if (lastPlayIndex == -1) {
            lastPlayIndex = currentPlayIndex;
            lastPlayingState = playing;
            return;
        }
        
        if (currentPlayIndex != lastPlayIndex) {
            lastPlayIndex = currentPlayIndex;
            if (playing) {
                BlockmanSoundPlayer.playFromBlockman(client.player, blockman);
            }
        }

        if (playing != lastPlayingState) {
            lastPlayingState = playing;
            if (playing) {
                if (!BlockmanSoundPlayer.isPlaying(blockman)) {
                    BlockmanSoundPlayer.playFromBlockman(client.player, blockman);
                }
            } else {
                BlockmanSoundPlayer.stop();
            }
        }
    }

    private void drawSongList(DrawContext context) {
        if (client == null || client.player == null) {
            return;
        }
        ItemStack blockman = client.player.getStackInHand(handler.isMainHand() ? Hand.MAIN_HAND : Hand.OFF_HAND);
        ItemStack cassette = BlockmanData.getCassette(blockman, client.player.getEntityWorld().getRegistryManager());
        List<Identifier> songs = CassetteData.getSongs(cassette);

        int textX = 47;
        int textY = 18;

        if (songs.isEmpty()) {
            context.drawText(textRenderer, Text.literal("No songs"), x + textX, y + textY, 0xFFFFFF, false);
            return;
        }

        Registry<net.minecraft.block.jukebox.JukeboxSong> registry =
                client.player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG);
        int playIndex = BlockmanData.getPlayIndex(blockman);

        for (int i = 0; i < songs.size(); i++) {
            Identifier id = songs.get(i);
            net.minecraft.block.jukebox.JukeboxSong song = registry.get(id);
            Text line = song != null ? song.description() : Text.literal(id.toString()).formatted(net.minecraft.util.Formatting.GRAY);
            if (i == playIndex) {
                line = Text.literal("> ").append(line).formatted(net.minecraft.util.Formatting.YELLOW);
            }
            context.drawText(textRenderer, line, x + textX, y + textY + i * 9, 0xFFFFFF, false);
        }
    }
}
