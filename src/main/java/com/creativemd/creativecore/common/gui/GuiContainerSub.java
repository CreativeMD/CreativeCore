package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.event.ControlClickEvent;
import com.creativemd.creativecore.core.CreativeCore;

public class GuiContainerSub extends GuiContainer{
	
	public static final ResourceLocation background = new ResourceLocation(CreativeCore.modid + ":textures/gui/GUI.png");
	
	protected ArrayList<SubGui> layers;
	
	public GuiContainerSub(EntityPlayer player, SubGui gui, SubContainer container) {
		super(new ContainerSub(player, container));
		((ContainerSub)inventorySlots).gui = this;
		
		layers = new ArrayList<SubGui>();
		gui.container = container;
		gui.gui = this;
		this.layers.add(gui);
		
		gui.initGui();
		
		resize();
		SubGui.itemRender = GuiScreen.itemRender;
	}
	
	public ArrayList<SubGui> getLayers()
	{
		return layers;
	}
	
	public void removeLayer(SubGui layer)
	{
		layers.remove(layer);
		((ContainerSub)inventorySlots).layers.remove(layer.container);
		resize();
	}
	
	public void addLayer(SubGui layer)
	{
		layers.add(layer);
		((ContainerSub)inventorySlots).layers.add(layer.container);
		resize();
	}
	
	public void resize()
	{
		this.xSize = 0;
		this.ySize = 0;
		for (int i = 0; i < layers.size(); i++) {
			if(layers.get(i).width > this.xSize)
				this.xSize = layers.get(i).width;
			if(layers.get(i).height > this.ySize)
				this.ySize = layers.get(i).height;
		}
	}
	
	public int getWidth()
	{
		return xSize;
	}
	
	public int getHeight()
	{
		return ySize;
	}
	
	/**Returns the max avaible scale for this gui**/
	public int getMaxScale(Minecraft mc)
	{
		int k = 1000;
		int scaleFactor = 1;


        while (scaleFactor < k && xSize * (scaleFactor + 1) <= mc.displayWidth && ySize * (scaleFactor + 1) <= mc.displayHeight)
        {
            ++scaleFactor;
        }

        /*if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }*/
		return scaleFactor;
	}
	
	@Override
	public void drawDefaultBackground()
    {
        
    }
	
	@Override                                   
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		for (int i = 0; i < layers.size(); i++){
			GL11.glPushMatrix();
			int k = guiLeft;
			int l = guiTop;
			
	        GL11.glTranslatef((float)-k, (float)-l, 0);
	        
			drawWorldBackground(0);	
			
			
			int offX = (this.width - layers.get(i).width) / 2 - k;
	        int offY = (this.height - layers.get(i).height) / 2 - l;
			//GL11.glTranslatef((float)k+offX, (float)l+offY, 0.0F);
			GL11.glTranslatef((float)k, (float)l, 0.0F);
			
			GL11.glTranslatef((float)offX, (float)offY, 0.0F);
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			//GL11.glDisable(GL11.GL_DEPTH_TEST);
			
			layers.get(i).drawBackground();
			RenderHelper.enableGUIStandardItemLighting();
			short short1 = 240;
	        short short2 = 240;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)short1 / 1.0F, (float)short2 / 1.0F);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			
			//GL11.glTranslatef((float)par1, (float)par2, 0.0F);
			layers.get(i).drawForeground(fontRendererObj);
			
			GL11.glTranslatef((float)-offX, (float)-offY, 0.0F);
	        GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glPopMatrix();
		}
	}
	
	public boolean hasTopLayer()
	{
		return layers.size() > 0;
	}
	
	public SubGui getTopLayer()
	{
		if(hasTopLayer())
			return layers.get(layers.size()-1);
		return null;
	}
	
	@Override
	public void keyTyped(char character, int key)
    {
		if(getTopLayer() != null && !getTopLayer().keyTyped(character, key))
			super.keyTyped(character, key);
    }
	
	@Override
	public void handleInput()
    {
		if(Mouse.isCreated())
		{
			handleScrolling();
			Vector2d mouse = GuiControl.getMousePos(width, height);
        	getTopLayer().mouseMove((int)mouse.x, (int)mouse.y, 0);
		}
		super.handleInput();
    }
    
	public void handleScrolling()
	{
		int j = Mouse.getDWheel();
        if (j != 0)
        {
        	Vector2d mouse = GuiControl.getMousePos(width, height);
        	getTopLayer().mouseScrolled((int)mouse.x, (int)mouse.y, j);
        	//Mouse.setGrabbed(true);
        }
	}
	
	@Override
	public void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		getTopLayer().mousePressed(x, y, button);
	}
	
	@Override
	public void mouseClickMove(int x, int y, int button, long time)
	{
		super.mouseClickMove(x, y, button, time);
		getTopLayer().mouseDragged(x, y, button, time);
	}
	
	@Override
	protected void mouseMovedOrUp(int x, int y, int button)
	{
		super.mouseMovedOrUp(x, y, button);
		//if(button > 0)
			//onMouseMove(x, y, button);
		//else
			onMouseReleased(x, y, button);
	}
	
	public void onMouseMove(int x, int y, int button)
	{
		getTopLayer().mouseMove(x, y, button);
	}
	
	public void onMouseReleased(int x, int y, int button)
	{
		getTopLayer().mouseReleased(x, y, button);
	}
	
	@Override
	public void onGuiClosed()
	{
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).onGuiClose();
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		//Unused!!!
	}

}
