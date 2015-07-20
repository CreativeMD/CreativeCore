package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.SubGui.ControlEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiButtonControl extends GuiControl{
	
	public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	
	public String caption;
	public int id;
	
	public GuiButtonControl(String caption, int x, int y, int width, int height) {
		this(caption, x, y, width, height, 0);
	}
	
	public GuiButtonControl(String caption, int x, int y, int width, int height, int id) {
		super(x, y, width, height);
		this.caption = caption;
		this.id = id;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		Minecraft mc = Minecraft.getMinecraft();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(buttonTextures);
		int k = 1;
		Vector2d mouse = getMousePos(parent.width, parent.height);
		int l = 14737632;
		
		if(isMouseOver((int)mouse.x, (int)mouse.y))
		{
			k = 2;
			l = 16777120;
		}
		
		if (!this.enabled)
        {
        	k = 0;
            l = 10526880;
        }
		
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        RenderHelper2D.drawTexturedModalRect(0, 0, 0, 46 + k * 20, this.width / 2, this.height);
        RenderHelper2D.drawTexturedModalRect(0 + this.width / 2, 0, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
        //this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);

        /*if (packedFGColour != 0)
        {
            l = packedFGColour;
        }*/
        renderer.drawStringWithShadow(caption, width / 2 - renderer.getStringWidth(caption) / 2, (this.height - 8) / 2, l);
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button)
	{
		if(enabled)
		{
			Minecraft mc = Minecraft.getMinecraft();
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			//parent.onControlEvent(this, ControlEvent.Click);
			return true;
		}
		return false;
	}
}
