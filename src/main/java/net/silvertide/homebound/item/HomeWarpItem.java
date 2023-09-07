package net.silvertide.homebound.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.capabilities.WarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.setup.CapabilityRegistry;
import net.silvertide.homebound.util.HomeboundUtil;

public class HomeWarpItem extends Item {
    private int useDuration;
    private int cooldown;
    private int maxDistance;
    private boolean canDimTravel;
    public HomeWarpItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.useDuration = properties.useDuration;
        this.maxDistance = properties.maxDistance;
        this.canDimTravel = properties.canDimTravel;
        this.cooldown = properties.cooldown;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
        boolean warpFailed = false;
        if (!level.isClientSide()) {
            WarpCap playerWarpCap = (WarpCap) CapabilityRegistry.getHome(player).orElse(null);
            if (player.isCrouching()) {
                playerWarpCap.setWarpPos(player.getOnPos(), level.dimension().location());
                player.sendSystemMessage(Component.literal("Home set."));
            } else {
                if (playerWarpCap == null || playerWarpCap.getWarpPos() == null) {
                    player.sendSystemMessage(Component.literal("No home set"));
                    warpFailed = true;
                } else if (playerWarpCap.hasHomeCooldown()) {
                    player.sendSystemMessage(Component.literal(getCooldownMessage(playerWarpCap.getHomeCooldown())));
                    warpFailed = true;
                } else if (!this.canDimTravel && !playerWarpCap.getWarpPos().isSameDimension(level.dimension().location())) {
                    player.sendSystemMessage(Component.literal(getDimensionMessage(playerWarpCap.getWarpPos().dimension().toString(), level.dimension().location().toString())));
                    warpFailed = true;
                } else if (maxDistance > 0) {
                    int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), level.dimension().location()));
                    if (distanceFromWarp > maxDistance) {
                        player.sendSystemMessage(Component.literal(getDistanceMessage(distanceFromWarp)));
                        warpFailed = true;
                    }
                }
                if (player.getAbilities().instabuild || !warpFailed) {
                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.success(itemstack);
                }
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        boolean clientSide = pLevel.isClientSide;

        if (player != null && !clientSide) {
            WarpCap playerWarpCap = (WarpCap) CapabilityRegistry.getHome(player).orElse(null);
            if(playerWarpCap == null) {
                return pStack;
            }
            HomeboundUtil.warp(player, playerWarpCap.getWarpPos());

            if (!player.getAbilities().instabuild) {
                playerWarpCap.setHomeCooldown(this.cooldown);
            }
            ServerLevel serverlevel = (ServerLevel)pLevel;
            for(int i = 0; i < 5; ++i) {
                serverlevel.sendParticles(ParticleTypes.SONIC_BOOM, player.getX() + pLevel.random.nextDouble(), (double)(player.getY() + 1), (double)player.getZ() + pLevel.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
            }
        }



        return pStack;
    }

    public String getCooldownMessage(int cooldownRemaining) {
        return "You haven't yet recovered from the last warp. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]";
    }
    private String getDimensionMessage(String currDim, String warpDim) {
        return "Can't warp between dimensions. [Current: " + HomeboundUtil.formatDimension(currDim) + ", Home: " + HomeboundUtil.formatDimension(warpDim) + "]";
    }
    public String getDistanceMessage(int distance) {
        return "You are too far from your home. [Current: " + distance + ", Max: " + this.maxDistance + "]";
    }
    @Override
    public int getUseDuration(ItemStack pStack) {
        return this.useDuration;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    public static class Properties {
        int cooldown = 3600;
        int useDuration = 40;
        int maxDistance = 0;
        boolean canDimTravel = true;
        Rarity rarity = Rarity.RARE;

        public Properties cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }
        public Properties useDuration(int useDuration) {
            this.useDuration = useDuration;
            return this;
        }
        public Properties maxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }
        public Properties canDimTravel(boolean canDimTravel) {
            this.canDimTravel = canDimTravel;
            return this;
        }
        public Properties rarity(Rarity rarity){
            this.rarity = rarity;
            return this;
        }
    }
}
