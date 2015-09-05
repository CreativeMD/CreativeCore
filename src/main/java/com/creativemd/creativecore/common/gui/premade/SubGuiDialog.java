package com.creativemd.creativecore.common.gui.premade;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiButton;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.controls.GuiLabel;
import com.creativemd.creativecore.common.gui.event.ControlClickEvent;
import com.creativemd.creativecore.common.packet.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

public class SubGuiDialog extends SubGui{
	
	public String[] buttons;
	public String text;
	
	public SubGuiDialog(String text, String[] buttons)
	{
		super(Math.max(mc.fontRenderer.getStringWidth(text)+20, buttons.length*30+20), 60);
		this.text = text;
		this.buttons = buttons;
	}
	
	@Override
	public void drawBackground()
	{
		int k = (this.width - this.width) / 2;
		int l = (this.height - this.height) / 2;
		
		Vector4d color = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(k, l, k+this.width, l+this.height, color, color);
		color = new Vector4d(120, 120, 120, 255);
		RenderHelper2D.drawGradientRect(k+2, l+2, k+this.width-2, l+this.height-2, color, color);
	}

	@Override
	public void createControls() {
		controls.add(new GuiLabel(text, 10, 5));
		for (int i = 0; i < buttons.length; i++) {
			controls.add(new GuiButton(buttons[i], 30*i+(width-buttons.length*30)/2, 30, 30, 20, i));
		}
	}
	
	public void saveData(NBTTagCompound nbt)
	{
		nbt.setBoolean("dialog", true);
    	nbt.setString("text", text);
    	nbt.setInteger("count", buttons.length);
    	for (int i = 0; i < buttons.length; i++) {
			nbt.setString("b" + i, buttons[i]);
		}
    	
	}
	
	@Override
	public void closeLayer(NBTTagCompound nbt, boolean isPacket)
    {
		saveData(nbt);
    	super.closeLayer(nbt, isPacket);
    }
	
	@CustomEventSubscribe
	public void onButtonClicked(ControlClickEvent event)
	{
		if(event.source instanceof GuiButton)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("clicked", ((GuiButton) event.source).caption);
	    	closeLayer(nbt);
		}
	}

	@Override
	public void drawOverlay(FontRenderer fontRenderer) {
		
	}

}
