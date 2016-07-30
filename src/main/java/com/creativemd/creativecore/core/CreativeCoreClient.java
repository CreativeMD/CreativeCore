package com.creativemd.creativecore.core;

import com.creativemd.creativecore.client.rendering.model.CreativeCustomModelLoader;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
}
