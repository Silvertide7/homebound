package net.silvertide.homebound.config;

import net.minecraft.util.StringUtil;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec SERVER_CONFIG;

    public static final ForgeConfigSpec.ConfigValue<Integer> HURT_COOLDOWN_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> BIND_HOME_USE_DURATION;
    public static final ForgeConfigSpec.ConfigValue<Integer> BIND_HOME_COOLDOWN_DURATION;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CANT_BIND_HOME_ON_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> HOME_DIMENSION_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TELEPORT_DIMENSION_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TELEPORT_STRUCTURE_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<Integer> MINIMUM_MOB_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> CHANNEL_SLOW_PERCENTAGE;

    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_BONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_BONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_BONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOMEWARD_BONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HEARTHWOOD_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HEARTHWOOD_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HEARTHWOOD_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HEARTHWOOD_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_GEM_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_GEM_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_GEM_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOMEWARD_GEM_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HOMEWARD_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOMEWARD_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> HAVEN_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> HAVEN_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> HAVEN_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HAVEN_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> DAWN_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> DAWN_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> DAWN_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DAWN_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SUN_STONE_DIMENSIONAL_TRAVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DUSK_STONE_DIMENSIONAL_TRAVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<Double> DUSK_STONE_MAX_DISTANCE_REDUCTION;

    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_USE_TIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TWILIGHT_STONE_DIMENSIONAL_TRAVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<Double> TWILIGHT_STONE_MAX_DISTANCE_REDUCTION;

    static {
        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("General");
        BUILDER.comment("When you take damage while using a homebound item to teleport home the teleport is canceled and a cooldown is added before you can use it again.");
        BUILDER.comment("This is to prevent players from easily teleporting out of a dangerous situation.");
        BUILDER.comment("This value is how long of a cooldown in seconds is added when damage is taken. Set to 0 to disable taking damage from canceling the teleport");
        HURT_COOLDOWN_TIME = BUILDER.defineInRange("Hurt Cooldown Time", 5, 0, Integer.MAX_VALUE);

        BUILDER.comment("How long it takes (in seconds) to bind your home to a location when you are crouching and using a homebound stone.");
        BIND_HOME_USE_DURATION = BUILDER.defineInRange("Bind Home Use Duration", 3, 0, Integer.MAX_VALUE);

        BUILDER.comment("Cooldown duration triggered when settings your home in seconds. If 0 then setting your home does not put your teleport on cooldown.");
        BUILDER.comment("If set to 3600 then this will add a 1 hour cooldown to your teleport whenever you set your home. This is cumulative to your current teleport cooldown.");
        BUILDER.comment("This means if your teleport is already on a 30 minute cooldown and the value is 3600, your new cooldown is 1 hour 30 minutes.");
        BUILDER.comment("If you don't have the Cant Bind Home On Cooldown option turned on then you could potentially accrue huge cooldown timers.");
        BIND_HOME_COOLDOWN_DURATION = BUILDER.defineInRange("Bind Home Cooldown Duration", 0, 0, Integer.MAX_VALUE);

        BUILDER.comment("If you can bind a home while your warp is on cooldown.");
        BUILDER.comment("If true then you can only bind your home while your teleport is on cooldown. If false then you can set your home anytime.");
        CANT_BIND_HOME_ON_COOLDOWN = BUILDER.define("Cant Bind Home On Cooldown", false);

        BUILDER.comment("Adding a dimension to this list will prevent you from setting your home while in this dimension.");
        BUILDER.comment("If you wanted to prevent players from setting their home in the end you would add minecraft:the_end");
        HOME_DIMENSION_BLACKLIST = BUILDER.defineListAllowEmpty("Home Dimension Blacklist", new ArrayList<>(), val -> (val instanceof String stringVal && !StringUtil.isNullOrEmpty(stringVal)) && stringVal.contains(":"));

        BUILDER.comment("Adding a dimension to this list will prevent you from using your homebound stone while in the dimension, as well as setting your home there.");
        BUILDER.comment("If you wanted to prevent players from using homebound stones and setting their home in the end you would add minecraft:the_end");
        TELEPORT_DIMENSION_BLACKLIST = BUILDER.defineListAllowEmpty("Teleport Dimension Blacklist", new ArrayList<>(), val -> (val instanceof String stringVal && !StringUtil.isNullOrEmpty(stringVal)) && stringVal.contains(":"));

        BUILDER.comment("Adding a dimension to this list will prevent you from using your homebound stone while in the structure, as well as setting your home there.");
        BUILDER.comment("If you wanted to prevent players from using homebound stones and setting their home in a bastion you would add minecraft:bastion");
        TELEPORT_STRUCTURE_BLACKLIST = BUILDER.defineListAllowEmpty("Teleport Structure Blacklist", new ArrayList<>(), val -> (val instanceof String stringVal && !StringUtil.isNullOrEmpty(stringVal)) && stringVal.contains(":"));

        BUILDER.comment("How far away you must be from the nearest hostile mob in order to begin teleporting.");
        BUILDER.comment("I set the max to 32 blocks (2 chunks) for performance reasons.");
        MINIMUM_MOB_DISTANCE = BUILDER.defineInRange("Minimum Mob Distance", 0, 0, 32);

        BUILDER.comment("Slows the player by this amount when using a homebound stone. 1.0 means completely stopped, 0.0 means no slow applied.");
        CHANNEL_SLOW_PERCENTAGE = BUILDER.defineInRange("Channel Slow Percentage", 0.7, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.push("Homeward Item Cooldowns");
        BUILDER.comment("All cooldowns are in minutes. All enchantments or cooldown reducing effects will apply to this base cooldown.");
        BUILDER.comment("All use times are in seconds. This is the length of time it takes to activate the stone.");
        BUILDER.comment("All max distances are in blocks. 16 blocks per chunk, so 160 blocks is 10 chunks. Having a value of 0 means no max distance.");
        BUILDER.comment("Dimensional travel determines if the item can teleport you home from another dimension or not.");
        BUILDER.comment("Blocks per bonus reduced means the number of blocks it takes to reduce the cooldown reduction effect by 1%. If this is set to 50 and the maximum distance reduction multiplier is 0.4 (or 40% cooldown reduction) and you are 51 blocks away from home, the cooldown reduction effect is now 0.39 (39%).");
        BUILDER.comment("If this is set to 50 and the maximum distance reduction multiplier is 0.4 (or 40% cooldown reduction) and you are 51 blocks away from home, the cooldown reduction effect is now 0.39 (39%).");
        BUILDER.comment("This will reduce by 1% for every 50 blocks away from home until the bonus is no longer applied at 0%. In the example above that would be 2000 blocks.");

        BUILDER.comment("--- Homeward Bone ---");
        HOMEWARD_BONE_COOLDOWN = BUILDER.defineInRange("Homeward Bone Cooldown", 120, 0, Integer.MAX_VALUE);
        HOMEWARD_BONE_USE_TIME = BUILDER.defineInRange("Homeward Bone Use Time", 15, 0, Integer.MAX_VALUE);
        HOMEWARD_BONE_MAX_DISTANCE = BUILDER.defineInRange("Homeward Bone Max Distance", 0, 0, Integer.MAX_VALUE);
        HOMEWARD_BONE_DIMENSIONAL_TRAVEL = BUILDER.define("Homeward Bone Dimensional travel allowed", true);

        BUILDER.comment("--- Hearthwood ---");
        HEARTHWOOD_COOLDOWN = BUILDER.defineInRange("Hearthwood Cooldown", 10, 0, Integer.MAX_VALUE);
        HEARTHWOOD_USE_TIME = BUILDER.defineInRange("Hearthwood Use Time", 3, 0, Integer.MAX_VALUE);
        HEARTHWOOD_MAX_DISTANCE = BUILDER.defineInRange("Hearthwood Max Distance", 160, 0, Integer.MAX_VALUE);
        HEARTHWOOD_DIMENSIONAL_TRAVEL = BUILDER.define("Hearthwood Dimensional travel allowed", false);

        BUILDER.comment("--- Homeward Gem ---");
        HOMEWARD_GEM_COOLDOWN = BUILDER.defineInRange("Homeward Gem Cooldown", 60, 0, Integer.MAX_VALUE);
        HOMEWARD_GEM_USE_TIME = BUILDER.defineInRange("Homeward Gem Use Time", 12, 0, Integer.MAX_VALUE);
        HOMEWARD_GEM_MAX_DISTANCE = BUILDER.defineInRange("Homeward Gem Max Distance", 600, 0, Integer.MAX_VALUE);
        HOMEWARD_GEM_DIMENSIONAL_TRAVEL = BUILDER.define("Homeward Gem Dimensional travel allowed", false);

        BUILDER.comment("--- Homeward Stone ---");
        HOMEWARD_STONE_COOLDOWN = BUILDER.defineInRange("Homeward Stone Cooldown", 60, 0, Integer.MAX_VALUE);
        HOMEWARD_STONE_USE_TIME = BUILDER.defineInRange("Homeward Stone Use Time", 10, 0, Integer.MAX_VALUE);
        HOMEWARD_STONE_MAX_DISTANCE = BUILDER.defineInRange("Homeward Stone Max Distance", 0, 0, Integer.MAX_VALUE);
        HOMEWARD_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Homeward Stone Dimensional travel allowed", false);

        BUILDER.comment("--- Haven Stone ---");
        HAVEN_STONE_COOLDOWN = BUILDER.defineInRange("Haven Stone Cooldown", 60, 0, Integer.MAX_VALUE);
        HAVEN_STONE_USE_TIME = BUILDER.defineInRange("Haven Stone Use Time", 10, 0, Integer.MAX_VALUE);
        HAVEN_STONE_MAX_DISTANCE = BUILDER.defineInRange("Haven Stone Max Distance", 0, 0, Integer.MAX_VALUE);
        HAVEN_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Haven Stone Dimensional travel allowed", true);

        BUILDER.comment("--- Dawn Stone ---");
        DAWN_STONE_COOLDOWN = BUILDER.defineInRange("Dawn Stone Cooldown", 50, 0, Integer.MAX_VALUE);
        DAWN_STONE_USE_TIME = BUILDER.defineInRange("Dawn Stone Use Time", 9, 0, Integer.MAX_VALUE);
        DAWN_STONE_MAX_DISTANCE = BUILDER.defineInRange("Dawn Stone Max Distance", 0, 0, Integer.MAX_VALUE);
        DAWN_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Dawn Stone Dimensional travel allowed", true);

        BUILDER.comment("--- Sun Stone ---");
        SUN_STONE_COOLDOWN = BUILDER.defineInRange("Sun Stone Cooldown", 45, 0, Integer.MAX_VALUE);
        SUN_STONE_USE_TIME = BUILDER.defineInRange("Sun Stone Use Time", 8, 0, Integer.MAX_VALUE);
        SUN_STONE_MAX_DISTANCE = BUILDER.defineInRange("Sun Stone Max Distance", 0, 0, Integer.MAX_VALUE);
        SUN_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Sun Stone Dimensional travel allowed", true);

        BUILDER.comment("--- Dusk Stone ---");
        DUSK_STONE_COOLDOWN = BUILDER.defineInRange("Dusk Stone Cooldown", 60, 0, Integer.MAX_VALUE);
        DUSK_STONE_USE_TIME = BUILDER.defineInRange("Dusk Stone Use Time", 10, 0, Integer.MAX_VALUE);
        DUSK_STONE_MAX_DISTANCE = BUILDER.defineInRange("Dusk Stone Max Distance", 0, 0, Integer.MAX_VALUE);
        DUSK_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Dusk Stone Dimensional travel allowed", true);
        DUSK_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT = BUILDER.defineInRange("Dusk Stone Blocks Per Percent Reduced", 40, 0, Integer.MAX_VALUE);
        DUSK_STONE_MAX_DISTANCE_REDUCTION = BUILDER.defineInRange("Dusk Stone Maximum Distance Reduction Multiplier", 0.6, 0.0, 1.0);

        BUILDER.comment("--- Twilight Stone ---");
        TWILIGHT_STONE_COOLDOWN = BUILDER.defineInRange("Twilight Stone Cooldown", 60, 0 ,Integer.MAX_VALUE);
        TWILIGHT_STONE_USE_TIME = BUILDER.defineInRange("Twilight Stone Use Time", 10, 0, Integer.MAX_VALUE);
        TWILIGHT_STONE_MAX_DISTANCE = BUILDER.defineInRange("Twilight Stone Max Distance", 0, 0, Integer.MAX_VALUE);
        TWILIGHT_STONE_DIMENSIONAL_TRAVEL = BUILDER.define("Twilight Stone Dimensional travel allowed", true);
        TWILIGHT_STONE_BLOCKS_PER_BONUS_REDUCED_BY_ONE_PERCENT = BUILDER.defineInRange("Twilight Stone Blocks Per Percent Reduced", 40, 0, Integer.MAX_VALUE);
        TWILIGHT_STONE_MAX_DISTANCE_REDUCTION = BUILDER.defineInRange("Twilight Stone Maximum Distance Reduction Multiplier", 0.7, 0.0, 1.0);

        BUILDER.pop();

        SERVER_CONFIG = BUILDER.build();
    }
}
