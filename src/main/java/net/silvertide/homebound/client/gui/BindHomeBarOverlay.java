package net.silvertide.homebound.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.data.ClientHomeData;
import net.silvertide.homebound.util.HomeboundUtil;

public class BindHomeBarOverlay implements LayeredDraw.Layer {
    private static final BindHomeBarOverlay instance = new BindHomeBarOverlay();
    public static BindHomeBarOverlay get(){
        return instance;
    }
    public final static ResourceLocation TEXTURE = Homebound.id("textures/gui/warp_bars.png");
    static final int IMAGE_WIDTH = 64;
    static final int IMAGE_HEIGHT = 9;
    static final int COMPLETION_BAR_WIDTH = 60;

    public void render(GuiGraphics guiHelper, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator() || !ClientHomeData.isPlayerBindingHome() || minecraft.level == null) {
            return;
        }

        long currentGameTime = minecraft.level.getGameTime();
        long totalWarpDurationInTicks = ClientHomeData.getFinishHomeTimeStamp() - ClientHomeData.getStartHomeTimeStamp();
        float warpCompletionPercent = (currentGameTime - ClientHomeData.getStartHomeTimeStamp()) / (float) totalWarpDurationInTicks;

        int screenWidth = guiHelper.guiWidth();
        int screenHeight = guiHelper.guiHeight();
        int barX, barY;
        barX = screenWidth / 2 - IMAGE_WIDTH / 2;
        barY = screenHeight / 2 + screenHeight / 6;

        guiHelper.blit(TEXTURE, barX, barY, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
        guiHelper.blit(TEXTURE, barX, barY, 0, IMAGE_HEIGHT, (int) (COMPLETION_BAR_WIDTH * warpCompletionPercent + (IMAGE_WIDTH - COMPLETION_BAR_WIDTH) / 2), IMAGE_HEIGHT);

        String remainingTimeString = HomeboundUtil.timeFromTicks((1 - warpCompletionPercent) * totalWarpDurationInTicks, 1);
        Font font =  Minecraft.getInstance().font;
        int countdownTextX = barX + (IMAGE_WIDTH - font.width(remainingTimeString)) / 2;
        int countdownTextY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, remainingTimeString, countdownTextX, countdownTextY, 0xFFFFFF);

        String bindHomeText = "Binding Home";
        int bindHomeTextX = barX + (IMAGE_WIDTH - font.width(bindHomeText)) / 2;
        int bindHomeTextY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, bindHomeText, bindHomeTextX, bindHomeTextY - font.lineHeight - 2, 0xFFFFFF);

    }
}
