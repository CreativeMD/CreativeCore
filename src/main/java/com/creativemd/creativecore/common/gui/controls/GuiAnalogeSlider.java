package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.core.CreativeCore;

public class GuiAnalogeSlider extends GuiControl
{
	public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation SliderRailTexture = new ResourceLocation(CreativeCore.modid, "textures/gui/SliderRail.png");
	public static final ResourceLocation SliderTexture = new ResourceLocation(CreativeCore.modid, "textures/gui/Slider.png");
	public int id;
	public float maxValue;
	public float minValue;
	public float value;
	public boolean grabbedSlider;

	/**
	 * The Default minimumValue is 0.
	 * @param x         - The x-coordinate of the topLeft corner of the Slider.
	 * @param y         - The y-coordinate of the topLeft corner of the Slider.
	 * @param width     - The Width of the Slider.
	 * @param height    - The height of the Slider.
	 * @param rotation  - The rotation of the Slider.
	 * @param id		- The id of the Slider, used to get its values.
	 * @param minValue  - The minimum Value this Slider can reach.
	 * @param maxValue  - The maximum Value this Slider can reach.
	 */
	public GuiAnalogeSlider(int x, int y, int width, int height, int rotation, int id, float minValue, float maxValue)
	{
		super(x, y, width, height, rotation);
		this.id = id;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	/**
	 * The Default minimumValue is 0.
	 * @param x         - The x-coordinate of the topLeft corner of the Slider.
	 * @param y         - The y-coordinate of the topLeft corner of the Slider.
	 * @param width     - The Width of the Slider.
	 * @param height    - The height of the Slider.
	 * @param rotation  - The rotation of the Slider.
	 * @param id		- The id of the Slider, used to get its values.
	 * @param maxValue  - The max Value this Slider can reach.
	 */
	public GuiAnalogeSlider(int x, int y, int width, int height, int rotation, int id, float maxValue)
	{
		this(x, y, width, height, rotation, id, 0, maxValue);
	}
	
	/**
	 * Default Slider Constructor, use this only if you use only 1 Slider!
	 * The Default minimumValue is 0;
	 * The Default maximumValue is 100;
	 * @param x         - The x-coordinate of the topLeft corner of the Slider.
	 * @param y         - The y-coordinate of the topLeft corner of the Slider.
	 * @param width     - The Width of the Slider.
	 * @param height    - The height of the Slider.
	 * @param rotation  - The rotation of the Slider.
	 */
	public GuiAnalogeSlider(int x, int y, int width, int height, int rotation)
	{
		this(x, y, width, height, rotation, 0, 0, 100);
	}

	@Override
	public void drawControl(FontRenderer renderer)
	{
		Minecraft mc = Minecraft.getMinecraft();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(buttonTextures);
		int mouseEvent = 1;
		Vector2d mouse = getMousePos(parent.width, parent.height);
		
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
        /*
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
		Vector2d mouse = getMousePos(parent.width, parent.height);
		if(enabled)
		{
			if(button == 0 && isMouseOver((int)mouse.x, (int)mouse.x))
			{
				Minecraft mc = Minecraft.getMinecraft();
				mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
				return (grabbedSlider = true);
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseDragged(int posX, int posY, int button)
	{
		Vector2d mouse = getMousePos(parent.width, parent.height);
		if(grabbedSlider)
		{
			if(mouse.x < this.posX)
				this.value = this.minValue;
			else if(mouse.x > this.posX + this.width)
				this.value = this.maxValue;
			else this.value = (float)((this.maxValue - this.minValue) * ((float)(mouse.x - this.posX) / (float)this.width));
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
