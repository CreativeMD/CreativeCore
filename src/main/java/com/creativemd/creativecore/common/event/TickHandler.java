package com.creativemd.creativecore.common.event;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.multiblock.IMultiBlock;
import com.creativemd.creativecore.common.multiblock.MultiBlockStructure;
import com.n247s.api.eventapi.eventsystem.EventBus;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TickHandler {
	
	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onTick(TickEvent tick) //Remove all Structures which doesn't have any connections
	{
		if(tick.side == Side.SERVER && tick.phase == Phase.END && tick.type == tick.type.SERVER)
		{
			int i = 0;
			while(i < MultiBlockStructure.allstructures.size()){
				if(MultiBlockStructure.allstructures.get(i).connections.size() == 0)
					MultiBlockStructure.allstructures.remove(i);
				else if(!MultiBlockStructure.allstructures.get(i).isValid())
				{
					for (int j = 0; j < MultiBlockStructure.allstructures.get(i).connections.size(); j++) {
						((IMultiBlock) MultiBlockStructure.allstructures.get(i).connections).setStructure(null);
					}
					MultiBlockStructure.allstructures.remove(i);
				}
				else
					i++;
			}
		}
	}
	
	public static ArrayList<CreativeCoreEventBus> ServerEvents = new ArrayList<CreativeCoreEventBus>();
	
	@SubscribeEvent
	public void onEventTick(TickEvent tick) //Remove all Structures which doesn't have any connections
	{
		if(tick.phase == Phase.START && tick.type == Type.SERVER)
		{
			if(ServerEvents == null)
				ServerEvents = new ArrayList<CreativeCoreEventBus>();
			try{
				for (int i = 0; i < ServerEvents.size(); i++) {
					for (int j = 0; j < ServerEvents.get(i).eventsToRaise.size(); j++) {
						ServerEvents.get(i).raiseEvent(ServerEvents.get(i).eventsToRaise.get(j), true);
					}
					ServerEvents.get(i).eventsToRaise.clear();
				}
			}catch(Exception e){
				//It is ready to crash
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static int defaultScale;
	@SideOnly(Side.CLIENT)
	public static boolean changed;
	
	@SideOnly(Side.CLIENT)
	public static ArrayList<CreativeCoreEventBus> ClientEvents;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(RenderTickEvent tick)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(tick.phase == Phase.START)
		{
			if(ClientEvents == null)
				ClientEvents = new ArrayList<CreativeCoreEventBus>();
			try{
				for (int i = 0; i < ClientEvents.size(); i++) {
					for (int j = 0; j < ClientEvents.get(i).eventsToRaise.size(); j++) {
						ClientEvents.get(i).raiseEvent(ClientEvents.get(i).eventsToRaise.get(j), true);
					}
					ClientEvents.get(i).eventsToRaise.clear();
				}
			}catch(Exception e){
				//It is ready to crash
			}
		}
		
		if(mc.thePlayer != null && mc.thePlayer.openContainer instanceof ContainerSub && ((ContainerSub)mc.thePlayer.openContainer).gui != null)
		{
			if(tick.phase == Phase.START)
			{
				if(!changed)
					defaultScale = mc.gameSettings.guiScale;
				int maxScale = ((GuiContainerSub)((ContainerSub)mc.thePlayer.openContainer).gui).getMaxScale(mc);
				int scale = Math.min(defaultScale, maxScale);
				if(defaultScale == 0)
					scale = maxScale;
				if(scale != mc.gameSettings.guiScale)
				{
					changed = true;
					mc.gameSettings.guiScale = scale;
					ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		            int k = scaledresolution.getScaledWidth();
		            int l = scaledresolution.getScaledHeight();
		            mc.currentScreen.setWorldAndResolution(mc, k, l);
		            
		            //mc.loadingScreen = new LoadingScreenRenderer(mc);
		            //mc.updateFramebufferSize();
				}
			}
			
			//if(tick.phase == Phase.END)
			//{
				//mc.gameSettings.guiScale = defaultScale;
			//}
		}else if(tick.phase == Phase.START && changed){
			changed = false;
			mc.gameSettings.guiScale = defaultScale;
		}
	}
}
