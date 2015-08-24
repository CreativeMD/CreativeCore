package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector2d;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.core.CreativeCore;

public class GuiSteppedSlider extends GuiControl
{
	public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation SliderRailTexture = new ResourceLocation(CreativeCore.modid, "textures/gui/SliderRail.png");
	public static final ResourceLocation SliderTexture = new ResourceLocation(CreativeCore.modid, "textures/gui/Slider.png");
	public String[] representativeObjects;
	public int id;
	public int stepsCount;
	public int value;
	public boolean grabbedSlider;

	/**
	 * @param x         			- The x-coordinate of the topLeft corner of the Slider.
	 * @param y         			- The y-coordinate of the topLeft corner of the Slider.
	 * @param width     			- The Width of the Slider.
	 * @param height   			 	- The height of the Slider.
	 * @param rotation 			 	- The rotation of the Slider.
	 * @param id					- The id of the Slider, used to get its values.
	 * @param stepsCount  			- The StepsCount this Slider should have. (This can't be lower than 2, It will throw an exception!)
	 * @param representativeObjects - The String each Step should represent.
	 */
	public GuiSteppedSlider(String name, int x, int y, int width, int height, int rotation, int id, int stepsCount, String[] representativeObjects)
	{
		super(name, x, y, width, height, rotation);
		this.id = id;
		if(stepsCount > 1)
			this.stepsCount = stepsCount;
		else throw new IllegalArgumentException("StepCount can't be lower than 2!");
		this.representativeObjects = representativeObjects;
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
         * - this.stepCount
         * offset = (this.posX + (this.value * (this.width/stepCount)))
         * representativeObjects = representativeObjects[Value]
         */
       
        /*
         * These methods are the same as the AnalogeSlider, alternative offset is added by the parameters
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(SliderRailTexture);
        RenderHelper2D.drawTexturedModalRect(0, this.posX + this.width, this.posY, 0, 0, this.width, this.height);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(SliderTexture);
        RenderHelper2D.drawTexturedModalRect(0, (int)((float)this.posX + ((float)this.width * (((this.value - this.minValue) / (this.maxValue - this.minValue))))), this.posY, 0, 0, (int)((float)this.width * (5F/85F)) , this.height);
        GL11.glPopMatrix();
        */
        
        
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button)
	{
		Vector2d mouse = parent.getMousePos();
		if(enabled)
		{
			if(button == 0 && isMouseOver((int)mouse.x, (int)mouse.x))
			{
				mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
				return (grabbedSlider = true);
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseDragged(int posX, int posY, int button, long time)
	{
		Vector2d mouse = parent.getMousePos();
		if(grabbedSlider)
		{
			if(mouse.x < this.posX)
				this.value = 0;
			else if(mouse.x > this.posX + this.width)
				this.value = this.stepsCount - 1;
			else this.value = (int)(((float)(mouse.x - this.posX) / ((float)this.width / (float)(this.stepsCount - 1))) - (int)((float)(mouse.x - this.posX) / ((float)this.width / (float)(this.stepsCount - 1))) > 0.5 ? (int)((float)(mouse.x - this.posX) / ((float)this.width / (float)(this.stepsCount - 1))) + 1 : (int)((float)(mouse.x - this.posX) / ((float)this.width / (float)(this.stepsCount - 1))));
			return true;
		}
		return false;
	}
	
	@Override
	public void mouseReleased(int posX, int posY, int button)
	{
		if(this.grabbedSlider)
			grabbedSlider = false;
	}
}
