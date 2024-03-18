package net.silvertide.homebound.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.silvertide.homebound.util.CapabilityUtil;
import net.silvertide.homebound.util.HomeboundUtil;

public class CmdNodeInfo {
    private static final String TARGET_ARG = "Target";

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("info").executes(CmdNodeInfo::getPlayerInfo);

    }
    public static int getPlayerInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        long gameTime = ctx.getSource().getLevel().getGameTime();
        ServerPlayer player = ctx.getSource().getPlayer();
        CapabilityUtil.getHome(player).ifPresent(warpCap -> {
            String warpPosString = "Home: §cNot set§r.";
            if(warpCap.getWarpPos() != null) {
                warpPosString = "Home: " + warpCap.getWarpPos().toString();
            }

            String cooldownString = "Cooldown: §2Ready§r";
            if(warpCap.hasCooldown(gameTime)) {
                cooldownString = "Cooldown: §c" + HomeboundUtil.formatTime(warpCap.getRemainingCooldown(gameTime)) + "§r";
            }
            player.sendSystemMessage(Component.literal("§3" + warpPosString + "§r"));
            player.sendSystemMessage(Component.literal("§3" + cooldownString + "§r"));
        });
        return 0;
    }
}
