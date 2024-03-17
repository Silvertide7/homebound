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
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.registry.EnchantmentRegistry;
import net.silvertide.homebound.util.HomeboundUtil;
import net.silvertide.homebound.util.ParticleUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeWarpItem extends Item implements ISoulboundItem {
    protected HomewardItemId id;
    private boolean soulbound;
    private boolean isEnchantable;
    private int enchantability;

    public HomeWarpItem(HomewardItemId id, Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.id = id;
        this.soulbound = properties.soulbound;
        this.isEnchantable = properties.isEnchantable;
        this.enchantability = properties.enchantability;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide()) {
            if (canPlayerWarp(player, level)) {
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
        if(player != null && !level.isClientSide()) {
            if(player.isCrouching()) {
                setHome(player, (ServerLevel) level);
                return InteractionResult.SUCCESS;
            } else {
                if(canPlayerWarp(player, level)) {
                    player.startUsingItem(pContext.getHand());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity entity, ItemStack pStack, int pRemainingUseDuration) {
        if(!pLevel.isClientSide()) {
            Player player = (Player) entity;
            ServerLevel serverLevel = (ServerLevel) pLevel;
            
            int activationDuration = this.getActivationDuration(pStack);
            int durationHeld = this.getUseDuration(pStack) - pRemainingUseDuration;
            if (durationHeld < activationDuration) {
                if(pRemainingUseDuration%6==0) {
                    int scalingParticles = (durationHeld)/12;
                    ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, scalingParticles);
                    HomeboundUtil.playSound(serverLevel, player, SoundEvents.BLAZE_BURN);
                }
            } else if(durationHeld == activationDuration) {
                warpHome(player, serverLevel, pStack);
            }
        }
    }

    private boolean isHomeSet(Player player, IWarpCap playerWarpCap) {
        if (playerWarpCap.getWarpPos() == null) {
            player.displayClientMessage(Component.literal("No home set"), true);
            return false;
        }
        return true;
    }

    public boolean canPlayerWarp(Player player, Level level) {
        AtomicBoolean canWarp = new AtomicBoolean(false);
        HomeboundUtil.getHome(player).ifPresent(playerWarpCap -> {
            canWarp.set(isHomeSet(player, playerWarpCap) &&
                    (player.getAbilities().instabuild ||
                            (hasNoCooldown(player, playerWarpCap)
                                    && inValidDimension(player, level, playerWarpCap)
                                    && withinMaxDistance(player, level, playerWarpCap))));
        });
        return canWarp.get();
    }

    private boolean hasNoCooldown(Player player, IWarpCap playerWarpCap) {
        long gameTime = player.level().getGameTime();
        if (playerWarpCap.hasCooldown(gameTime)) {
            int timeRemaining = playerWarpCap.getRemainingCooldown(gameTime);
            player.displayClientMessage(Component.literal(getCooldownMessage(timeRemaining)), true);
            return false;
        }
        return true;
    }

    private boolean inValidDimension(Player player, Level level, IWarpCap playerWarpCap) {
        if (!canDimTravel() && !playerWarpCap.getWarpPos().isSameDimension(level.dimension().location())) {
            player.displayClientMessage(Component.literal(getDimensionMessage()), true);
            return false;
        }
        return true;
    }

    private boolean withinMaxDistance(Player player, Level level, IWarpCap playerWarpCap) {
        int maxDistance = getMaxDistance();
        if (maxDistance > 0) {
            int distanceFromWarp = playerWarpCap.getWarpPos().calculateDistance(new WarpPos(player.getOnPos(), level.dimension().location()));
            if (distanceFromWarp > maxDistance) {
                player.displayClientMessage(Component.literal(getDistanceMessage(maxDistance, distanceFromWarp)), true);
                return false;
            }
        }
        return true;
    }

    private void setHome(Player player, ServerLevel serverLevel){
        HomeboundUtil.getHome(player).ifPresent(warpCap -> {
            warpCap.setWarpPos(player, serverLevel);
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.CRIT, 20);
            HomeboundUtil.playSound(serverLevel, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE);
            player.displayClientMessage(Component.literal("§aHome set.§r"), true);
        });
    }

    protected void warpHome(Player player, ServerLevel serverLevel, ItemStack pStack) {
        HomeboundUtil.getHome(player).ifPresent(warpCap -> {
            HomeboundUtil.warp(player, warpCap.getWarpPos());
            if (!player.getAbilities().instabuild) {
                warpCap.setCooldown(player.level().getGameTime(), getCooldown(pStack, player, serverLevel));
            }
            ParticleUtil.spawnParticals(serverLevel, player, ParticleTypes.PORTAL, 30);
        });
    }

    public int getCooldown(ItemStack stack, Player player, ServerLevel level) {
        int baseCooldown = getBaseCooldown();
        return applyEnchantCooldownModifier(applyDistanceCooldownModifier(player, level, baseCooldown), stack);
    }

    protected double getDistanceBasedCooldownReduction(){
        return switch(this.id) {
            case DUSK_STONE -> Config.DUSK_STONE_MAX_DISTANCE_REDUCTION.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_MAX_DISTANCE_REDUCTION.get();
            default -> 0.0;
        };
    }

    protected int getBlocksPerBonusReducedBy1Percent(){
        return switch(this.id) {
            case DUSK_STONE -> Config.DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT.get();
            default -> 0;
        };
    }

    protected int getBaseCooldown() {
        int cooldown = switch(this.id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_COOLDOWN.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_COOLDOWN.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_COOLDOWN.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_COOLDOWN.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_COOLDOWN.get();
            case DAWN_STONE -> Config.DAWN_STONE_COOLDOWN.get();
            case SUN_STONE -> Config.SUN_STONE_COOLDOWN.get();
            case DUSK_STONE -> Config.DUSK_STONE_COOLDOWN.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_COOLDOWN.get();
            default -> 60;
        };

        return cooldown*60;
    }

    protected int applyEnchantCooldownModifier(int cooldown, ItemStack stack){
        int cooldownReductionLevel = stack.getEnchantmentLevel(EnchantmentRegistry.COOLDOWN_REDUCTION.get());
        if (cooldownReductionLevel > 0) {
            double reducedCooldown = (1 - 0.05*cooldownReductionLevel)*cooldown;
            return (int) reducedCooldown;
        }
        return cooldown;
    }

    protected int applyDistanceCooldownModifier(Player player, ServerLevel level, int cooldown){
        double maxCooldownReduction = getDistanceBasedCooldownReduction();

        if(maxCooldownReduction > 0.0) {
            int blocksPerPercentAdded = getBlocksPerBonusReducedBy1Percent();
            
            IWarpCap playerWarpCap = HomeboundUtil.getWarpCap(player);
            if(playerWarpCap == null) return cooldown;

            WarpPos currentPos = HomeboundUtil.buildWarpPos(player, level);

            int dimensionMultiplier = playerWarpCap.getWarpPos().isSameDimension(currentPos) ? 1 : 2;
            int distanceToHome = playerWarpCap.getWarpPos().calculateDistance(currentPos);
            double distancePenalty = (distanceToHome/blocksPerPercentAdded/100.0)*dimensionMultiplier;

            if(distancePenalty < maxCooldownReduction) {
                double cooldownReductionBonus = (1.0-(maxCooldownReduction-distancePenalty));
                double modifiedCooldown = ((double) cooldown)*cooldownReductionBonus;
                return (int) modifiedCooldown;
            }
        }
        return cooldown;
    }

    public String getCooldownMessage(int cooldownRemaining) {
        return "§cYou haven't recovered. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]§r";
    }
    private String getDimensionMessage() {
        return "§cCan't warp between dimensions.§r";
    }
    public String getDistanceMessage(int maxDistance, int distance) {
        return "§cToo far from home. [" + distance + " / " + maxDistance + "]§r";
    }

    protected int getActivationDuration(ItemStack stack) {
        int useDuration = getBaseUseDuration();
        return applyEnchantHasteModifier(useDuration, stack);
    }

    protected int getBaseUseDuration() {
        int useDuration = switch(this.id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_USE_TIME.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_USE_TIME.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_USE_TIME.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_USE_TIME.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_USE_TIME.get();
            case DAWN_STONE -> Config.DAWN_STONE_USE_TIME.get();
            case SUN_STONE -> Config.SUN_STONE_USE_TIME.get();
            case DUSK_STONE -> Config.DUSK_STONE_USE_TIME.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_USE_TIME.get();
            default -> 10;
        };

        // Multiply by 20 because 20 ticks per second
        return useDuration*20;
    }
    protected int applyEnchantHasteModifier(int useDuration, ItemStack stack) {
        int channelHasteLevel = stack.getEnchantmentLevel(EnchantmentRegistry.CHANNEL_HASTE.get());
        if (channelHasteLevel > 0) {
            double quickCastDuration = useDuration - 0.1*channelHasteLevel*useDuration;
            return (int) quickCastDuration;
        }
        return useDuration;
    }
    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    protected boolean canDimTravel() {
        return switch (this.id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_DIMENSIONAL_TRAVEL.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_DIMENSIONAL_TRAVEL.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_DIMENSIONAL_TRAVEL.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_DIMENSIONAL_TRAVEL.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_DIMENSIONAL_TRAVEL.get();
            case DAWN_STONE -> Config.DAWN_STONE_DIMENSIONAL_TRAVEL.get();
            case SUN_STONE -> Config.SUN_STONE_DIMENSIONAL_TRAVEL.get();
            case DUSK_STONE -> Config.DUSK_STONE_DIMENSIONAL_TRAVEL.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_DIMENSIONAL_TRAVEL.get();
            default -> false;
        };
    }

    protected int getMaxDistance() {
        return switch (this.id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_MAX_DISTANCE.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_MAX_DISTANCE.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_MAX_DISTANCE.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_MAX_DISTANCE.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_MAX_DISTANCE.get();
            case DAWN_STONE -> Config.DAWN_STONE_MAX_DISTANCE.get();
            case SUN_STONE -> Config.SUN_STONE_MAX_DISTANCE.get();
            case DUSK_STONE -> Config.DUSK_STONE_MAX_DISTANCE.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_MAX_DISTANCE.get();
            default -> 0;
        };
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
        double distanceBasedCooldownReduction = getDistanceBasedCooldownReduction();
        int cooldown = applyEnchantCooldownModifier(this.getBaseCooldown(), stack);
        if(distanceBasedCooldownReduction > 0.0) {
            int blocksPerPercentReduced = getBlocksPerBonusReducedBy1Percent();
            double lowestCooldownPossible = (double) cooldown*(1.0-distanceBasedCooldownReduction);
            pTooltipComponents.add(Component.literal("Cooldown is reduced by as much as §a" + distanceBasedCooldownReduction*100.0 + "%§r when close to home."));
            pTooltipComponents.add(Component.literal("This bonus is reduced by §a1%§r for every " + blocksPerPercentReduced + " blocks away from home."));
            pTooltipComponents.add(Component.literal("Traveling across dimensions doubles this penalty."));
            pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime((int)lowestCooldownPossible) + " (less than " + blocksPerPercentReduced + " blocks) to " + HomeboundUtil.formatTime(cooldown) + " (over " + (int)(distanceBasedCooldownReduction*100.0*blocksPerPercentReduced) + " blocks)§r"));
        } else {
            pTooltipComponents.add(Component.literal("§aCooldown: " + HomeboundUtil.formatTime(cooldown) + "§r"));
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.literal("Crouch and use the item on a block to set your home."));
            addCooldownHoverText(pTooltipComponents, pStack);

            pTooltipComponents.add(Component.literal("§aCast Time: " + this.getActivationDuration(pStack) / 20.0 + " seconds.§r"));

            int maxDistance = getMaxDistance();
            if(maxDistance > 0) pTooltipComponents.add(Component.literal("§aMax Warp Distance: " + maxDistance + " blocks§r"));

            boolean canDimTravel = canDimTravel();
            pTooltipComponents.add(Component.literal("§aDimensional Travel: " + (canDimTravel ? "Yes" : "§cNo§r") + "§r"));
            if(this.isSoulbound()) pTooltipComponents.add(Component.literal("§5This item persists death.§r"));
        } else {
            pTooltipComponents.add(Component.literal("Find your way home. Press §eSHIFT§r for more info."));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean isSoulbound() {
        return this.soulbound;
    }

    public static class Properties {
        Rarity rarity = Rarity.COMMON;
        boolean soulbound = false;
        boolean isEnchantable = true;
        int enchantability = 15;
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