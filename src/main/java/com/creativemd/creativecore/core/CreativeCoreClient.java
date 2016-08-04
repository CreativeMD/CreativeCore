package com.creativemd.creativecore.core;

import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.client.rendering.model.CreativeCustomModelLoader;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreativeCoreClient {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static void doClientThings()
	{
		if(!mc.getFramebuffer().isStencilEnabled())
			mc.getFramebuffer().enableStencil();
		
		ModelLoaderRegistry.registerLoader(new CreativeCustomModelLoader());
	}
	
	public static ItemMeshDefinition mesh = new ItemMeshDefinition() {
		
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return new ModelResourceLocation(stack.getItem().getRegistryName(), "inventory");
		}
	};
	
	public static void registerBlockItem(Block toRegister){
		Item item = Item.getItemFromBlock(toRegister);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, mesh);
	}
	
	public static void registerItemRenderer(Item item)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static IItemColor itemColor = new IItemColor() {
		
		@Override
		public int getColorFromItemstack(ItemStack stack, int tintIndex) {
			return tintIndex;
		}
	};
	
	public static IBlockColor blockColor = new IBlockColor() {
		
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			if(CreativeBakedQuad.lastRenderedQuad != null && CreativeBakedQuad.lastRenderedQuad.cube != null && CreativeBakedQuad.lastRenderedQuad.cube.block != null)
			{
				IBlockState newState = CreativeBakedQuad.lastRenderedQuad.cube.getBlockState(CreativeBakedQuad.lastRenderedQuad.cube.block);
				return mc.getBlockColors().colorMultiplier(newState, worldIn, pos, tintIndex);
				//return ColorUtils.WHITE;
			}
			return tintIndex;
		}
	};
	
	public static void registerBlockColorHandler(Block block)
	{
		mc.getItemColors().registerItemColorHandler(itemColor, block);
		mc.getBlockColors().registerBlockColorHandler(blockColor, block);
	}
}
