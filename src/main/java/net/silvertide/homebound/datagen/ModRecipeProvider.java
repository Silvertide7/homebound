package net.silvertide.homebound.datagen;

import net.minecraft.tags.ItemTags;
import net.silvertide.homebound.Homebound;
import net.silvertide.homebound.item.ItemRegistry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
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
                .pattern("AAA")
                .pattern("APA")
                .pattern("AAA")
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HOMEWARD_GEM.get())
                .define('H', Ingredient.of(ItemRegistry.HOMEWARD_SHARD.get()))
                .define('E', Ingredient.of(Items.EMERALD))
                .pattern("EHE")
                .pattern("EHE")
                .pattern("   ")
                .unlockedBy("has_homeward_shard", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HOMEWARD_SHARD.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HEARTHWOOD.get())
                .define('H', Ingredient.of(ItemRegistry.HOMEWARD_SHARD.get()))
                .define('L', ItemTags.LOGS)
                .pattern("LLL")
                .pattern("LHL")
                .pattern("LLL")
                .unlockedBy("has_homeward_shard", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.HOMEWARD_SHARD.get()).build()))
                .save(pWriter);
    }
}
