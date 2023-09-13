package net.silvertide.homebound.datagen;

import net.minecraft.tags.ItemTags;
import net.silvertide.homebound.registry.ItemRegistry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_BONE.get())
                .define('B', Ingredient.of(Items.BONE))
                .define('P', Ingredient.of(Items.ENDER_PEARL))
                .pattern(" B ")
                .pattern("BPB")
                .pattern(" B ")
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_SHARD.get())
                .define('P', Ingredient.of(Items.ENDER_PEARL))
                .define('A', Ingredient.of(Items.AMETHYST_SHARD))
                .pattern("   ")
                .pattern("APA")
                .pattern("   ")
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_GEM.get())
                .define('H', ItemRegistry.HOMEWARD_SHARD.get())
                .pattern(" HH")
                .pattern(" HH")
                .pattern("   ")
                .unlockedBy("has_homeward_shard", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HOMEWARD_SHARD.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HEARTHWOOD.get())
                .define('H', ItemRegistry.HOMEWARD_GEM.get())
                .define('L', ItemTags.LOGS)
                .pattern("LLL")
                .pattern("LHL")
                .pattern("LLL")
                .unlockedBy("has_homeward_gem", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HOMEWARD_GEM.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_STONE.get())
                .define('H', ItemRegistry.HOMEWARD_GEM.get())
                .define('S', Ingredient.of(Items.SMOOTH_STONE))
                .define('D', Ingredient.of(Items.DIAMOND))
                .pattern("SDS")
                .pattern("DHD")
                .pattern("SDS")
                .unlockedBy("has_homeward_gem", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HOMEWARD_GEM.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HAVEN_STONE.get())
                .define('H', ItemRegistry.HOMEWARD_STONE.get())
                .define('B', Ingredient.of(Items.NETHER_BRICK))
                .define('N', Ingredient.of(Items.NETHERITE_INGOT))
                .pattern("BNB")
                .pattern("NHN")
                .pattern("BNB")
                .unlockedBy("has_homeward_stone", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HOMEWARD_STONE.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.DAWN_STONE.get())
                .define('H', ItemRegistry.HAVEN_STONE.get())
                .define('S', Ingredient.of(Items.PRISMARINE_SHARD))
                .pattern("SSS")
                .pattern("SHS")
                .pattern("SSS")
                .unlockedBy("has_haven_stone", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HAVEN_STONE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SUN_STONE.get())
                .requires(ItemRegistry.DAWN_STONE.get())
                .requires(Ingredient.of(Items.NETHER_STAR))
                .unlockedBy("has_dawn_stone", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ItemRegistry.DAWN_STONE.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.DUSK_STONE.get())
                .define('H', ItemRegistry.HAVEN_STONE.get())
                .define('S', Ingredient.of(Items.ECHO_SHARD))
                .pattern("SSS")
                .pattern("SHS")
                .pattern("SSS")
                .unlockedBy("has_haven_stone", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HAVEN_STONE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.TWILIGHT_STONE.get())
                .requires(ItemRegistry.DUSK_STONE.get())
                .requires(Ingredient.of(Items.NETHER_STAR))
                .unlockedBy("has_dusk_stone", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ItemRegistry.DUSK_STONE.get()).build()))
                .save(pWriter);
    }
}
