package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.event.ControlChangedEvent;
import com.creativemd.creativecore.core.CreativeCore;

public class GuiAnalogeSlider extends GuiControl
{
	public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public float maxValue;
	public float minValue;
	public float value;
	public boolean grabbedSlider;
	
	public GuiAnalogeSlider(String name, int x, int y, int width, int height, int rotation, float value, float minValue, float maxValue)
	{
		super(name, x, y, width, height, rotation);
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public GuiAnalogeSlider(String name, int x, int y, int width, int height, float value, float minValue, float maxValue)
	{
		this(name, x, y, width, height, 0, value, minValue, maxValue);
	}
	
	public String getTextByValue()
	{
		return value + "";
	}

	@Override
	public void drawControl(FontRenderer renderer)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(buttonTextures);
		int mouseEvent = 1;
		Vector2d mouse = parent.getMousePos();
		
		if(isMouseOver((int)mouse.x, (int)mouse.y))
			mouseEvent = 2;
		
		if (!this.enabled)
			mouseEvent = 0;
		
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        /*
         * parameters
         * - this.posX
         * - this.posY
         * - this.width
         * - this.height
         * - this.value
         * - this.maxValue
         * - this.minValue
         */
        
        Vector4d black = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(0, 0, this.width, this.height, black, black);
		
		Vector4d color = new Vector4d(60, 60, 60, 255);
		RenderHelper2D.drawGradientRect(1, 1, this.width-1, this.height-1, color, color);
		
		int sliderWidth = 4;
		float percent = (this.value - this.minValue) / (this.maxValue - this.minValue);
		
		int posX = 1+(int)((this.width - (1+sliderWidth)) * percent);
		
		Vector4d white = new Vector4d(255, 255, 255, 255);
		RenderHelper2D.drawGradientRect(posX, 1, posX+4, this.height-1, white, white);
		
		String text = getTextByValue();
		renderer.drawStringWithShadow(text, width / 2 - renderer.getStringWidth(text) / 2, (this.height - 8) / 2, White);
        
       /* GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(buttonTextures);
        RenderHelper2D.drawTexturedModalRect(0, this.posX + this.width, this.posY, 0, 0, this.width, this.height);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(buttonTextures);
        RenderHelper2D.drawTexturedModalRect(0, (int)((float)this.posX + ((float)this.width * (((this.value - this.minValue) / (this.maxValue - this.minValue))))), this.posY, 0, 0, (int)((float)this.width * (5F/85F)) , this.height);
        GL11.glPopMatrix();*/
        
        
        
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button)
	{
		Vector2d mouse = parent.getMousePos();
		if(button == 0)
		{
			Minecraft mc = Minecraft.getMinecraft();
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			return (grabbedSlider = true);
		}
		return false;
	}
	
	public void setValue(float value)
	{
		this.value = Math.max(minValue, value);
		this.value = Math.min(maxValue, this.value);
		
		raiseEvent(new ControlChangedEvent(this));
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button){
		Vector2d mouse = parent.getMousePos();
		if(grabbedSlider)
		{
			if(mouse.x < this.posX)
				this.value = this.minValue;
			else if(mouse.x > this.posX + this.width)
				this.value = this.maxValue;
			else
				this.value = this.minValue+(float)((this.maxValue - this.minValue) * ((float)(mouse.x - this.posX) / (float)this.width));
			setValue(value);
			//return true;
		}
		//return false;
	}
	
	@Override
	public void mouseReleased(int posX, int posY, int button)
	{
		if(this.grabbedSlider)
			grabbedSlider = false;
	}
}
