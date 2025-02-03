package net.silvertide.homebound.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.WarpAttachmentUtil;

public class CmdNodeInfo {
    private static final String TARGET_ARG = "Target";

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("info").executes(CmdNodeInfo::getPlayerInfo);

    }
    public static int getPlayerInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        long gameTime = ctx.getSource().getLevel().getGameTime();
        ServerPlayer player = ctx.getSource().getPlayer();
        if(player == null) return 0;

        WarpAttachmentUtil.getWarpAttachment(player).ifPresent(warpAttachment -> {
            String warpPosString = "Home: §cNot set§r.";
            if(warpAttachment.warpPos() != null) {
                warpPosString = "Home: " + warpAttachment.warpPos().toString();
            }

            String cooldownString = "Cooldown: §2Ready§r";
            if(warpAttachment.hasCooldown(gameTime)) {
                cooldownString = "Cooldown: §c" + HomeboundUtil.formatTime(warpAttachment.getRemainingCooldown(gameTime)) + "§r";
            }
            HomeboundUtil.sendSystemMessage(player, "§3" + warpPosString + "§r");
            HomeboundUtil.sendSystemMessage(player, "§3" + cooldownString + "§r");
        });
        return 0;
    }
}
