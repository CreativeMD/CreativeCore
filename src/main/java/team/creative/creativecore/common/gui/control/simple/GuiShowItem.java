package team.creative.creativecore.common.gui.control.simple;

import org.joml.Matrix3x2fStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiShowItem extends GuiControl {
    
    public ItemStack stack = ItemStack.EMPTY;
    
    public GuiShowItem(String name) {
        super(name);
    }
    
    public GuiShowItem(String name, ItemStack stack) {
        super(name);
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
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 16;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 16;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        float scale = Math.min(rect.getContentWidth() / 16, rect.getContentHeight() / 16);
        Matrix3x2fStack pose = graphics.pose();
        pose.scale(scale, scale);
        graphics.renderItem(stack, 0, 0);
    }
    
}
