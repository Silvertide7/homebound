package net.silvertide.homebound.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.silvertide.homebound.item.IWarpItem;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.common.MinecraftForge;
/**
 * StartWarpEvent is fired whenever a {@link Player} tries to warp.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the warp does not start.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class StartWarpEvent extends PlayerEvent {
    private final IWarpItem warpItem;
    public StartWarpEvent(Player player, IWarpItem warpItem) {
        super(player);
        this.warpItem = warpItem;
    }
    @Override
    public boolean isCancelable() {
        return true;
    }
    public IWarpItem getWarpItem() {
        return this.warpItem;
    }
}
