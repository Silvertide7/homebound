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
import net.silvertide.homebound.capabilities.CapabilityRegistry;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.util.HomeboundUtil;

public class HomeWarpItem extends Item {
    private final int DAMAGE_COOLDOWN = 5;
    private int useDuration;
    private int cooldown;
    private int maxDistance;
    private boolean canDimTravel;

    private float playerHealth;
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
        if (!level.isClientSide()) {

            this.playerHealth = player.getHealth();
            boolean canWarp = true;

            IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
            if(playerWarpCap == null) return InteractionResultHolder.consume(itemstack);


            // If crouching then set home
            if (player.isCrouching()) {
                playerWarpCap.setWarpPos(player, level);
                player.sendSystemMessage(Component.literal("Home set."));
            } else {
                // If not crouching then attempt to start warping.
                // Check all the cases that would not allow a player to warp.
                if (playerWarpCap.getWarpPos() == null) {
                    player.sendSystemMessage(Component.literal("No home set"));
                    canWarp = false;
                } else if (!player.getAbilities().instabuild) {
                    if (playerWarpCap.hasHomeCooldown()) {
                        player.sendSystemMessage(Component.literal(getCooldownMessage(playerWarpCap.getHomeCooldown())));
                        canWarp = false;
                    } else if (!this.canDimTravel && !playerWarpCap.getWarpPos().isSameDimension(level.dimension().location())) {
                        player.sendSystemMessage(Component.literal(getDimensionMessage(playerWarpCap.getWarpPos().dimension().toString(), level.dimension().location().toString())));
                        canWarp = false;
                    } else if (maxDistance > 0) {
                        int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), level.dimension().location()));
                        if (distanceFromWarp > maxDistance) {
                            player.sendSystemMessage(Component.literal(getDistanceMessage(distanceFromWarp)));
                            canWarp = false;
                        }
                    }
                }
                if (canWarp) {
                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.success(itemstack);
                }
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity entity, ItemStack pStack, int pRemainingUseDuration) {
        if(!entity.level().isClientSide){
            Player player = (Player) entity;
            if(player.getHealth() > this.playerHealth){
                this.playerHealth = player.getHealth();
            } else if(player.getHealth() < this.playerHealth){
                player.stopUsingItem();
                player.sendSystemMessage(Component.literal("Warp cancelled."));
                CapabilityRegistry.getHome(player).ifPresent(warpCap -> {
                    if (!warpCap.hasHomeCooldown()){
                        warpCap.setHomeCooldown(DAMAGE_COOLDOWN);
                    }
                });

            } else if(entity.getRandom().nextInt()%3==0){
                ServerLevel serverLevel = (ServerLevel) pLevel;
                serverLevel.sendParticles(ParticleTypes.ENCHANT, entity.getX() + pLevel.random.nextDouble(), (entity.getY() + 1), entity.getZ() + pLevel.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
            }
        }
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        boolean clientSide = pLevel.isClientSide;

        if (player != null && !clientSide) {
            IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
            if(playerWarpCap == null) {
                return pStack;
            }
            HomeboundUtil.warp(player, playerWarpCap.getWarpPos());

            if (!player.getAbilities().instabuild) {
                playerWarpCap.setHomeCooldown(this.getCooldown(player, pLevel));
            }
            ServerLevel serverlevel = (ServerLevel)pLevel;
            for(int i = 0; i < 5; ++i) {
                serverlevel.sendParticles(ParticleTypes.SONIC_BOOM, player.getX() + pLevel.random.nextDouble(), (double)(player.getY() + 1), (double)player.getZ() + pLevel.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
            }
        }

        return pStack;
    }

    public int getCooldown(Player player, Level level) {
        return this.cooldown;
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
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    public static class Properties {
        int cooldown = 3600;
        int useDuration = 80;
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
