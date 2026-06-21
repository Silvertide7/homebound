package net.silvertide.homebound.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.client.data.ClientHomeData;
import net.silvertide.homebound.client.data.ClientWarpData;
import net.silvertide.homebound.util.HomeboundUtil;

@OnlyIn(Dist.CLIENT)
public class ChannelBarOverlay implements LayeredDraw.Layer {
    private static final ChannelBarOverlay instance = new ChannelBarOverlay();
    public static ChannelBarOverlay get() { return instance; }

    public static final ResourceLocation TEXTURE = Homebound.id("textures/gui/warp_bars.png");
    static final int IMAGE_WIDTH = 64;
    static final int IMAGE_HEIGHT = 9;
    static final int COMPLETION_BAR_WIDTH = 60;

    @Override
    public void render(GuiGraphics guiHelper, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null
                || minecraft.options.hideGui || minecraft.player.isSpectator()) {
            return;
        }

        ChannelBarType type;
        long start, finish;
        if (ClientWarpData.isPlayerWarping()) {
            type = ChannelBarType.WARP;
            start = ClientWarpData.getStartWarpTimeStamp();
            finish = ClientWarpData.getFinishWarpTimeStamp();
        } else if (ClientHomeData.isPlayerBindingHome()) {
            type = ChannelBarType.BIND_HOME;
            start = ClientHomeData.getStartHomeTimeStamp();
            finish = ClientHomeData.getFinishHomeTimeStamp();
        } else {
            return;
        }

        long totalTicks = finish - start;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float percent = progress(start, totalTicks, minecraft.level.getGameTime(), partialTick);

        int barX = guiHelper.guiWidth() / 2 - IMAGE_WIDTH / 2;
        int barY = guiHelper.guiHeight() / 2 + guiHelper.guiHeight() / 6;

        guiHelper.blit(TEXTURE, barX, barY, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
        guiHelper.blit(TEXTURE, barX, barY, 0, IMAGE_HEIGHT * type.progressTexRow,
                (int) (COMPLETION_BAR_WIDTH * percent + (IMAGE_WIDTH - COMPLETION_BAR_WIDTH) / 2), IMAGE_HEIGHT);

        Font font = minecraft.font;
        String remainingTime = HomeboundUtil.timeFromTicks((1 - percent) * totalTicks, 1);
        int countdownX = barX + (IMAGE_WIDTH - font.width(remainingTime)) / 2;
        int countdownY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;
        guiHelper.drawString(font, remainingTime, countdownX, countdownY, 0xFFFFFF);

        int labelX = barX + (IMAGE_WIDTH - font.width(type.label)) / 2;
        guiHelper.drawString(font, type.label, labelX, countdownY - font.lineHeight - 2, 0xFFFFFF);
    }

    private static float progress(long startTick, long totalTicks, long gameTime, float partialTick) {
        if (totalTicks <= 0) return 1f;
        float elapsed = (gameTime - startTick) + partialTick;
        return Math.clamp(elapsed / totalTicks, 0f, 1f);
    }
}
