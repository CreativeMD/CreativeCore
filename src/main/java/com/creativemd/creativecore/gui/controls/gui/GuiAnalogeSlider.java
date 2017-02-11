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
	public int sliderWidth = 4;
	
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
		return Math.round(value*100F)/100F + "";
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		
		float percent = getPercentage();
		
		int posX = (int)((this.width - (borderWidth*2+sliderWidth)) * percent);
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
			int width = this.width - getContentOffset()*2 - sliderWidth;
			if(posX < this.posX+getContentOffset())
				this.value = this.minValue;
			else if(posX > this.posX + getContentOffset() + width + sliderWidth/2)
				this.value = this.maxValue;
			else{
				int mouseOffsetX = posX - this.posX - getContentOffset() - sliderWidth/2;
				this.value = (float) (this.minValue+(float)((this.maxValue - this.minValue) * ((float)mouseOffsetX / (float)width)));
			}
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
