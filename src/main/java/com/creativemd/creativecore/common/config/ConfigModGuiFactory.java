package com.creativemd.creativecore.common.config;

import java.util.Collections;
import java.util.Set;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.gui.SubGuiConfig;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.gui.mc.GuiScreenSub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.relauncher.Side;

public abstract class ConfigModGuiFactory implements IModGuiFactory {
	
	public abstract String modid();
	
	@Override
	public void initialize(Minecraft minecraftInstance) {
		
	}
	
	public ICreativeConfigHolder getHolder() {
		ICreativeConfigHolder holder = (ICreativeConfigHolder) CreativeConfigRegistry.ROOT.get(modid());
		if (holder != null && !holder.isEmpty(Side.CLIENT))
			return holder;
		return null;
	}
	
	@Override
	public boolean hasConfigGui() {
		return getHolder() != null;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		ICreativeConfigHolder holder = getHolder();
		if (holder != null) {
			CreativeCore.configHandler.load(modid(), Side.CLIENT);
			if (Minecraft.getMinecraft().player == null)
				CreativeCore.configHandler.loadClientFields();
			return new GuiScreenSub(parentScreen, new SubGuiConfig(holder, Side.CLIENT));
		}
		return null;
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.EMPTY_SET;
	}
	
}
