package com.creativemd.creativecore.client.rendering;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.common.utils.RotationUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper3D {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static RenderBlocks renderer = RenderBlocks.getInstance();
	
	public static ExtendedRenderBlocks renderBlocks = new ExtendedRenderBlocks(renderer);
	
	public static void renderBlock(double x, double y, double z, double width, double height, double length, double rotateX, double rotateY, double rotateZ, double red, double green, double blue, double alpha)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glRotated(rotateX, 1, 0, 0);
		GL11.glRotated(rotateY, 0, 1, 0);
		GL11.glRotated(rotateZ, 0, 0, 1);
		GL11.glScaled(width, height, length);
		GL11.glColor4d(red, green, blue, alpha);
		
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GL11.glNormal3f(0.0f, 1.0f, 0.0f);
		GL11.glVertex3f(-0.5f, 0.5f, 0.5f);
		GL11.glVertex3f(0.5f, 0.5f, 0.5f);
		GL11.glVertex3f(0.5f, 0.5f, -0.5f);
		GL11.glVertex3f(-0.5f, 0.5f, -0.5f);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GL11.glNormal3f(0.0f, 0.0f, 1.0f);
		GL11.glVertex3f(0.5f, -0.5f, 0.5f);
		GL11.glVertex3f(0.5f, 0.5f, 0.5f);
		GL11.glVertex3f(-0.5f, 0.5f, 0.5f);
		GL11.glVertex3f(-0.5f, -0.5f, 0.5f);
		GL11.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GL11.glNormal3f(1.0f, 0.0f, 0.0f);
		GL11.glVertex3f(0.5f, 0.5f, -0.5f);
		GL11.glVertex3f(0.5f, 0.5f, 0.5f);
		GL11.glVertex3f(0.5f, -0.5f, 0.5f);
		GL11.glVertex3f(0.5f, -0.5f, -0.5f);
		GL11.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
		GL11.glVertex3f(-0.5f, -0.5f, 0.5f);
		GL11.glVertex3f(-0.5f, 0.5f, 0.5f);
		GL11.glVertex3f(-0.5f, 0.5f, -0.5f);
		GL11.glVertex3f(-0.5f, -0.5f, -0.5f);
		GL11.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GL11.glNormal3f(0.0f, -1.0f, 0.0f);
		GL11.glVertex3f(0.5f, -0.5f, 0.5f);
		GL11.glVertex3f(-0.5f, -0.5f, 0.5f);
		GL11.glVertex3f(-0.5f, -0.5f, -0.5f);
		GL11.glVertex3f(0.5f, -0.5f, -0.5f);
		GL11.glEnd();
	 
		GL11.glBegin(GL11.GL_POLYGON);
		//GL11.glColor4d(red, green, blue, alpha);
		GL11.glNormal3f(0.0f, 0.0f, -1.0f);
		GL11.glVertex3f(0.5f, 0.5f, -0.5f);
		GL11.glVertex3f(0.5f, -0.5f, -0.5f);
		GL11.glVertex3f(-0.5f, -0.5f, -0.5f);
		GL11.glVertex3f(-0.5f, 0.5f, -0.5f);
		GL11.glEnd();
		
		
        GL11.glPopMatrix();
	}
	
	public static void renderBlock(Block block, double x, double y, double z, double width, double height, double length, double rotateX, double rotateY, double rotateZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glRotated(rotateX, 1, 0, 0);
		GL11.glRotated(rotateY, 0, 1, 0);
		GL11.glRotated(rotateZ, 0, 0, 1);
		GL11.glScaled(width, height, length);
		RenderHelper3D.renderBlock(block);
        GL11.glPopMatrix();
	}
	
	public static void renderBlock(Block block, double sizeX, double sizeY, double sizeZ)
	{
		renderBlock(block, sizeX, sizeY, sizeZ, 0);
	}
	
	public static void renderBlock(Block block, double sizeX, double sizeY, double sizeZ, int meta)
	{
		int blocksX = (int) Math.ceil(sizeX/1);
		int blocksY = (int) Math.ceil(sizeY/1);
		int blocksZ = (int) Math.ceil(sizeZ/1);
		GL11.glTranslated(-sizeX/2D+0.5, -sizeY/2D+0.5, -sizeZ/2D+0.5);
		
		boolean shouldX = false;
		boolean shouldY = false;
		boolean shouldZ = false;
		for (int blockX = 0; blockX < blocksX; blockX++) {
			for (int blockY = 0; blockY < blocksY; blockY++) {
				for (int blockZ = 0; blockZ < blocksZ; blockZ++) {				
					if(blockX > 0 && blockX < blocksX-1 && blockY > 0 && blockY < blocksY-1 && blockZ > 0 && blockZ < blocksZ-1)
						break;
					
					double boundX = 1;
					if(blockX == blocksX-1)
						boundX = sizeX-blockX;
					double boundY = 1;
					if(blockY == blocksY-1)
						boundY = sizeY-blockY;
					double boundZ = 1;
					if(blockZ == blocksZ-1)
						boundZ = sizeZ-blockZ;
					
					int moveX = 1;
					if(!shouldX)
						moveX = 0;
					int moveY = 1;
					if(!shouldY)
						moveY = 0;
					int moveZ = 1;
					if(!shouldZ)
						moveZ = 0;
					GL11.glTranslated(moveX, moveY, moveZ);
					GL11.glRotated(-90, 0, 1, 0);
					RenderHelper3D.renderer.setRenderBounds(0, 0, 0, boundX, boundY, boundZ);
					RenderHelper3D.renderer.lockBlockBounds = true;
					RenderHelper3D.renderBlock(block, meta);
					RenderHelper3D.renderer.lockBlockBounds = false;
					shouldX = false;
					shouldY = false;
					shouldZ = true;
				}
				GL11.glTranslated(0, 0, -(blocksZ-1));
				shouldZ = false;
				shouldY = true;
			}
			GL11.glTranslated(0, -(blocksY-1), 0);
			shouldY = false;
			shouldX = true;
		}
	}
	
	public static void renderBlock(Block block)
	{
		renderBlock(block, 0);
	}
	
	public static void renderBlock(Block block, int meta)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		renderer.useInventoryTint = false;
		float f5 = 1F;
		renderer.renderBlockAsItem(block, meta, f5);
	}
	
	public static void applyBlockRotation(RenderBlocks renderer, ForgeDirection direction)
	{
		//Default direction is East
		double minX = renderer.renderMinX-0.5D;
		double minY = renderer.renderMinY-0.5D;
		double minZ = renderer.renderMinZ-0.5D;
		double maxX = renderer.renderMaxX-0.5D;
		double maxY = renderer.renderMaxY-0.5D;
		double maxZ = renderer.renderMaxZ-0.5D;
		Vec3 min = RotationUtils.applyVectorRotation(Vec3.createVectorHelper(minX, minY, minZ), direction);
		Vec3 max = RotationUtils.applyVectorRotation(Vec3.createVectorHelper(maxX, maxY, maxZ), direction);
		
		min = min.addVector(0.5, 0.5, 0.5);
		max = max.addVector(0.5, 0.5, 0.5);
		
		if(min.xCoord < max.xCoord)
		{
			renderer.renderMinX = min.xCoord;
			renderer.renderMaxX = max.xCoord;
		}
		else
		{
			renderer.renderMinX = max.xCoord;
			renderer.renderMaxX = min.xCoord;
		}
		if(min.yCoord < max.yCoord)
		{
			renderer.renderMinY = min.yCoord;
			renderer.renderMaxY = max.yCoord;
		}
		else
		{
			renderer.renderMinY = max.yCoord;
			renderer.renderMaxY = min.yCoord;
		}
		if(min.zCoord < max.zCoord)
		{
			renderer.renderMinZ = min.zCoord;
			renderer.renderMaxZ = max.zCoord;
		}
		else
		{
			renderer.renderMinZ = max.zCoord;
			renderer.renderMaxZ = min.zCoord;
		}
	}
	
	public static void applyDirection(ForgeDirection direction)
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
	
	public static void renderItem(ItemStack stack, double x, double y, double z, double rotationX, double rotationY, double rotationZ, double size, ForgeDirection direction, double moveX, double moveY, double moveZ)
	{
		if (stack.getItem() != null)
        {
			ResourceLocation resourcelocation = mc.renderEngine.getResourceLocation(stack.getItemSpriteNumber());
		    mc.renderEngine.bindTexture(resourcelocation);
            TextureUtil.func_152777_a(false, false, 1.0F);
            //this.random.setSeed(187L);
            GL11.glPushMatrix();
            byte b0 = 1;

            GL11.glTranslatef((float)x+0.5F, (float)y+0.5F, (float)z+0.5F);
            
            //GL11.glTranslated(-moveX, -moveY, -moveZ);
            applyDirection(direction);
            GL11.glTranslated(moveX, moveY, moveZ);
            
            GL11.glRotated(rotationX, 1, 0, 0);
            GL11.glRotated(rotationY, 0, 1, 0);
            GL11.glRotated(rotationZ, 0, 0, 1);
            
            GL11.glScaled(size, size, size);
            
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float f6;
            float f7;
            int k;
            
            if (stack.getItemSpriteNumber() == 0 && stack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType()))
            {
                Block block = Block.getBlockFromItem(stack.getItem());
                float f9 = 0.25F;
                k = block.getRenderType();

                if (k == 1 || k == 19 || k == 12 || k == 2)
                {
                    f9 = 0.5F;
                }

                if (block.getRenderBlockPass() > 0)
                {
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glScalef(f9, f9, f9);

                for (int l = 0; l < b0; ++l)
                {
                    GL11.glPushMatrix();

                    renderer.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);
                    GL11.glPopMatrix();
                }

                if (block.getRenderBlockPass() > 0)
                {
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
            else
            {
                float f5;

                if (/*stack.getItemSpriteNumber() == 1 &&*/ stack.getItem().requiresMultipleRenderPasses())
                {
                    GL11.glScalef(0.5F, 0.5F, 0.5F);

                    for (int j = 0; j < stack.getItem().getRenderPasses(stack.getItemDamage()); ++j)
                    {
                        IIcon iicon1 = stack.getItem().getIcon(stack, j);
						k = stack.getItem().getColorFromItemStack(stack, j);
						f5 = (float)(k >> 16 & 255) / 255.0F;
						f6 = (float)(k >> 8 & 255) / 255.0F;
						f7 = (float)(k & 255) / 255.0F;
						GL11.glColor4f(f5, f6, f7, 1.0F);
						renderDroppedItem(stack, iicon1, b0, f5, f6, f7, j);
                    }
                }
                else
                {
                    if (stack != null && stack.getItem() instanceof ItemCloth)
                    {
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    GL11.glScalef(0.5F, 0.5F, 0.5F);

                    IIcon iicon = stack.getIconIndex();
                    
                    int i = stack.getItem().getColorFromItemStack(stack, 0);
                    float f4 = (float)(i >> 16 & 255) / 255.0F;
                    f5 = (float)(i >> 8 & 255) / 255.0F;
                    f6 = (float)(i & 255) / 255.0F;
                    renderDroppedItem(stack, iicon, b0, f4, f5, f6, 0);

                    if (stack != null && stack.getItem() instanceof ItemCloth)
                    {
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
		    mc.renderEngine.bindTexture(resourcelocation);
            TextureUtil.func_147945_b();
        }
	}
	
	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public static void renderDroppedItem(ItemStack stack, IIcon p_77020_2_, int p_77020_3_, float p_77020_5_, float p_77020_6_, float p_77020_7_, int pass)
    {
        Tessellator tessellator = Tessellator.instance;

        if (p_77020_2_ == null)
        {
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourcelocation = texturemanager.getResourceLocation(stack.getItemSpriteNumber());
            p_77020_2_ = ((TextureMap)texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        float f14 = ((IIcon)p_77020_2_).getMinU();
        float f15 = ((IIcon)p_77020_2_).getMaxU();
        float f4 = ((IIcon)p_77020_2_).getMinV();
        float f5 = ((IIcon)p_77020_2_).getMaxV();
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;
        float f10;

        GL11.glPushMatrix();

        float f9 = 0.0625F;
        f10 = 0.021875F;
        int j = stack.stackSize;
        byte b0 = 1;

        GL11.glTranslatef(-f7, -f8, -0.05F);

        for (int k = 0; k < b0; ++k)
        {
            GL11.glTranslatef(0f, 0f, f9 + f10);

            if (stack.getItemSpriteNumber() == 0)
            {
            	mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            }
            else
            {
            	mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
            }

            GL11.glColor4f(p_77020_5_, p_77020_6_, p_77020_7_, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, f15, f4, f14, f5, ((IIcon)p_77020_2_).getIconWidth(), ((IIcon)p_77020_2_).getIconHeight(), f9);

            if (stack.hasEffect(pass))
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                mc.renderEngine.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f11 = 0.76F;
                GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f12 = 0.125F;
                GL11.glScalef(f12, f12, f12);
                float f13 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f13, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f12, f12, f12);
                f13 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f13, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
        }

        GL11.glPopMatrix();
    }
	
	public static void renderTag(String text, double x, double y, double z, int distance)
    {
        double d3 = Math.sqrt(RenderManager.instance.getDistanceToCamera(x, y, z));

        //if (d3 <= (double)(distance * distance))
        //{
        FontRenderer fontrenderer = mc.fontRenderer;
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.0F, (float)y, (float)z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f1, -f1, f1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;

        if (text.equals("deadmau5"))
        {
            b0 = -10;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        int j = fontrenderer.getStringWidth(text) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
        tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
        tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
        tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, b0, 553648127);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, b0, -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        //}
    }
}
