package team.creative.creativecore.common.gui.controls;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.math.geo.Rect;

public class SlotViewer extends GuiControlBasic {
    
    private static DisplayColor hover = new DisplayColor(1, 1, 1, 0.2F);
    
    public ItemStack stack;
    
    public SlotViewer(String name, int x, int y, ItemStack stack) {
        super(name, x, y, 18, 18);
        this.stack = stack;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.SLIDER;
    }
    
    @Override
    public List<Component> getTooltip() {
        return stack.getTooltipLines(getPlayer(), TooltipFlag.Default.NORMAL);
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, Rect rect, int mouseX, int mouseY) {
        matrix.translate(0, 0, 10);
        GuiRenderHelper.drawItemStack(matrix, stack);
        matrix.translate(0, 0, 10);
        if (rect.inside(mouseX, mouseY))
            hover.render(matrix, rect.getWidth(), rect.getHeight());
        
    }
    
}
