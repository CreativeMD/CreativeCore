package com.creativemd.creativecore.common.gui.controls;

import java.lang.annotation.Target;
import java.util.ArrayList;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.xml.ws.FaultAction;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.event.GuiControlEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class GuiControl{
	
	public static final int White = 14737632;
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	private int id = -1;
	
	public void resetID()
	{
		this.id = -1;
	}
	
	public void setID(int id)
	{
		if(this.id == -1)
			this.id = id;
	}
	
	public boolean isContainerControl = false;
	public int posX;
	public int posY;
	public int height;
	public int width;
	public int rotation;
	public boolean visible;
	public boolean enabled;
	
	public SubGui parent;
	
	///**0: around center, 1: around left & top corner**/
	//public int rotateMode = 0;
	
	public String name;
	
	public GuiControl(String name, int x, int y, int width, int height, int rotation)
	{
		this.name = name;
		this.posX = x;
		this.posY = y;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.enabled = true;
		this.visible = true;
	}
	
	public GuiControl(String name, int x, int y, int width, int height)
	{
		this(name, x, y, width, height, 0);
	}
	
	//================Container Management================
	
	public GuiControl setContainerControl()
	{
		isContainerControl = true;
		return this;
	}
	
	//================INIT================
	
	public void init()
	{
		
	}
	
	//================Render================
	
	public abstract void drawControl(FontRenderer renderer);
	
	public void renderControl(FontRenderer renderer, int zLevel)
	{
		Vector2d centerOffset = getCenterOffset();
		GL11.glPushMatrix();
		GL11.glTranslated(posX+centerOffset.x, posY+centerOffset.y, 0);
		GL11.glRotated(rotation, 0, 0, 1);
		GL11.glTranslated(-centerOffset.x, -centerOffset.y, 0);
		drawControl(renderer);
		GL11.glPopMatrix();
	}
	
	//================Interaction================
	
	public GuiControl setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public boolean isInteractable()
	{
		return visible && enabled;
	}
	
	public boolean isMouseOver()
	{
		Vector2d mouse = parent.getMousePos();
		Vector2d pos = getValidPos((int)mouse.x, (int)mouse.y);
		return enabled && parent.isTopLayer() && visible && isMouseOver((int)pos.x, (int)pos.y);
	}
	
	public boolean isMouseOver(int posX, int posY)
	{
		if(posX >= this.posX && posX < this.posX+this.width &&
				posY >= this.posY && posY < this.posY+this.height)
		{
			return true;
		}
		return false;
	}
	
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		return false;
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		return false;
	}
	
	public boolean mouseDragged(int posX, int posY, int button, long time){
		return false;
	}	
	
	public void mouseReleased(int posX, int posY, int button){
		
	}
	
	public void mouseMove(int posX, int posY, int button){
		
	}
	
	//================SORTING================
	
	public void moveControlAbove(GuiControl controlInBack)
	{
		parent.moveControlAbove(this, controlInBack);
	}
	
	public void moveControlBehind(GuiControl controlInFront)
	{
		parent.moveControlBehind(this, controlInFront);
	}
	
	public void moveControlToBottom()
	{
		parent.moveControlToBottom(this);
	}
	
	public void moveControlToTop()
	{
		parent.moveControlToTop(this);
	}
	
	public boolean is(String name)
	{
		return this.name.equalsIgnoreCase(name);
	}
	
	//================CUSTOM EVENTS================
	
	public void onLoseFocus(){}
	
	public boolean onKeyPressed(char character, int key)
	{
		return false;
	}
	
	public void onGuiClose() {}
	
	//================Tooltip================
	
	public ArrayList<String> getTooltip()
	{
		return new ArrayList<String>();
	}
	
	//================Event================
	
	public boolean raiseEvent(GuiControlEvent event)
	{
		return parent.raiseEvent(event);
	}
	
	//================Rotation-Center================
	
	public Vector2d getCenterOffset()
	{
		return new Vector2d(width/2, height/2);
	}
	
	public Vector2d getValidPos(int x, int y)
	{
		Vector2d pos = new Vector2d(x, y);
		Vector2d centerOffset = getCenterOffset();
		return getRotationAround(-rotation, pos, new Vector2d(posX+centerOffset.x, posY+centerOffset.y));
	}
	
	//================Sound================
	
	public static void playSound(String soundID)
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(soundID), 1.0F));
	}
	
	//================STATIC HELPERS================
	
	public static Vector2d getMousePos(int width, int height)
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution scaledresolution = new ScaledResolution(mc , mc.displayWidth, mc.displayHeight);
		int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
		int x = Mouse.getEventX() * i / mc.displayWidth;
        int y = j - Mouse.getEventY() * j / mc.displayHeight - 1;
        int movex = (i - width)/2;
        int movey = (j - height)/2;
        x -= movex;
        y -= movey;
		return new Vector2d(x, y);
	}
	
	public static Vector2d getRotationAround(double angle, Vector2d pos, Vector2d center)
	{
		Vector2d result = new Vector2d(pos);
		result.sub(center);
		Vector2d temp = new Vector2d(result);
		result.x = Math.cos(Math.toRadians(angle)) * temp.x - Math.sin(Math.toRadians(angle)) * temp.y;
		result.y = Math.sin(Math.toRadians(angle)) * temp.x + Math.cos(Math.toRadians(angle)) * temp.y;
		result.add(center);
		return result;
	}
	
	
	public static void renderControls(ArrayList<GuiControl> controls, FontRenderer renderer, int zLevel)
	{
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).renderControl(renderer, zLevel);
		}
	}
	
}
