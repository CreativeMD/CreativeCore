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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public abstract class GuiControl{
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	private int id = -1;
	
	public void setID(int id)
	{
		if(this.id == -1)
			this.id = id;
	}
	
	public int posX;
	public int posY;
	public int height;
	public int width;
	public int rotation;
	public boolean visible;
	public boolean enabled;
	
	public SubGui parent;
	
	/**0: around center, 1: around left & top corner**/
	public int rotateMode = 0;
	
	public GuiControl(int x, int y, int width, int height, int rotation)
	{
		this.posX = x;
		this.posY = y;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.enabled = true;
		this.visible = true;
	}
	
	public GuiControl(int x, int y, int width, int height)
	{
		this(x, y, width, height, 0);
	}
	
	/*public boolean doesControlSupportSync()
	{
		return getContainerControl() != null;
	}*/
	
	//public abstract ContainerControl getContainerControl();
	
	public abstract void drawControl(FontRenderer renderer);
	
	public void renderControl(FontRenderer renderer, int zLevel)
	{
		GL11.glPushMatrix();
		//GL11.glTranslated(-posX, -posY, 0);
		//GL11.glTranslated(width/2, height/2, zLevel);
		GL11.glTranslated(posX, posY, 0);
		GL11.glRotated(rotation, 0, 0, 1);
		GL11.glTranslated(-width/2, -height/2, 0);
		//GL11.glTranslated(-posX, -posY, zLevel);
		drawControl(renderer);
		//GL11.glTranslated(posX, posY, -zLevel);
		//GL11.glRotated(-rotation, 0, 0, 1);
		GL11.glPopMatrix();
	}
	
	public boolean isMouseOver()
	{
		Vector2d mouse = GuiControl.getMousePos(parent.width, parent.height);
		Vector2d pos = getValidPos((int)mouse.x, (int)mouse.y);
		return isMouseOver((int)pos.x, (int)pos.y);
	}
	
	public boolean isMouseOver(int posX, int posY)
	{
		//Vector2d mousePos = getRotationAround(-rotation, new Vector2d(posX, posY), new Vector2d(this.posX, this.posY));
		if(posX >= this.posX-this.width/2 && posX < this.posX+this.width/2 &&
				posY >= this.posY-this.height/2 && posY < this.posY+this.height/2)
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
	
	public boolean mouseDragged(int posX, int posY, int button){
		return false;
	}	
	
	public void mouseReleased(int posX, int posY, int button){
		
	}
	
	public void mouseMove(int posX, int posY, int button){
		
	}
	
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
	
	public void onLoseFocus(){}
	
	public boolean onKeyPressed(char character, int key)
	{
		return false;
	}
	//public void onKeyDown(int key){}
	//public void onKeyUp(int key){}
	
	
	public ArrayList<String> getTooltip()
	{
		return new ArrayList<String>();
	}
	
	public void raiseEvent(GuiControlEvent event)
	{
		parent.eventBus.raiseEvent(event);
	}
	
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
        //System.out.println("Mouse X:" + x + ", Y:" + y);
		return new Vector2d(x, y);
	}	
	
	public Vector2d getValidPos(int x, int y)
	{
		Vector2d pos = new Vector2d(x, y);
		//pos.sub(new Vector2d(mc.displayWidth/2-GuiContainerSub.xSize/2, mc.displayHeight/2-GuiContainerSub.ySize/2));
		return getRotationAround(-rotation, pos, new Vector2d(posX, posY));
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
