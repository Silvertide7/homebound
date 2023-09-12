package net.silvertide.homebound.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.registry.EnchantmentRegistry;
import net.silvertide.homebound.util.CapabilityUtil;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.ParticleUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeWarpItem extends Item implements ISoulboundItem {
    protected final int SET_HOME_DURATION = 40;
    protected int useDuration;
    private int cooldown;
    private int maxDistance;
    protected boolean canDimTravel;
    private boolean soulbound;


    public HomeWarpItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.useDuration = properties.useDuration;
        this.maxDistance = properties.maxDistance;
        this.canDimTravel = properties.canDimTravel;
        this.cooldown = properties.cooldown;
        this.soulbound = properties.soulbound;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide()) {
            // If crouching then set home
            if (player.isCrouching()) {
                ServerLevel serverLevel = (ServerLevel) level;
                setHome(player, serverLevel);
                return InteractionResultHolder.success(itemstack);
            }
            // If not crouching then attempt to start warping.
            else {
                AtomicBoolean canWarp = new AtomicBoolean(false);
                CapabilityUtil.getHome(player).ifPresent(playerWarpCap -> {
                    canWarp.set(isHomeSet(player, playerWarpCap) &&
                            (player.getAbilities().instabuild ||
                                    (hasNoCooldown(player, playerWarpCap)
                                            && checkDimensionalTravel(player, level, playerWarpCap)
                                            && withinMaxDistance(player, level, playerWarpCap))));
                });
                if (canWarp.get()) {
                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.success(itemstack);
                }
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity entity, ItemStack pStack, int pRemainingUseDuration) {
        if(!entity.level().isClientSide && pRemainingUseDuration%5==0) {
            Player player = (Player) entity;
            ServerLevel serverLevel = (ServerLevel) pLevel;
            int scalingParticles = (this.useDuration - pRemainingUseDuration)/10;
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, scalingParticles);
            HomeboundUtil.playSound(serverLevel, player, SoundEvents.BLAZE_BURN);
        }
    }


    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        boolean clientSide = pLevel.isClientSide;

        if (player != null && !clientSide) {
            ServerLevel serverLevel = (ServerLevel) pLevel;
            warpHome(player, serverLevel, pStack);
        }
        return pStack;
    }

    private boolean isHomeSet(Player player, IWarpCap playerWarpCap) {
        if (playerWarpCap.getWarpPos() == null) {
            player.sendSystemMessage(Component.literal("No home set"));
            return false;
        }
        return true;
    }

    private boolean hasNoCooldown(Player player, IWarpCap playerWarpCap) {
        long gameTime = player.level().getGameTime();
        if (playerWarpCap.hasCooldown(gameTime)) {
            int timeRemaining = playerWarpCap.getRemainingCooldown(gameTime);
            player.sendSystemMessage(Component.literal(getCooldownMessage(timeRemaining)));
            return false;
        }
        return true;
    }

    private boolean checkDimensionalTravel(Player player, Level level, IWarpCap playerWarpCap) {
        if (!this.canDimTravel && !playerWarpCap.getWarpPos().isSameDimension(level.dimension().location())) {
            player.sendSystemMessage(Component.literal(getDimensionMessage()));
            return false;
        }
        return true;
    }

    private boolean withinMaxDistance(Player player, Level level, IWarpCap playerWarpCap) {
        if (maxDistance > 0) {
            int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), level.dimension().location()));
            if (distanceFromWarp > maxDistance) {
                player.sendSystemMessage(Component.literal(getDistanceMessage(distanceFromWarp)));
                return false;
            }
        }
        return true;
    }

    private void setHome(Player player, ServerLevel serverLevel){
        player.sendSystemMessage(Component.literal("§aHome set.§r"));
        CapabilityUtil.getHome(player).ifPresent(warpCap -> {
            warpCap.setWarpPos(player, serverLevel);
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.CRIT, 20);
            HomeboundUtil.playSound(serverLevel, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE);
        });
    }

    protected void warpHome(Player player, ServerLevel serverLevel, ItemStack pStack) {
        CapabilityUtil.getHome(player).ifPresent(warpCap -> {
            HomeboundUtil.warp(player, warpCap.getWarpPos());
            if (!player.getAbilities().instabuild) {
                warpCap.setCooldown(player.level().getGameTime(), getCooldown(player, serverLevel));
            }
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, 30);
        });
    }

    public int getCooldown(Player player, Level level) {
        return this.cooldown;
    }

    public String getCooldownMessage(int cooldownRemaining) {
        return "§cYou haven't recovered. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]§r";
    }
    private String getDimensionMessage() {
        return "§cCan't warp between dimensions.§r";
    }
    public String getDistanceMessage(int distance) {
        return "§cToo far from home. [" + distance + " / " + this.maxDistance + "]§r";
    }
    @Override
    public int getUseDuration(ItemStack pStack) {
        int quickCastLevel = pStack.getEnchantmentLevel(EnchantmentRegistry.CHANNEL_HASTE.get());
        int duration = this.useDuration;
        if (quickCastLevel > 0) {
            double quickCastDuration = (1.0 - pStack.getEnchantmentLevel(EnchantmentRegistry.CHANNEL_HASTE.get()) / 10.0) * this.useDuration;
            duration = (int) quickCastDuration;
        }
        return duration;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    protected void addCooldownHoverText(List<Component> pTooltipComponents) {
        pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(this.cooldown) + "§r"));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.literal("To set your home crouch and channel the item for §e" + this.SET_HOME_DURATION/20 + "§r seconds."));
            pTooltipComponents.add(Component.literal("§aCast Time: " + this.useDuration / 20 + " seconds.§r"));
            addCooldownHoverText(pTooltipComponents);
            if(this.maxDistance > 0) pTooltipComponents.add(Component.literal("§aMax Warp Distance: " + this.maxDistance + " blocks§r"));
            pTooltipComponents.add(Component.literal("§aDimensional Travel: " + (this.canDimTravel ? "Yes" : "No") + "§r"));
            if(this.isSoulbound()) pTooltipComponents.add(Component.literal("§5This item persists death.§r"));
        } else {
            pTooltipComponents.add(Component.literal("Find your way home."));
            pTooltipComponents.add(Component.literal("Press §eSHIFT§r for more information"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean isSoulbound() {
        return this.soulbound;
    }

    public static class Properties {
        int cooldown = 1800;
        int useDuration = 240;
        int maxDistance = 0;
        boolean canDimTravel = false;
        Rarity rarity = Rarity.COMMON;
        boolean soulbound = false;

        public Properties cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }
        public Properties useDuration(int useDuration) {
            this.useDuration = useDuration*20;
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
        public Properties isSoulbound(boolean isSoulbound) {
            this.soulbound = isSoulbound;
            return this;
        }
        public Properties rarity(Rarity rarity){
            this.rarity = rarity;
            return this;
        }
    }
}

/*
                player.stopUsingItem();
                player.sendSystemMessage(Component.literal("§cWarp cancelled.§r"));
                CapabilityUtil.getHome(player).ifPresent(warpCap -> {
                    long gameTime = player.level().getGameTime();
                    if (!warpCap.hasCooldown(gameTime)) {
                        warpCap.setCooldown(gameTime, DAMAGE_COOLDOWN);
                    }
                });
 */