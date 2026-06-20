package net.silvertide.homebound.events.custom;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silvertide.homebound.item.IWarpItem;

public class StartWarpEvent extends PlayerEvent implements ICancellableEvent {
    private final IWarpItem warpItem;
    public StartWarpEvent(Player player, IWarpItem warpItem) {
        super(player);
        this.warpItem = warpItem;
    }
    public IWarpItem getWarpItem() {
        return this.warpItem;
    }
}
