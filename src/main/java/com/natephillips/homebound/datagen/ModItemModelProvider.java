package com.natephillips.homebound.datagen;

import com.natephillips.homebound.Homebound;
import com.natephillips.homebound.block.ModBlocks;
import com.natephillips.homebound.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Homebound.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.HOMEBOUND_GEM);
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        fromBaseItem(block, baseBlock, "block/button_inventory", "texture");
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        fromBaseItem(block, baseBlock, "block/fence_inventory", "texture");
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        fromBaseItem(block, baseBlock, "block/wall_inventory", "wall");
    }


    public void fromBaseItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock, String mcLoc, String texture) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc(mcLoc))
                .texture(texture, new ResourceLocation(Homebound.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(Homebound.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(Homebound.MOD_ID, "item/" + item.getId().getPath()));
    }
}
