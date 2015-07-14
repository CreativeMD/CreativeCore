package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import javax.vecmath.Color4b;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.SubGui.ControlEvent;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.events.GuiEventHandler;
import com.creativemd.creativecore.common.gui.events.KeyBoardEvents;
import com.creativemd.creativecore.common.gui.events.MouseEvents;
import com.creativemd.creativecore.common.tileentity.TileEntityCreative;
import com.creativemd.creativecore.core.CreativeCore;
import com.mojang.realmsclient.dto.Subscription.SubscriptionType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiContainerSub extends GuiContainer{
	
	public static final ResourceLocation background = new ResourceLocation(CreativeCore.modid + ":textures/gui/GUI.png");
	
	public SubGui gui;
	
	public ArrayList<GuiControl> controls;
	
	public GuiContainerSub(EntityPlayer player, SubGui gui, SubContainer container) {
		super(new ContainerSub(player, container));
		((ContainerSub)inventorySlots).gui = this;
		this.gui = gui;
		this.gui.container = container;
		gui.createControls();
		controls = gui.controls;
		for (int i = 0; i < controls.size(); i++)
		{
			controls.get(i).parent = gui;
			controls.get(i).setID(i);
		}
		for (int i = 0; i < ((ContainerSub)inventorySlots).controls.size(); i++) {
			((ContainerSub)inventorySlots).controls.get(i).init();
			controls.add(((ContainerSub)inventorySlots).controls.get(i).guiControl);
			controls.get(controls.size()-1).parent = gui;
		}
		this.xSize = gui.width;
		this.ySize = gui.height;
		SubGui.itemRender = GuiScreen.itemRender;
		
		MouseEvents.instance.addGuiContainerMouseListner(this);
		for(int i = 0; i < this.controls.size(); i++)
		{
			MouseEvents.instance.addMouseListner(this.controls.get(i));
		}
		
		KeyBoardEvents.instance.addMainGuiContianerKeyboardListner(this);
		for(int i = 0; i < this.controls.size(); i++)
		{
			KeyBoardEvents.instance.addKeyboardListner(this.controls.get(i));
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
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		GuiControl.renderControls(controls, fontRendererObj, 0);
		gui.drawForeground(fontRendererObj);
		
		Vector2d mouse = GuiControl.getMousePos(xSize, ySize);
		for (int i = 0; i < controls.size(); i++) {
			Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
			if(controls.get(i).isMouseOver((int)pos.x, (int)pos.y))
			{
				RenderHelper2D.drawHoveringText(controls.get(i).getTooltip(), (int)mouse.x, (int)mouse.y, fontRendererObj, this);
			}
		}
	}
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
		super.setWorldAndResolution(mc, width, height);
    }
	
	@Override
	public void keyTyped(char character, int key)
    {
		for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i).onKeyPressed(character, key))
				return ;
		}
		super.keyTyped(character, key);
    }
	
	/**
	 * This method is only used to separate the MouseEvents form the KeyBoardEvents.
	 * use the handleMouseInput() & handleKeyboardInput() methods instead.
	@Override
	public void handleInput()
    {
		if(Mouse.isCreated())
		{
			handleScrolling();
		}
		super.handleInput();
    }
    */
	
	@Override
	public void handleMouseInput()
	{
		GuiEventHandler.handleMouseEvent();
	}
	
	@Override
	public void handleKeyboardInput()
	{
		GuiEventHandler.handleKeyboardEvents();
	}
	
	public void handleScrolling()
	{
		int j = Mouse.getDWheel();
        if (j != 0)
        {
        	Vector2d mouse = GuiControl.getMousePos(xSize, ySize);
        	for(int i = controls.size()-1; i >= 0; i--)
    		{
				Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
				//Vector2d mousePos = getRotationAround(-rotation, new Vector2d(posX, posY), new Vector2d(this.posX, this.posY));
				if(controls.get(i).isMouseOver((int)pos.x, (int)pos.y) && controls.get(i).mouseScrolled((int)pos.x, (int)pos.y, j > 0 ? 1 : -1))
					return ;
    		}
        	//Mouse.setGrabbed(true);
        }
	}
	
	@Override
	public void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		for (int i = controls.size()-1; i >= 0; i--) {
			Vector2d mouse = GuiControl.getMousePos(xSize, ySize);
			Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
			//Vector2d mousePos = getRotationAround(-rotation, new Vector2d(posX, posY), new Vector2d(this.posX, this.posY));
			if(controls.get(i).isMouseOver((int)pos.x, (int)pos.y) && controls.get(i).mousePressed((int)pos.x, (int)pos.y, button))
			{
				gui.onControlEvent(controls.get(i), ControlEvent.Click);
				//gui.onControlClicked(controls.get(i));
				return ;
			}else{
				controls.get(i).onLoseFocus();
			}
		}
	}
	
	@Override
	public void mouseClickMove(int x, int y, int button, long time)
	{
		super.mouseClickMove(x, y, button, time);
		for(int i = controls.size()-1; i >= 0; i--)
		{
			Vector2d mouse = GuiControl.getMousePos(xSize, ySize);
			Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
			if(controls.get(i).isMouseOver((int)pos.x, (int)pos.y) && controls.get(i).mouseDragged((int)pos.x, (int)pos.y, button))
			{
				//gui.onMouseDragged(controls.get(i));
				return;
			}
		}
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
		for(int i = controls.size()-1; i >= 0; i--)
		{
			Vector2d mouse = GuiControl.getMousePos(xSize, ySize);
			Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
			if(controls.get(i).isMouseOver((int)pos.x, (int)pos.y))
				controls.get(i).mouseMove((int)pos.x, (int)pos.y, button);
		}
	}
	
	public void onMouseReleased(int x, int y, int button)
	{
		for(int i = controls.size()-1; i >= 0; i--)
		{
			Vector2d mouse = GuiControl.getMousePos(xSize, ySize);
			Vector2d pos = controls.get(i).getValidPos((int)mouse.x, (int)mouse.y);
			controls.get(i).mouseReleased((int)pos.x, (int)pos.y, button);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		gui.drawBackground();
		
		
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int frameSX = 176;
		int frameSY = 166;
		this.drawTexturedModalRect(k, l, 0, 0, 6, 6);
		this.drawTexturedModalRect(k+this.xSize-6, l, frameSX-6, 0, 6, 6);
		this.drawTexturedModalRect(k, l+this.ySize-6, 0, frameSY-6, 6, 6);
		this.drawTexturedModalRect(k+this.xSize-6, l+this.ySize-6, frameSX-6, frameSY-6, 6, 6);
		
		float sizeX = (frameSX-12);
		float amountX = (float)(xSize-12)/sizeX;
		
		int i = 0;
		while(amountX > 0)
		{
			float percent = 1;
			if(amountX < 1)
				percent = amountX;
			this.drawTexturedModalRect(k+6+(int)(i*sizeX), l, 6, 0, (int)Math.ceil(sizeX*percent), 6);
			this.drawTexturedModalRect(k+6+(int)(i*sizeX), l+this.ySize-6, 6, frameSY-6, (int)Math.ceil(sizeX*percent), 6);
			amountX--;
			i++;
		}
		
		float sizeY = (frameSY-12);
		float amountY = (float)(ySize-12)/sizeY;
		i = 0;
		while(amountY > 0)
		{
			float percent = 1;
			if(amountY < 1)
				percent = amountY;
			this.drawTexturedModalRect(k, l+6+(int)(i*sizeY), 0, 6, 6, (int)Math.ceil(sizeY*percent));
			this.drawTexturedModalRect(k+this.xSize-6, l+6+(int)(i*sizeY), frameSX-6, 6, 6, (int)Math.ceil(sizeY*percent));
			//this.drawTexturedModalRect(k+6+(int)(i*sizeY), l+this.ySize-6, 6, frameSY-6, (int)Math.ceil(sizeX*percent), 6);
			amountY--;
			i++;
		}
		
		//this.drawRect(k+6, l+6, k+this.xSize-6, l+this.ySize-6, 0);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColorMask(true, true, true, false);
		Vector4d color = new Vector4d(198, 198, 198, 255);
		RenderHelper2D.drawGradientRect(k+6, l+6, k+this.xSize-6, l+this.ySize-6, color, color);
		GL11.glColorMask(true, true, true, true);
		
		/*Tessellator tessellator = Tessellator.instance;
		GL11.glColor4d(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColorMask(true, true, true, false);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)k+6, (double)l+this.ySize-6, 0.0D);
        tessellator.addVertex((double)k+this.xSize-6, (double)l+this.ySize-6, 0.0D);
        tessellator.addVertex((double)k+this.xSize-6, (double)l+6, 0.0D);
        tessellator.addVertex((double)k+6, (double)l+6, 0.0D);
        tessellator.draw();*/
        //tessellator.
        //GL11.glColor4f(f, f1, f2, f3);
		//this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		
		/*for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
            this.drawTexturedModalRect(k+slot.xDisplayPosition-1, l+slot.yDisplayPosition-1, 176, 0, 18, 18);
        }*/
	}

}
