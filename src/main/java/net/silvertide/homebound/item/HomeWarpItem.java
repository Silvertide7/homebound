package net.silvertide.homebound.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
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
    protected int useDuration;
    protected int cooldown;
    private int maxDistance;
    protected boolean canDimTravel;
    private boolean soulbound;
    private boolean isEnchantable;
    private int enchantability;

    public HomeWarpItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.useDuration = properties.useDuration;
        this.maxDistance = properties.maxDistance;
        this.canDimTravel = properties.canDimTravel;
        this.cooldown = properties.cooldown;
        this.soulbound = properties.soulbound;
        this.isEnchantable = properties.isEnchantable;
        this.enchantability = properties.enchantability;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide()) {
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
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        if(player != null && !level.isClientSide() && player.isCrouching()){
            setHome(player, (ServerLevel) level);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity entity, ItemStack pStack, int pRemainingUseDuration) {
        if(!entity.level().isClientSide && pRemainingUseDuration%6==0) {
            Player player = (Player) entity;
            ServerLevel serverLevel = (ServerLevel) pLevel;
            int scalingParticles = (this.useDuration - pRemainingUseDuration)/12;
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
                warpCap.setCooldown(player.level().getGameTime(), getFinalCooldown(player, serverLevel, pStack));
            }
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, 30);
        });
    }

    protected int getFinalCooldown(Player player, ServerLevel level, ItemStack stack) {
        return getBaseCooldown(stack);
    }

    protected int getBaseCooldown(ItemStack stack) {
        return applyCooldownEnchant(this.cooldown, stack);
    }

    protected int applyCooldownEnchant(int cooldown, ItemStack stack){
        int cooldownReductionLevel = stack.getEnchantmentLevel(EnchantmentRegistry.COOLDOWN_REDUCTION.get());
        if (cooldownReductionLevel > 0) {
            double reducedCooldown = cooldown - 0.05*cooldownReductionLevel*cooldown;
            return (int) reducedCooldown;
        }
        return cooldown;
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
        int channelHasteLevel = pStack.getEnchantmentLevel(EnchantmentRegistry.CHANNEL_HASTE.get());
        if (channelHasteLevel > 0) {
            double quickCastDuration = this.useDuration - 0.1*channelHasteLevel*this.useDuration;
            return (int) quickCastDuration;
        }
        return this.useDuration;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return this.isEnchantable;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return this.isEnchantable ? this.enchantability : 0;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    protected void addCooldownHoverText(List<Component> pTooltipComponents, ItemStack stack) {
        pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(this.getBaseCooldown(stack)) + "§r"));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.literal("To set your home crouch and use the item."));
            addCooldownHoverText(pTooltipComponents, pStack);
            pTooltipComponents.add(Component.literal("§aCast Time: " + this.getUseDuration(pStack) / 20.0 + " seconds.§r"));
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
        boolean isEnchantable = true;
        int enchantability = 15;

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
        public Properties isEnchantable(boolean isEnchantable) {
            this.isEnchantable = isEnchantable;
            return this;
        }

        public Properties enchantability(int enchantability) {
            this.enchantability = enchantability;
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