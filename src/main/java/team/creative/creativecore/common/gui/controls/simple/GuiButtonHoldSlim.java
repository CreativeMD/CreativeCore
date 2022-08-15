package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiButtonHoldSlim extends GuiButtonHold {
    
    public GuiButtonHoldSlim(String name, Consumer<Integer> pressed) {
        super(name, pressed);
    }
    
    public GuiButtonHoldSlim(String name, int width, int height, Consumer<Integer> pressed) {
        super(name, width, height, pressed);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        if (rect.inside(mouseX, mouseY))
            text.defaultColor = getStyle().fontColorHighlight.toInt();
        else
            text.defaultColor = getStyle().fontColor.toInt();
        super.renderContent(matrix, control, rect, mouseX, mouseY);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public int getMinWidth() {
        return -1;
    }
}