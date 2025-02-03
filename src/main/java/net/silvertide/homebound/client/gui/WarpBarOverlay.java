package net.silvertide.homebound.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.data.ClientWarpData;
import net.silvertide.homebound.util.HomeboundUtil;

public class WarpBarOverlay implements LayeredDraw.Layer {
    private static final WarpBarOverlay instance = new WarpBarOverlay();
    public static WarpBarOverlay get(){
        return instance;
    }
    public final static ResourceLocation TEXTURE = Homebound.id("textures/gui/warp_bars.png");
    static final int IMAGE_WIDTH = 64;
    static final int IMAGE_HEIGHT = 9;
    static final int COMPLETION_BAR_WIDTH = 60;
    public void render(GuiGraphics guiHelper, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator() || !ClientWarpData.isPlayerWarping() || minecraft.level == null) {
            return;
        }

        long currentGameTime = minecraft.level.getGameTime();
        long totalWarpDurationInTicks = ClientWarpData.getFinishWarpTimeStamp() - ClientWarpData.getStartWarpTimeStamp();
        float warpCompletionPercent = (currentGameTime - ClientWarpData.getStartWarpTimeStamp()) / (float) totalWarpDurationInTicks;

        int screenWidth = guiHelper.guiWidth();
        int screenHeight = guiHelper.guiHeight();
        int barX, barY;
        barX = screenWidth / 2 - IMAGE_WIDTH / 2;
        barY = screenHeight / 2 + screenHeight / 6;

        renderBarBackground(guiHelper, barX, barY);
        renderBarProgress(guiHelper, barX, barY, warpCompletionPercent);

        String remainingTimeString = HomeboundUtil.timeFromTicks((1 - warpCompletionPercent) * totalWarpDurationInTicks, 1);
        var font = Minecraft.getInstance().font;
        int countdownTextX = barX + (IMAGE_WIDTH - font.width(remainingTimeString)) / 2;
        int countdownTextY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, remainingTimeString, countdownTextX, countdownTextY, 0xFFFFFF);

        String warpHomeText = "Returning Home";
        int warpHomeTextX = barX + (IMAGE_WIDTH - font.width(warpHomeText)) / 2;
        int warpHomeTextY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, warpHomeText, warpHomeTextX, warpHomeTextY - font.lineHeight - 2, 0xFFFFFF);
    }

    private void renderBarBackground(GuiGraphics guiHelper, int x, int y) {
        guiHelper.blit(TEXTURE, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
    }

    private void renderBarProgress(GuiGraphics guiHelper, int x, int y, float warpCompletionPercent) {
        guiHelper.blit(TEXTURE, x, y, 0, IMAGE_HEIGHT*2, (int) (COMPLETION_BAR_WIDTH * warpCompletionPercent + (IMAGE_WIDTH - COMPLETION_BAR_WIDTH) / 2), IMAGE_HEIGHT);
    }
}
