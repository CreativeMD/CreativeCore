package com.creativemd.creativecore.client.rendering.model;

import java.util.HashMap;

import com.creativemd.creativecore.core.CreativeCoreClient;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreativeBlockRenderHelper {
	
	public static ItemMeshDefinition mesh = new ItemMeshDefinition() {
		
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return new ModelResourceLocation(stack.getItem().getRegistryName().toString(), "inventory");
		}
	};
	
	public static HashMap<ResourceLocation, Block> blocks = new HashMap<>();
	
	public static void registerCreativeRenderedBlock(Block block)
	{
		blocks.put(block.getRegistryName(), block);
		registerBlockItem(block);
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			registerCreativeRenderedBlockClient(block);
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerCreativeRenderedBlockClient(Block block)
	{
		CreativeCoreClient.registerBlockColorHandler(block);
	}
	
	private static void registerBlockItem(Block toRegister){
		Item item = Item.getItemFromBlock(toRegister);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, mesh);
	}
	
}
