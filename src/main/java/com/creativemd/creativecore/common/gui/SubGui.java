package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.GuiControlPacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.n247s.api.eventapi.eventsystem.EventBus;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SubGui {
	
	public static RenderItem itemRender;
	
	public EventBus eventBus;
	
	public SubContainer container;
	
	public int width;
	public int height;
    
    public SubGui() {
		this(176, 166);
	}
    
    public SubGui(int width, int height) {
		this.width = width;
		this.height = height;
		eventBus = new EventBus();
		eventBus.RegisterEventListner(this);
	}
    
	//public String title;
    
    public ArrayList<GuiControl> controls = new ArrayList<GuiControl>();
	
	public abstract void createControls();
	
	public abstract void drawForeground(FontRenderer fontRenderer);
	
	public void drawBackground(){}
	
	public void onGuiClose(){
		eventBus.removeEventListner(this);
	}
	
	public void moveControlAbove(GuiControl control, GuiControl controlInBack)
	{
		if(controls.contains(controlInBack) && controls.remove(control) && controls.indexOf(controlInBack)+1 < controls.size())
			controls.add(controls.indexOf(controlInBack)+1, control);
		else
			moveControlToTop(control);			
	}
	
	public void moveControlBehind(GuiControl control, GuiControl controlInFront)
	{
		if(controls.contains(controlInFront) && controls.remove(control))
			controls.add(controls.indexOf(controlInFront), control);
	}
	
	public void moveControlToBottom(GuiControl control)
	{
		if(controls.remove(control))
			controls.add(1, control);
	}
	
	public void moveControlToTop(GuiControl control)
	{
		if(controls.remove(control))
			controls.add(control);
	}
	
	//public void onMouseDragged(GuiControl control){}
	
	//public void onMouseReleased(GuiControl control){}
	
	public void readFromOpeningNBT(NBTTagCompound nbt){}
	
	public void readFromNBT(NBTTagCompound nbt){}
	
	public void sendPacketToServer(int controlID, NBTTagCompound nbt)
	{
		PacketHandler.sendPacketToServer(new GuiControlPacket(controlID, nbt));
	}
}
