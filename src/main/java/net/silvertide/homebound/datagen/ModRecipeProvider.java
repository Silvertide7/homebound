package net.silvertide.homebound.datagen;

import net.minecraft.tags.ItemTags;
import net.silvertide.homebound.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_BONE.get())
                .define('B', Ingredient.of(Items.BONE))
                .define('P', Ingredient.of(Items.ENDER_PEARL))
                .pattern(" B ")
                .pattern("BPB")
                .pattern(" B ")
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_SHARD.get())
                .define('P', Ingredient.of(Items.ENDER_PEARL))
                .define('A', Ingredient.of(Items.AMETHYST_SHARD))
                .pattern("   ")
                .pattern("APA")
                .pattern("   ")
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_GEM.get())
                .define('H', ItemRegistry.HOMEWARD_SHARD.get())
                .pattern(" HH")
                .pattern(" HH")
                .pattern("   ")
                .unlockedBy("has_homeward_shard", has(ItemRegistry.HOMEWARD_SHARD.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HEARTHWOOD.get())
                .define('H', ItemRegistry.HOMEWARD_GEM.get())
                .define('L', ItemTags.LOGS)
                .pattern("LLL")
                .pattern("LHL")
                .pattern("LLL")
                .unlockedBy("has_homeward_gem", has(ItemRegistry.HOMEWARD_GEM.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_STONE.get())
                .define('H', ItemRegistry.HOMEWARD_GEM.get())
                .define('S', Ingredient.of(Items.SMOOTH_STONE))
                .define('D', Ingredient.of(Items.DIAMOND))
                .pattern("SDS")
                .pattern("DHD")
                .pattern("SDS")
                .unlockedBy("has_homeward_gem", has(ItemRegistry.HOMEWARD_GEM.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HAVEN_STONE.get())
                .define('H', ItemRegistry.HOMEWARD_STONE.get())
                .define('B', Ingredient.of(Items.NETHER_BRICK))
                .define('N', Ingredient.of(Items.NETHERITE_INGOT))
                .pattern("BBB")
                .pattern("NHN")
                .pattern("BBB")
                .unlockedBy("has_homeward_stone", has(ItemRegistry.HOMEWARD_STONE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.DAWN_STONE.get())
                .define('H', ItemRegistry.HAVEN_STONE.get())
                .define('S', Ingredient.of(Items.PRISMARINE_SHARD))
                .pattern("SSS")
                .pattern("SHS")
                .pattern("SSS")
                .unlockedBy("has_haven_stone", has(ItemRegistry.HAVEN_STONE.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SUN_STONE.get())
                .requires(ItemRegistry.DAWN_STONE.get())
                .requires(Ingredient.of(Items.NETHER_STAR))
                .unlockedBy("has_dawn_stone", has(ItemRegistry.DAWN_STONE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.DUSK_STONE.get())
                .define('H', ItemRegistry.HAVEN_STONE.get())
                .define('S', Ingredient.of(Items.ECHO_SHARD))
                .pattern("SSS")
                .pattern("SHS")
                .pattern("SSS")
                .unlockedBy("has_haven_stone", has(ItemRegistry.HAVEN_STONE.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.TWILIGHT_STONE.get())
                .requires(ItemRegistry.DUSK_STONE.get())
                .requires(Ingredient.of(Items.NETHER_STAR))
                .unlockedBy("has_dusk_stone", has(ItemRegistry.DUSK_STONE.get()))
                .save(recipeOutput);
    }
}
