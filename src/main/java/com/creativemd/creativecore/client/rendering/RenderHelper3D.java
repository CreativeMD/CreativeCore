package com.creativemd.creativecore.client.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper3D {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static void renderBlock(double x, double y, double z, double width, double height, double length, double rotateX, double rotateY, double rotateZ, double red, double green, double blue, double alpha)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotate((float)rotateX, 1, 0, 0);
		GlStateManager.rotate((float)rotateY, 0, 1, 0);
		GlStateManager.rotate((float)rotateZ, 0, 0, 1);
		GlStateManager.scale(width, height, length);
		GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
		
		GlStateManager.glBegin(GL11.GL_POLYGON);
		GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(0.5f, 0.5f, -0.5f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, -0.5f);
		GlStateManager.glEnd();
		
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GlStateManager.glNormal3f(0.0f, 0.0f, 1.0f);
		GlStateManager.glVertex3f(0.5f, -0.5f, 0.5f);
		GlStateManager.glVertex3f(0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, -0.5f, 0.5f);
		GlStateManager.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GlStateManager.glNormal3f(1.0f, 0.0f, 0.0f);
		GlStateManager.glVertex3f(0.5f, 0.5f, -0.5f);
		GlStateManager.glVertex3f(0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(0.5f, -0.5f, 0.5f);
		GlStateManager.glVertex3f(0.5f, -0.5f, -0.5f);
		GlStateManager.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GlStateManager.glNormal3f(-1.0f, 0.0f, 0.0f);
		GlStateManager.glVertex3f(-0.5f, -0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, -0.5f);
		GlStateManager.glVertex3f(-0.5f, -0.5f, -0.5f);
		GlStateManager.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GlStateManager.glNormal3f(0.0f, -1.0f, 0.0f);
		GlStateManager.glVertex3f(0.5f, -0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, -0.5f, 0.5f);
		GlStateManager.glVertex3f(-0.5f, -0.5f, -0.5f);
		GlStateManager.glVertex3f(0.5f, -0.5f, -0.5f);
		GlStateManager.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GlStateManager.glNormal3f(0.0f, 0.0f, -1.0f);
		GlStateManager.glVertex3f(0.5f, 0.5f, -0.5f);
		GlStateManager.glVertex3f(0.5f, -0.5f, -0.5f);
		GlStateManager.glVertex3f(-0.5f, -0.5f, -0.5f);
		GlStateManager.glVertex3f(-0.5f, 0.5f, -0.5f);
		GlStateManager.glEnd();
		
		
        GlStateManager.popMatrix();
	}
	
	public static void applyDirection(EnumFacing direction)
	{
		int rotation = 0;
		switch(direction)
		{
		case EAST:
			rotation = 0;
			break;
		case NORTH:
			rotation = 90;
			break;
		case SOUTH:
			rotation = 270;
			break;
		case WEST:
			rotation = 180;
			break;
		case UP:
			GL11.glRotated(90, 1, 0, 0);
			GL11.glRotated(-90, 0, 0, 1);
			break;
		case DOWN:
			GL11.glRotated(-90, 1, 0, 0);
			GL11.glRotated(-90, 0, 0, 1);
			break;
		default:
			break;
		}
		GL11.glRotated(rotation, 0, 1, 0);
	}
}
