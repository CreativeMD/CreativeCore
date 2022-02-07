package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiShowItem extends GuiControl {
    
    public ItemStack stack;
    
    public GuiShowItem(String name) {
        super(name);
    }
    
    public GuiShowItem(String name, ItemStack stack) {
        super(name);
        this.stack = stack;
    }
    
    public GuiShowItem(String name, int width, int height) {
        super(name, width, height);
    }
    
    public GuiShowItem(String name, int width, int height, ItemStack stack) {
        super(name, width, height);
        this.stack = stack;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int height, int preferred) {}
    
    @Override
    protected int preferredWidth() {
        return 16;
    }
    
    @Override
    protected int preferredHeight() {
        return 16;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        float scale = (float) Math.min(rect.getWidth() / 16, rect.getHeight() / 16);
        pose.scale(scale, scale, 1);
        GuiRenderHelper.drawItemStack(pose, stack, 1);
    }
    
}
