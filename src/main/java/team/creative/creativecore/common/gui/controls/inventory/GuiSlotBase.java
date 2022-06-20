package team.creative.creativecore.common.gui.controls.inventory;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiSlotBase extends GuiControl {
    
    public static final int SLOT_SIZE = 18;
    private static DisplayColor hover = new DisplayColor(1, 1, 1, 0.2F);
    
    public GuiSlotBase(String name) {
        super(name);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.SLOT;
    }
    
    public abstract ItemStack getStack();
    
    @Override
    public List<Component> getTooltip() {
        if (getStack().isEmpty())
            return super.getTooltip();
        return getStack().getTooltipLines(getPlayer(), TooltipFlag.Default.NORMAL);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        matrix.translate(0, 0, 10);
        GuiRenderHelper.drawItemStack(matrix, getStack(), 1F);
        GuiRenderHelper.drawItemStackDecorations(matrix, getStack());
        matrix.translate(0, 0, 10);
        if (rect.inside(mouseX, mouseY))
            hover.render(matrix, rect.getWidth(), rect.getHeight());
        
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        return true;
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int height, int preferred) {}
    
    @Override
    public int getMaxWidth() {
        return 18;
    }
    
    @Override
    public int getMaxHeight() {
        return 18;
    }
    
    @Override
    protected int preferredWidth() {
        return 18;
    }
    
    @Override
    protected int preferredHeight() {
        return 18;
    }
    
    @Override
    public int getMinWidth() {
        return 18;
    }
    
    @Override
    public int getMinHeight() {
        return 18;
    }
}
