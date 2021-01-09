package com.creativemd.creativecore.client.avatar;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

import net.minecraft.item.ItemStack;

public class AvatarItemStack extends Avatar {
    
    public ItemStack stack;
    
    public AvatarItemStack(ItemStack stack) {
        this.stack = stack;
    }
    
    @Override
    public void handleRendering(GuiRenderHelper helper, int width, int height) {
        helper.drawItemStack(stack, 0, 0, width, height);
    }
}
