package net.silvertide.homebound.util;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.fml.ModList;
import net.silvertide.homebound.capabilities.IWarpCap;
import net.silvertide.homebound.capabilities.WarpPos;
import net.silvertide.homebound.compat.CuriosCompat;
import net.silvertide.homebound.item.IWarpItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;


public final class HomeboundUtil {
    private static final Random SOUND_RNG = new Random();
    private HomeboundUtil() {}
    public static final int TICKS_PER_SECOND = 20;

    public static boolean withinAnyStructuresBounds(ServerPlayer serverPlayer, List<String> structureRLs) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        // Loop through all structures found at the players position.
        for(Map.Entry<Structure, LongSet> structureEntry : serverLevel.structureManager().getAllStructuresAt(serverPlayer.blockPosition()).entrySet()) {
            // Determine if this structure is on the list of structures to check for.
            Registry<Structure> structureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
            ResourceLocation structureRL = structureRegistry.getKey(structureEntry.getKey());
            if(structureRL != null && structureRLs.contains(structureRL.toString())) {
                // Determine if player is within that structures bounds.
                if(withinStructureBounds(serverPlayer, structureEntry.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean withinStructureBounds(ServerPlayer serverPlayer, Structure structure) {
        BlockPos playerPos = serverPlayer.blockPosition();
        StructureStart structureStart = serverPlayer.serverLevel().structureManager().getStructureAt(playerPos, structure);
        if(structureStart != StructureStart.INVALID_START) {
            return structureStart.getBoundingBox().isInside(playerPos);
        }
        return false;
    }

    public static int applyDistanceCooldownModifier(IWarpItem warpItem, ServerPlayer player, int cooldown){
        double maxCooldownReduction = warpItem.getDistanceBasedCooldownReduction();

        if(maxCooldownReduction > 0.0) {
            int blocksPerPercentAdded = warpItem.getBlocksPerBonusReducedBy1Percent();

            IWarpCap playerWarpCap = CapabilityUtil.getWarpCapOrNull(player);
            if(playerWarpCap == null) return cooldown;

            WarpPos currentPos = CapabilityUtil.createWarpPosOnPlayer(player);

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

    public static Optional<ItemStack> findWarpInitiatiorItemStack(Player player) {
        Inventory playerInventory = player.getInventory();
        // check main or offhand

        // Check the currently selected item (if on the hotbar)
        int currentlySelectedSlotIndex = player.getInventory().selected;
        if (Inventory.isHotbarSlot(currentlySelectedSlotIndex) && player.getInventory().items.get(currentlySelectedSlotIndex).getItem() instanceof IWarpItem) {
            return Optional.of(player.getInventory().items.get(currentlySelectedSlotIndex));
        }

        if(playerInventory.offhand.get(0).getItem() instanceof IWarpItem) {
            return Optional.of(playerInventory.offhand.get(0));
        }

        if (ModList.get().isLoaded("curios")) {
            Optional<ItemStack> curiosWarpItemStack = CuriosCompat.findCuriosWarpItemStack(player);
            if(curiosWarpItemStack.isPresent()) {
                return curiosWarpItemStack;
            }
        }

        for (int i = 0; i < playerInventory.items.size(); i++) {
            ItemStack stack = playerInventory.items.get(i);
            if(stack.getItem() instanceof IWarpItem){
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    public static void spawnParticals(ServerLevel serverLevel, Player player, ParticleOptions particle, int numParticles){
        Level level = player.level();
        for(int i = 0; i < numParticles; i++){
            serverLevel.sendParticles(particle, player.getX() + level.random.nextDouble() - 0.5, player.getY() + 1.0, player.getZ() + level.random.nextDouble() - 0.5, 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }
    }

    public static void displayClientMessage(Player player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }

    public static void sendSystemMessage(Player player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }

    public static void playSound(Level level, double x, double y, double z, SoundEvent soundEvent){
        level.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
    }

    public static void playSound(Level level, Player player, SoundEvent soundEvent){
        playSound(level, player.getX(), player.getY(), player.getZ(), soundEvent);
    }

    public static String getCooldownMessage(int cooldownRemaining) {
        return "§cYou haven't recovered. [" + HomeboundUtil.formatTime(cooldownRemaining) + "]§r";
    }
    private static String getDimensionMessage() {
        return "§cCan't warp between dimensions.§r";
    }

    public static String getDistanceMessage(int maxDistance, int distance) {
        return "§cToo far from home. [" + distance + " / " + maxDistance + "]§r";
    }

    public static String formatDimension(String dimString) {
        int indexOfColon = dimString.indexOf(":");
        if (indexOfColon != -1) {
            String dimName = dimString.substring(indexOfColon + 1);
            String[] dimWords = dimName.split("_");

            for(int i = 0; i < dimWords.length; i++) {
                dimWords[i] = dimWords[i].substring(0, 1).toUpperCase() + dimWords[i].substring(1).toLowerCase();
            }
            return String.join(" ", dimWords);
        } else {
            return dimString;
        }
    }

    public static String formatTime(int seconds) {
        if (seconds < 0) {
            return "Invalid input"; // Handle negative input if needed
        }

        int hours = seconds / 3600;
        int remainingSeconds = seconds % 3600;
        int minutes = remainingSeconds / 60;
        int remainingSecs = remainingSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSecs);
    }

    // This was taken from Iron's Spells and Spellbooks Utils
    public static String timeFromTicks(float ticks, int decimalPlaces) {
        float ticks_to_seconds = 20;
        float seconds_to_minutes = 60;
        String affix = "s";
        float time = ticks / ticks_to_seconds;
        if (time > seconds_to_minutes) {
            time /= seconds_to_minutes;
            affix = "m";
        }
        return stringTruncation(time, decimalPlaces) + affix;
    }

    // This was taken from Iron's Spells and Spellbooks Utils
    public static String stringTruncation(double f, int decimalPlaces) {
        if (f == Math.floor(f)) {
            return Integer.toString((int) f);
        }

        double multiplier = Math.pow(10, decimalPlaces);
        double truncatedValue = Math.floor(f * multiplier) / multiplier;

        // Convert the truncated value to a string
        String result = Double.toString(truncatedValue);

        // Remove trailing zeros
        result = result.replaceAll("0*$", "");

        // Remove the decimal point if there are no decimal places
        result = result.endsWith(".") ? result.substring(0, result.length() - 1) : result;

        return result;
    }

}