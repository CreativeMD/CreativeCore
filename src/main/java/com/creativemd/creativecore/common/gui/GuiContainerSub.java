package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.tileentity.TileEntityCreative;
import com.creativemd.creativecore.core.CreativeCore;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiContainerSub extends GuiContainer{
	
	public static final int xSize = 176;
	
    public static final int ySize = 166;
    
	public static final ResourceLocation background = new ResourceLocation(CreativeCore.modid + ":textures/gui/GUI.png");
	
	public SubGui gui;
	
	public ArrayList<GuiControl> controls;
	
	public GuiContainerSub(EntityPlayer player, SubGui gui, SubContainer container) {
		super(new ContainerSub(player, container));
		this.gui = gui;
		controls = gui.getControls();
	}
	
	@Override                                   
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		GuiControl.renderControls(controls, fontRendererObj, 0);
		gui.drawForeground(fontRendererObj);
		
		Vector2d mouse = GuiControl.getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i).isMouseOver((int)mouse.x, (int)mouse.y))
			{
				RenderHelper2D.drawHoveringText(controls.get(i).getTooltip(), (int)mouse.x, (int)mouse.y, fontRendererObj);
			}
		}
	}
	
	@Override
	public void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		for (int i = 0; i < controls.size(); i++) {
			Vector2d mouse = GuiControl.getMousePos();
			Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
			if(controls.get(i).isMouseOver((int)pos.x, (int)pos.y) && controls.get(i).mousePressed((int)pos.x, (int)pos.y, button))
				gui.onControlClicked(controls.get(i));
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		
		for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
            this.drawTexturedModalRect(k+slot.xDisplayPosition-1, l+slot.yDisplayPosition-1, xSize, 0, 18, 18);
        }
		gui.drawBackground(fontRendererObj);
	}

}
