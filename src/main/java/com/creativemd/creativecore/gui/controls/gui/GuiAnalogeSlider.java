package com.creativemd.creativecore.gui.controls.gui;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class GuiAnalogeSlider extends GuiControl
{
	public float maxValue;
	public float minValue;
	public float value;
	public boolean grabbedSlider;
	
	public GuiAnalogeSlider(String name, int x, int y, int width, int height, float value, float minValue, float maxValue)
	{
		super(name, x, y, width, height);
		this.marginWidth = 0;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public String getTextByValue()
	{
		return value + "";
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		
		int sliderWidth = 4;
		float percent = getPercentage();
		
		int posX = 1+(int)((this.width - (1+sliderWidth)) * percent);
		style.getFace(this).renderStyle(posX, 0, helper, 4, height);
		
		String text = getTextByValue();
		helper.drawStringWithShadow(text, width, height, ColorUtils.WHITE);
	}
	
	public float getPercentage()
	{
		return (this.value - this.minValue) / (this.maxValue - this.minValue);
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button)
	{
		if(button == 0)
		{
			playSound(SoundEvents.UI_BUTTON_CLICK);
			return (grabbedSlider = true);
		}
		return false;
	}
	
	public void setValue(float value)
	{
		this.value = Math.max(minValue, value);
		this.value = Math.min(maxValue, this.value);
		
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button){
		if(grabbedSlider)
		{
			if(posX < this.posX)
				this.value = this.minValue;
			else if(posX > this.posX + this.width)
				this.value = this.maxValue;
			else
				this.value = this.minValue+(float)((this.maxValue - this.minValue) * ((float)(posX - this.posX) / (float)this.width));
			setValue(value);
		}
	}
	
	@Override
	public void mouseReleased(int posX, int posY, int button)
	{
		if(this.grabbedSlider)
			grabbedSlider = false;
	}
}
