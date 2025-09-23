package team.creative.creativecore.client.gui.control.inventory;

import java.util.List;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.inventory.GuiSlotBase;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiClientSlotBase<T extends GuiSlotBase> extends GuiClientControl<T> {
    
    public static final int SLOT_SIZE = 18;
    public static final DisplayColor HOVER = new DisplayColor(1, 1, 1, 0.3F);
    
    public GuiClientSlotBase(T control) {
        super(control);
    }
    
    protected abstract ItemStack getStackToRender();
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.SLOT;
    }
    
    @Override
    public List<Component> getTooltip() {
        if (control.getStack().isEmpty())
            return super.getTooltip();
        return control.getStack().getTooltipLines(TooltipContext.of(control.provider()), control.getPlayer(), TooltipFlag.Default.NORMAL);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        pose.translate(1, 1);
        ItemStack stack = getStackToRender();
        graphics.renderItem(stack, 0, 0);
        ((CreativeGuiGraphics) graphics).renderItemDecorations(stack, 0, 0);
        pose.translate(-1, -1);
        if (realRect.inside(mouseX, mouseY) && enabled)
            HOVER.render(graphics, rect.getWidth(), rect.getHeight());
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return true;
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int maxWidth(int availableWidth) {
        return SLOT_SIZE;
    }
    
    @Override
    protected int maxHeight(int width, int availableHeight) {
        return SLOT_SIZE;
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return SLOT_SIZE;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return SLOT_SIZE;
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return SLOT_SIZE;
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return SLOT_SIZE;
    }
    
}
