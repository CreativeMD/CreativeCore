package com.creativemd.creativecore.gui.mc;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerSub extends GuiContainer {
	
	protected ArrayList<SubGui> layers;
	
	public GuiContainerSub(EntityPlayer player, SubGui gui, SubContainer container) {
		super(new ContainerSub(player, container));
		((ContainerSub)inventorySlots).gui = this;
		
		layers = new ArrayList<SubGui>();
		gui.container = container;
		gui.gui = this;
		this.layers.add(gui);
		
		gui.onOpened();
		
		resize();
		//SubGui.itemRender = GuiScreen.itemRender;
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
	
	public boolean isTopLayer(SubGui gui)
	{
		return getTopLayer() == gui;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
	}
	
	@Override
	public void drawDefaultBackground()
    {
        
    }
	
	@Override                                   
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		for (int i = 0; i < layers.size(); i++){
			
			layers.get(i).onTick();
			
			GlStateManager.pushMatrix();
			int k = guiLeft;
			int l = guiTop;
			
			GlStateManager.translate(-k, -l, 0);
	        
			//drawWorldBackground(0);	
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
			
			int offX = (this.width - layers.get(i).width) / 2 - k;
	        int offY = (this.height - layers.get(i).height) / 2 - l;
			//GL11.glTranslatef((float)k+offX, (float)l+offY, 0.0F);
	        GlStateManager.translate(k, l, 0);
			
	        GlStateManager.translate(offX, offY, 0);
			
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			//GL11.glDisable(GL11.GL_DEPTH_TEST);
	        
	        layers.get(i).renderControl(GuiRenderHelper.instance);
			
			/*layers.get(i).drawBackground();
			RenderHelper.enableGUIStandardItemLighting();
			short short1 = 240;
	        short short2 = 240;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)short1 / 1.0F, (float)short2 / 1.0F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			
			//GL11.glTranslatef((float)par1, (float)par2, 0.0F);
			layers.get(i).drawForeground(fontRendererObj);
			
			GlStateManager.translate(-offX, -offY, 0);
			GlStateManager.enableLighting();*/
	        GlStateManager.popMatrix();
		}
	}
	
	@Override
	public void onGuiClosed()
	{
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).onClosed();
		}
	}
	
	@Override
	public void keyTyped(char character, int key) throws IOException
    {
		if(getTopLayer() != null && !getTopLayer().onKeyPressed(character, key))
			super.keyTyped(character, key);
    }
	
	@Override
	public void handleInput() throws IOException
    {
		if(Mouse.isCreated())
		{
			handleScrolling();
			Vec3d mouse = getTopLayer().getMousePos();
        	getTopLayer().mouseMove((int)mouse.xCoord, (int)mouse.yCoord, 0);
		}
		super.handleInput();
    }
    
	public void handleScrolling()
	{
		int j = Mouse.getDWheel();
        if (j != 0)
        {
        	Vec3d mouse = getTopLayer().getMousePos();
        	getTopLayer().mouseScrolled((int)mouse.xCoord, (int)mouse.yCoord, j);
        	//Mouse.setGrabbed(true);
        }
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) throws IOException
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
	protected void mouseReleased(int x, int y, int button)
    {
		super.mouseReleased(x, y, button);
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
}
