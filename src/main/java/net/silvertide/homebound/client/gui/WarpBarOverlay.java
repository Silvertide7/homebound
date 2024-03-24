package net.silvertide.homebound.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.util.HomeboundUtil;

public class WarpBarOverlay implements IGuiOverlay {
    private static final WarpBarOverlay instance = new WarpBarOverlay();
    public static WarpBarOverlay get(){
        return instance;
    }
    public final static ResourceLocation TEXTURE = new ResourceLocation(Homebound.MOD_ID, "textures/gui/warp_bars.png");
    static final int IMAGE_WIDTH = 64;
    static final int IMAGE_HEIGHT = 9;
    static final int COMPLETION_BAR_WIDTH = 60;
    public void render(ForgeGui gui, GuiGraphics guiHelper, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        if(!ClientWarpData.isPlayerWarping() || minecraft.level == null) return;

        long currentGameTime = minecraft.level.getGameTime();
        long totalWarpDurationInTicks = ClientWarpData.getFinishWarpTimeStamp() - ClientWarpData.getStartWarpTimeStamp();
        float warpCompletionPercent = (currentGameTime - ClientWarpData.getStartWarpTimeStamp()) / (float) totalWarpDurationInTicks;

        int barX, barY;
        barX = screenWidth / 2 - IMAGE_WIDTH / 2;
        barY = screenHeight / 2 + screenHeight / 6;

        guiHelper.blit(TEXTURE, barX, barY, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
        guiHelper.blit(TEXTURE, barX, barY, 0, IMAGE_HEIGHT*2, (int) (COMPLETION_BAR_WIDTH * warpCompletionPercent + (IMAGE_WIDTH - COMPLETION_BAR_WIDTH) / 2), IMAGE_HEIGHT);

        String remainingTimeString = HomeboundUtil.timeFromTicks((1 - warpCompletionPercent) * totalWarpDurationInTicks, 1);
        var font = gui.getFont();
        int countdownTextX = barX + (IMAGE_WIDTH - font.width(remainingTimeString)) / 2;
        int countdownTextY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, remainingTimeString, countdownTextX, countdownTextY, 0xFFFFFF);

        String warpHomeText = "Returning Home";
        int warpHomeTextX = barX + (IMAGE_WIDTH - font.width(warpHomeText)) / 2;
        int warpHomeTextY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, warpHomeText, warpHomeTextX, warpHomeTextY - font.lineHeight - 2, 0xFFFFFF);

    }
}
