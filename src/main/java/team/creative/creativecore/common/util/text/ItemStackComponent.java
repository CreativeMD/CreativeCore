package team.creative.creativecore.common.util.text;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.GuiRenderHelper;

public class ItemStackComponent extends MutableComponentCreative implements AdvancedComponent {
    
    public final ItemStack stack;
    
    public ItemStackComponent(ItemStack stack) {
        super(ComponentContents.EMPTY, Lists.newArrayList(), Style.EMPTY);
        this.stack = stack;
    }
    
    @Override
    public int getWidth(Font font) {
        return 16;
    }
    
    @Override
    public int getHeight(Font font) {
        return 12;
    }
    
    @Override
    public boolean canSplit() {
        return false;
    }
    
    @Override
    public List<AdvancedComponent> split(int width, boolean force) {
        return null;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public void render(PoseStack stack, Font font, int defaultColor) {
        stack.pushPose();
        stack.translate(-2, -2, 10);
        GuiRenderHelper.drawItemStack(stack, this.stack, 1);
        stack.popPose();
    }
    
    @Override
    public MutableComponent plainCopy() {
        return null; // new ItemStackComponent(stack);
    }
    
}
