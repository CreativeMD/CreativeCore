package com.creativemd.creativecore.core;

import com.creativemd.creativecore.client.rendering.model.CreativeCustomModelLoader;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
