package net.silvertide.homebound.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.silvertide.homebound.item.IWarpInitiator;

public class WarpEvent extends PlayerEvent {
    private final IWarpInitiator warpInitiator;
    public WarpEvent(Player player, IWarpInitiator warpInitiator) {
        super(player);
        this.warpInitiator = warpInitiator;
    }

    public IWarpInitiator getWarpInitiator() {
        return this.warpInitiator;
    }
}
