package com.creativemd.creativecore.client.rendering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Color4b;
import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.core.CreativeCore;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper2D {
	
	public static RenderItem renderer = RenderItem.getInstance();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static int zLevel = 0;
	
	public static final ResourceLocation tabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	public static final ResourceLocation tab_item_search = new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png");
	
	public static void renderItem(ItemStack stack, int x, int y)
	{
		renderItem(stack, x, y, 1.0);
	}
	
	public static void renderItem(ItemStack stack, int x, int y, double alpha, double rotation)
	{
		renderItem(stack, x, y, alpha, rotation, 16, 16);
	}
	
	public static void renderItem(ItemStack stack, int x, int y, double alpha, double rotation, double sizeX, double sizeY)
	{
		if(stack != null)
			renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, x, y, true, alpha, rotation, sizeX, sizeY);
	}
	
	public static void renderItem(ItemStack stack, int x, int y, double alpha)
	{
		renderItem(stack, x, y, alpha, 0);
	}
	
	public static void renderIcon(IIcon icon, int x, int y, double alpha, boolean isBlock, double rotation, double sizeX, double sizeY)
	{
		GL11.glPushMatrix();
		//GL11.glTranslated(x, y, 0);
		
		GL11.glTranslated(x+sizeX/2, y+sizeY/2, 0);
		GL11.glRotated(rotation, 0, 0, 1);
		GL11.glTranslated(-x-sizeX/2, -y-sizeY/2, 0);
        
        //GL11.glTranslated(x, y, 0);
        
		GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        int sprite = 1;
        if(isBlock)
        	sprite = 0;
        ResourceLocation resourcelocation = mc.renderEngine.getResourceLocation(sprite);
        mc.renderEngine.bindTexture(resourcelocation);
        
        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        
        GL11.glColor4d(1, 1, 1, alpha);
        
        GL11.glTranslated(0, 0, zLevel);
        renderer.renderIcon(x, y, icon, (int)sizeX, (int)sizeY);
        GL11.glTranslated(0, 0, -zLevel);
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        
        GL11.glEnable(GL11.GL_LIGHTING);
        
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
	}
	
	public static void renderItemIntoGUI(FontRenderer font, TextureManager texture, ItemStack stack, int x, int y, boolean renderEffect, double alpha, double rotation, double sizeX, double sizeY)
    {
		GL11.glPushMatrix();
        int k = stack.getItemDamage();
        Object object = null;
        try{
        	object = stack.getIconIndex();
        }catch(Exception e){
        	object = null;
        }
        int l;
        float f;
        float f3;
        float f4;
        GL11.glTranslated(x+8, y+8, 0);
        GL11.glScaled((double)sizeX/16D, (double)sizeY/16D, 1);
        GL11.glRotated(rotation, 0, 0, 1);
        GL11.glTranslated(-x-8, -y-8, 0);
        GL11.glColor4d(1, 1, 1, alpha);
        try{
	        if (stack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType()))
	        {
	            texture.bindTexture(TextureMap.locationBlocksTexture);
	            Block block = Block.getBlockFromItem(stack.getItem());
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	            GL11.glEnable(GL11.GL_BLEND);
	            //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            if (block.getRenderBlockPass() != 0)
	            {
	                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	                GL11.glEnable(GL11.GL_BLEND);
	                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            }
	            else
	            {
	                GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
	                GL11.glDisable(GL11.GL_BLEND);
	            }
	            
	            GL11.glPushMatrix();
	            GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0F + renderer.zLevel);
	            GL11.glScalef(10.0F, 10.0F, 10.0F);
	            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
	            GL11.glScalef(1.0F, 1.0F, -1.0F);
	            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
	            l = stack.getItem().getColorFromItemStack(stack, 0);
	            f3 = (float)(l >> 16 & 255) / 255.0F;
	            f4 = (float)(l >> 8 & 255) / 255.0F;
	            f = (float)(l & 255) / 255.0F;
	            
	            if (renderer.renderWithColor)
	            {
	                GL11.glColor4f(f3, f4, f, (float)alpha);
	            }else{
	            	GL11.glColor4f(1, 1, 1, (float)alpha);
	            }
	            
	            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
	            GL11.glEnable(GL11.GL_LIGHTING);
	            RenderBlocks.getInstance().useInventoryTint = true;
	            RenderBlocks.getInstance().renderBlockAsItem(block, k, 1.0F);
	            RenderBlocks.getInstance().useInventoryTint = true;
	            GL11.glDisable(GL11.GL_LIGHTING);
	            if (block.getRenderBlockPass() == 0)
	            {
	                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	            }
	            
	            GL11.glPopMatrix();
	        }
	        else if (stack.getItem().requiresMultipleRenderPasses())
	        {
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            texture.bindTexture(TextureMap.locationItemsTexture);
	            GL11.glDisable(GL11.GL_ALPHA_TEST);
	            GL11.glDisable(GL11.GL_TEXTURE_2D);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(0, 0, 0, 0);
	            GL11.glColorMask(false, false, false, true);
	            GL11.glColor3f(1.0F, 1.0F, 1.0F);
	            Tessellator tessellator = Tessellator.instance;
	            tessellator.startDrawingQuads();
	            tessellator.setColorOpaque_I(-1);
	            tessellator.addVertex((double)(x - 2), (double)(y + 18), (double)renderer.zLevel);
	            tessellator.addVertex((double)(x + 18), (double)(y + 18), (double)renderer.zLevel);
	            tessellator.addVertex((double)(x + 18), (double)(y - 2), (double)renderer.zLevel);
	            tessellator.addVertex((double)(x - 2), (double)(y - 2), (double)renderer.zLevel);
	            tessellator.draw();
	            GL11.glColorMask(true, true, true, true);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            
	            Item item = stack.getItem();
	            for (l = 0; l < item.getRenderPasses(k); ++l)
	            {
	                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	                texture.bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
	                IIcon iicon = item.getIcon(stack, l);
	                int i1 = stack.getItem().getColorFromItemStack(stack, l);
	                f = (float)(i1 >> 16 & 255) / 255.0F;
	                float f1 = (float)(i1 >> 8 & 255) / 255.0F;
	                float f2 = (float)(i1 & 255) / 255.0F;
	                
	                if (renderer.renderWithColor)
	                {
	                    GL11.glColor3f(f, f1, f2);
	                }
	                
	                GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
	                GL11.glEnable(GL11.GL_ALPHA_TEST);
	                
	                renderer.renderIcon(x, y, iicon, 16, 16);
	                
	                GL11.glDisable(GL11.GL_ALPHA_TEST);
	                GL11.glEnable(GL11.GL_LIGHTING);
	
	                if (renderEffect && stack.hasEffect(l))
	                {
	                    renderer.renderEffect(texture, x, y);
	                }
	            }
	
	            GL11.glEnable(GL11.GL_LIGHTING);
	        }
	        else
	        {
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            ResourceLocation resourcelocation = texture.getResourceLocation(stack.getItemSpriteNumber());
	           
	            texture.bindTexture(resourcelocation);
	            
	            if (object == null)
	            {
	                object = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
	            }
	            
	            l = stack.getItem().getColorFromItemStack(stack, 0);
	            f3 = (float)(l >> 16 & 255) / 255.0F;
	            f4 = (float)(l >> 8 & 255) / 255.0F;
	            f = (float)(l & 255) / 255.0F;
	            
	            if (renderer.renderWithColor)
	            {
	                GL11.glColor4f(f3, f4, f, (float)alpha);
	            }
	            
	            GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
	            //GL11.glAlphaFunc(0, 1);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            GL11.glEnable(GL11.GL_BLEND);
	            
	            renderer.renderIcon(x, y, (IIcon)object, 16, 16);
	            
	            GL11.glEnable(GL11.GL_LIGHTING);
	            GL11.glDisable(GL11.GL_ALPHA_TEST);
	            GL11.glDisable(GL11.GL_BLEND);
	            
	            if (renderEffect && stack.hasEffect(0))
	            {
	            	renderer.renderEffect(texture, x, y);
	            }
	            GL11.glEnable(GL11.GL_LIGHTING);
	        }
        }catch(Exception e){
        	
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
	
	/**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int x1, int y1, int x2, int y2, Vec3 color, double alpha)
    {
    	//GL11.glTranslated(mc.displayWidth/2, mc.displayHeight/2, 0);
    	GL11.glTranslated(0, 0, 0);
    	GL11.glPushMatrix();
    	//GL11.glTranslated(mc.displayWidth/4, mc.displayHeight/4, 0);
    	/*x1 += mc.displayWidth/4;
    	x2 += mc.displayWidth/4;
    	y1 += mc.displayHeight/4;
    	y2 += mc.displayHeight/4;*/
        int j1;

        if (x1 < x2)
        {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (y1 < y2)
        {
            j1 = y1;
            y1 = y2;
            y2 = j1;
        }
        
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4d(color.xCoord, color.yCoord, color.zCoord, alpha);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)x1, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y1, 0.0D);
        tessellator.addVertex((double)x1, (double)y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     */
    public static void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_)
    {
        float f = (float)(p_73733_5_ >> 24 & 255) / 255.0F;
        float f1 = (float)(p_73733_5_ >> 16 & 255) / 255.0F;
        float f2 = (float)(p_73733_5_ >> 8 & 255) / 255.0F;
        float f3 = (float)(p_73733_5_ & 255) / 255.0F;
        float f4 = (float)(p_73733_6_ >> 24 & 255) / 255.0F;
        float f5 = (float)(p_73733_6_ >> 16 & 255) / 255.0F;
        float f6 = (float)(p_73733_6_ >> 8 & 255) / 255.0F;
        float f7 = (float)(p_73733_6_ & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)p_73733_3_, (double)p_73733_2_, (double)zLevel);
        tessellator.addVertex((double)p_73733_1_, (double)p_73733_2_, (double)zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)p_73733_1_, (double)p_73733_4_, (double)zLevel);
        tessellator.addVertex((double)p_73733_3_, (double)p_73733_4_, (double)zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     */
    public static void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, Vector4d color1, Vector4d color2)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA((int)color1.x, (int)color1.y, (int)color1.z, (int)color1.w);
        tessellator.addVertex((double)p_73733_3_, (double)p_73733_2_, (double)zLevel);
        tessellator.addVertex((double)p_73733_1_, (double)p_73733_2_, (double)zLevel);
        tessellator.setColorRGBA((int)color2.x, (int)color2.y, (int)color2.z, (int)color2.w);
        tessellator.addVertex((double)p_73733_1_, (double)p_73733_4_, (double)zLevel);
        tessellator.addVertex((double)p_73733_3_, (double)p_73733_4_, (double)zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public static void drawTexturedModalRect(double p_73729_1_, double p_73729_2_, double p_73729_3_, double p_73729_4_, double p_73729_5_, double p_73729_6_)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), (double)zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), (double)zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), (double)zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), (double)zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.draw();
    }
    
    public static void drawTexturedModalRect(int zLevel, int x, int y, int u, int v, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)zLevel, (double)((float)(u + 0) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)zLevel, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)zLevel, (double)((float)(u + width) * f), (double)((float)(v + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)zLevel, (double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
        tessellator.draw();
    }
    
    public static void drawHoveringText(List strings, int x, int y, FontRenderer font, int width, int height)
    {
        if (!strings.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = strings.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;

            if (strings.size() > 1)
            {
                i1 += 2 + (strings.size() - 1) * 10;
            }

            if (j2 + k > width)
            {
                j2 -= 28 + k;
            }

            /*if (k2 + i1 + 6 > height)
            {
                k2 = height - i1 - 6;
            }*/

            zLevel = 300;
            int j1 = -267386864;
            drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < strings.size(); ++i2)
            {
                String s1 = (String)strings.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                {
                    k2 += 2;
                }

                k2 += 10;
            }

            zLevel = 0;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
    
    public static void renderInventoryCubes(RenderBlocks renderer, ArrayList<CubeObject> cubes, Block parBlock, int meta)
    {
    	 
    	Tessellator tesselator = Tessellator.instance;
    	for (int i = 0; i < cubes.size(); i++)
        {
    		Block block = parBlock;
			renderer.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
            if(cubes.get(i).block != null)
            {
            	block = cubes.get(i).block;
            	meta = 0;
            }
            if(cubes.get(i).icon != null){
            	GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                tesselator.startDrawingQuads();
                tesselator.setNormal(0.0F, -1.0F, 0.0F);
                renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
                tesselator.draw();
                tesselator.startDrawingQuads();
                tesselator.setNormal(0.0F, 1.0F, 0.0F);
                renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
                tesselator.draw();
                tesselator.startDrawingQuads();
                tesselator.setNormal(0.0F, 0.0F, -1.0F);
                renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
                tesselator.draw();
                tesselator.startDrawingQuads();
                tesselator.setNormal(0.0F, 0.0F, 1.0F);
                renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
                tesselator.draw();
                tesselator.startDrawingQuads();
                tesselator.setNormal(-1.0F, 0.0F, 0.0F);
                renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
                tesselator.draw();
                tesselator.startDrawingQuads();
                tesselator.setNormal(1.0F, 0.0F, 0.0F);
                renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
                tesselator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }else{
	            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	            tesselator.startDrawingQuads();
	            tesselator.setNormal(0.0F, -1.0F, 0.0F);
	            renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
	            tesselator.draw();
	            tesselator.startDrawingQuads();
	            tesselator.setNormal(0.0F, 1.0F, 0.0F);
	            renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
	            tesselator.draw();
	            tesselator.startDrawingQuads();
	            tesselator.setNormal(0.0F, 0.0F, -1.0F);
	            renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
	            tesselator.draw();
	            tesselator.startDrawingQuads();
	            tesselator.setNormal(0.0F, 0.0F, 1.0F);
	            renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
	            tesselator.draw();
	            tesselator.startDrawingQuads();
	            tesselator.setNormal(-1.0F, 0.0F, 0.0F);
	            renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
	            tesselator.draw();
	            tesselator.startDrawingQuads();
	            tesselator.setNormal(1.0F, 0.0F, 0.0F);
	            renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
	            tesselator.draw();
	            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
        }
    }
    
    public static void renderScrollBar(int posX, int posY, double percent, int height, boolean isDisabled)
    {
    	 GL11.glEnable(GL11.GL_BLEND);
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        mc.getTextureManager().bindTexture(tab_item_search);
    	drawTexturedModalRect(posX, posY, 174, 17, 14, 15);
    	
    	int maxSize = 110;
    	int amount = (int) Math.ceil((double)(height-2D)/110D);
    	for (int i = 0; i < amount; i++) {
    		int tempHeight = Math.min(maxSize, (height-17)-i*maxSize);
    		drawTexturedModalRect(posX, posY+15+i*maxSize, 174, 18, 14, tempHeight);
		}
    	
    	
    	drawTexturedModalRect(posX, posY+height-15, 174, 114, 14, 15);
    	
    	mc.getTextureManager().bindTexture(tabs);
    	drawTexturedModalRect(posX+1, posY+1+(height-2-15)*percent, isDisabled ? 232+12 : 232, 0, 12, 15);
    }
}
