package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.GuiControlPacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

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
	
	public SubContainer container;
	
	public int width;
	public int height;
    
    public SubGui() {
		this(176, 166);
	}
    
    public SubGui(int width, int height) {
		this.width = width;
		this.height = height;
	}
    
	//public String title;
    
    public ArrayList<GuiControl> controls = new ArrayList<GuiControl>();
	
	public abstract void createControls();
	
	public abstract void drawForeground(FontRenderer fontRenderer);
	
	public abstract void drawBackground(FontRenderer fontRenderer);
	
	public void onControlEvent(GuiControl control, ControlEvent event){}
	
	//public void onMouseDragged(GuiControl control){}
	
	//public void onMouseReleased(GuiControl control){}
	
	public void readFromOpeningNBT(NBTTagCompound nbt){}
	
	public void readFromNBT(NBTTagCompound nbt){}
	
	public void sendPacketToServer(int controlID, NBTTagCompound nbt)
	{
		PacketHandler.sendPacketToServer(new GuiControlPacket(controlID, nbt));
	}
	
	public static enum ControlEvent{
		Click,
		DblClick,
		KeyPressed,
		Update;
	}
}
