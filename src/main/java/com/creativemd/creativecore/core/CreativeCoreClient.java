package com.creativemd.creativecore.core;

import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.client.rendering.model.CreativeCustomModelLoader;
import com.creativemd.creativecore.common.utils.ColorUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import scala.collection.parallel.ParIterableLike.Min;

@SideOnly(Side.CLIENT)
public class CreativeCoreClient {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static void doClientThings()
	{
		if(!mc.getFramebuffer().isStencilEnabled())
			mc.getFramebuffer().enableStencil();
		
		ModelLoaderRegistry.registerLoader(new CreativeCustomModelLoader());
		
		
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
