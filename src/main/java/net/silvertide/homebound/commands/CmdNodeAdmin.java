package net.silvertide.homebound.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.silvertide.homebound.util.CapabilityUtil;
import net.silvertide.homebound.util.HomeboundUtil;

public class CmdNodeAdmin {
    private static final String TARGET_ARG = "Target";

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("admin")
                .requires(p -> p.hasPermission(2))
                .then(Commands.argument(TARGET_ARG, EntityArgument.players())
                        .then(Commands.literal("clear")
                            .then(Commands.literal("cooldown")
                                .executes(CmdNodeAdmin::adminClearCooldown))
                            .then(Commands.literal("home")
                                    .executes(CmdNodeAdmin::adminClearHome))));

    }
    public static int adminClearCooldown(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        for (ServerPlayer player : EntityArgument.getPlayers(ctx, TARGET_ARG)) {
            CapabilityUtil.getHome(player).ifPresent(warpCap -> {
                warpCap.setCooldown(0,0);
            });
        }
        return 0;
    }

    public static int adminClearHome(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        for (ServerPlayer player : EntityArgument.getPlayers(ctx, TARGET_ARG)) {
            CapabilityUtil.getHome(player).ifPresent(warpCap -> {
                if(warpCap.getWarpPos() != null) warpCap.clearHome();
            });
        }
        return 0;
    }
}
