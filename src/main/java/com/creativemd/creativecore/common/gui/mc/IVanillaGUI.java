package com.creativemd.creativecore.common.gui.mc;

import java.util.List;

import com.creativemd.creativecore.common.gui.container.SubGui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IVanillaGUI {
	
	public List<SubGui> getLayers();
	
	public void removeLayer(SubGui layer);
	
	public void addLayer(SubGui layer);
	
	public int getMaxScale(Minecraft mc);
	
	public boolean hasTopLayer();
	
	public SubGui getTopLayer();
	
	public boolean isTopLayer(SubGui gui);
	
	public boolean isOpen(Class<? extends SubGui> clazz);
	
	public <T extends SubGui> T get(Class<T> clazz);
	
	public int getGuiLeft();
	
	public int getGuiTop();
	
	public void onLayerClosed();
	
	public void sendChatMessage(String msg);
}
