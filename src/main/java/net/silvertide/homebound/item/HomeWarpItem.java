package net.silvertide.homebound.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.silvertide.homebound.config.Config;
import net.silvertide.homebound.events.custom.StartWarpEvent;
import net.silvertide.homebound.util.*;

import java.util.List;

public class HomeWarpItem extends Item implements ISoulboundItem, IWarpItem {
    protected HomeWarpItemId id;
    private final boolean soulbound;
    private final boolean isEnchantable;
    private final int enchantability;

    public HomeWarpItem(HomeWarpItemId id, Properties properties) {
        super(new Item.Properties().stacksTo(1).rarity(properties.rarity));
        this.id = id;
        this.soulbound = properties.soulbound;
        this.isEnchantable = properties.isEnchantable;
        this.enchantability = properties.enchantability;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);

        if (player instanceof ServerPlayer serverPlayer) {
            if(player.isCrouching()) {
                HomeManager homeManager = HomeManager.get();
                if(!homeManager.isPlayerBindingHome(serverPlayer)) {
                    if(!homeManager.startBindingHome(serverPlayer)) {
                        return InteractionResultHolder.fail(stack);
                    }

                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.consume(stack);
                }
            } else {
                WarpManager warpManager = WarpManager.get();
                if(!warpManager.isPlayerWarping(serverPlayer) && !NeoForge.EVENT_BUS.post(new StartWarpEvent(player, this)).isCanceled()) {

                    if(!warpManager.startWarping(serverPlayer, stack)) {
                        return InteractionResultHolder.fail(stack);
                    };

                    player.startUsingItem(pUsedHand);
                    return InteractionResultHolder.consume(stack);
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if(entity instanceof ServerPlayer serverPlayer) {
            WarpManager warpManager = WarpManager.get();
            if(warpManager.isPlayerWarping(serverPlayer)) {
                warpManager.cancelWarp(serverPlayer);
            }
            HomeManager homeManager = HomeManager.get();
            if(homeManager.isPlayerBindingHome(serverPlayer)) {
                homeManager.cancelBindHome(serverPlayer);
            }

            WarpAttachmentUtil.getWarpAttachment(serverPlayer).ifPresent(warpAttachment -> {
                long gameTime = serverPlayer.level().getGameTime();
                if(!warpAttachment.hasCooldown(gameTime)) {
                    WarpAttachmentUtil.setWarpAttachment(serverPlayer, warpAttachment.withAddedCooldown(1, gameTime));
                }
            });
        }
    }

    public int getWarpUseDuration(Level level, ItemStack stack) {
        int useDuration = getBaseUseDurationInTicks();
        int channelHasteLevel = EnchantmentUtil.getServerChannelHasteLevel(stack, level);
        return EnchantmentUtil.applyHasteModifier(useDuration, channelHasteLevel);
    }


    private int getClientWarpUseDuration(ItemStack stack) {
        int useDuration = getBaseUseDurationInTicks();
        int channelHasteLevel = EnchantmentUtil.getClientChannelHasteLevel(stack);
        return EnchantmentUtil.applyHasteModifier(useDuration, channelHasteLevel);
    }

    public int getWarpCooldown(ServerPlayer player, ItemStack stack) {
        int baseCooldown = getBaseCooldown();
        int cooldownLevel = EnchantmentUtil.getServerCooldownReductionLevel(stack, player.level());
        int modifiedCooldownFromDistance = HomeboundUtil.applyDistanceCooldownModifier(this, player, baseCooldown);
        return EnchantmentUtil.applyCooldownReductionModifier(modifiedCooldownFromDistance, cooldownLevel);
    }

    public boolean isConsumedOnUse() {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
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
        int cooldown = EnchantmentUtil.applyCooldownReductionModifier(getBaseCooldown(), EnchantmentUtil.getClientCooldownReductionLevel(stack));
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.literal("Crouch and use the item to set your home."));
            addCooldownHoverText(tooltipComponents, stack);

            tooltipComponents.add(Component.literal("§aCast Time: " + getClientWarpUseDuration(stack) / 20.0 + " seconds.§r"));

            int maxDistance = getMaxDistance();
            if(maxDistance > 0) tooltipComponents.add(Component.literal("§aMax Distance: " + maxDistance + " blocks§r"));

            boolean canDimTravel = canDimTravel();
            tooltipComponents.add(Component.literal("§aDimensional Travel: " + (canDimTravel ? "Yes" : "§cNo§r") + "§r"));
            if(this.isSoulbound()) tooltipComponents.add(Component.literal("§5This item persists death.§r"));
        } else {
            tooltipComponents.add(Component.literal("Find your way home. Press §eSHIFT§r for more info."));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isSoulbound() {
        return this.soulbound;
    }

    public int getBaseCooldown() {
        int cooldownInMinutes = switch(id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_COOLDOWN.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_COOLDOWN.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_COOLDOWN.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_COOLDOWN.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_COOLDOWN.get();
            case DAWN_STONE -> Config.DAWN_STONE_COOLDOWN.get();
            case SUN_STONE -> Config.SUN_STONE_COOLDOWN.get();
            case DUSK_STONE -> Config.DUSK_STONE_COOLDOWN.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_COOLDOWN.get();
        };
        // We multiply by 60 because we want this value in seconds.
        return cooldownInMinutes*60;
    }

    public double getDistanceBasedCooldownReduction() {
        return switch(id) {
            case DUSK_STONE -> Config.DUSK_STONE_MAX_DISTANCE_REDUCTION.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_MAX_DISTANCE_REDUCTION.get();
            default -> 0.0;
        };
    }

    public int getBlocksPerBonusReducedBy1Percent() {
        return switch(id) {
            case DUSK_STONE -> Config.DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT.get();
            default -> 0;
        };
    }

    public int getBaseUseDurationInTicks() {
        int useDurationInSeconds = switch(id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_USE_TIME.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_USE_TIME.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_USE_TIME.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_USE_TIME.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_USE_TIME.get();
            case DAWN_STONE -> Config.DAWN_STONE_USE_TIME.get();
            case SUN_STONE -> Config.SUN_STONE_USE_TIME.get();
            case DUSK_STONE -> Config.DUSK_STONE_USE_TIME.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_USE_TIME.get();
        };
        return useDurationInSeconds*HomeboundUtil.TICKS_PER_SECOND;
    }

    public boolean canUseCuriosSlot() {
        return switch(id) {
            case HOMEWARD_BONE -> false;
            case HEARTHWOOD -> true;
            case HOMEWARD_GEM -> true;
            case HOMEWARD_STONE -> true;
            case HAVEN_STONE -> true;
            case DAWN_STONE -> true;
            case SUN_STONE -> true;
            case DUSK_STONE -> true;
            case TWILIGHT_STONE -> true;
        };
    }


    public boolean canDimTravel() {
        return switch (id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_DIMENSIONAL_TRAVEL.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_DIMENSIONAL_TRAVEL.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_DIMENSIONAL_TRAVEL.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_DIMENSIONAL_TRAVEL.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_DIMENSIONAL_TRAVEL.get();
            case DAWN_STONE -> Config.DAWN_STONE_DIMENSIONAL_TRAVEL.get();
            case SUN_STONE -> Config.SUN_STONE_DIMENSIONAL_TRAVEL.get();
            case DUSK_STONE -> Config.DUSK_STONE_DIMENSIONAL_TRAVEL.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_DIMENSIONAL_TRAVEL.get();
        };
    }

    public int getMaxDistance() {
        return switch (id) {
            case HOMEWARD_BONE -> Config.HOMEWARD_BONE_MAX_DISTANCE.get();
            case HEARTHWOOD -> Config.HEARTHWOOD_MAX_DISTANCE.get();
            case HOMEWARD_GEM -> Config.HOMEWARD_GEM_MAX_DISTANCE.get();
            case HOMEWARD_STONE -> Config.HOMEWARD_STONE_MAX_DISTANCE.get();
            case HAVEN_STONE -> Config.HAVEN_STONE_MAX_DISTANCE.get();
            case DAWN_STONE -> Config.DAWN_STONE_MAX_DISTANCE.get();
            case SUN_STONE -> Config.SUN_STONE_MAX_DISTANCE.get();
            case DUSK_STONE -> Config.DUSK_STONE_MAX_DISTANCE.get();
            case TWILIGHT_STONE -> Config.TWILIGHT_STONE_MAX_DISTANCE.get();
        };
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