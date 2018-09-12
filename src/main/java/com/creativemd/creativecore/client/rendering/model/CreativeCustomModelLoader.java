package com.creativemd.creativecore.client.rendering.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class CreativeCustomModelLoader implements ICustomModelLoader {// , ItemMeshDefinition{

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		for (ResourceLocation location : CreativeBlockRenderHelper.items.keySet()) {
			if (location.getResourceDomain().equals(modelLocation.getResourceDomain()) && location.getResourcePath().equals(modelLocation.getResourcePath()))
				return true;
		}
		for (ResourceLocation location : CreativeBlockRenderHelper.blocks.keySet()) {
			if (location.getResourceDomain().equals(modelLocation.getResourceDomain()) && location.getResourcePath().equals(modelLocation.getResourcePath()))
				return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		return CreativeModel.instance;
		// return ModelLoaderRegistry.getModel(new ResourceLocation("minecraft",
		// "block"));
	}

	/*
	 * @Override public ModelResourceLocation getModelLocation(ItemStack stack) {
	 * for (ResourceLocation location : CreativeBlockRenderHelper.blocks.keySet()) {
	 * if(location.getResourceDomain().equals(modelLocation.getResourceDomain()) &&
	 * location.getResourcePath().equals(modelLocation.getResourcePath())) return
	 * true; return; }
	 */

}
