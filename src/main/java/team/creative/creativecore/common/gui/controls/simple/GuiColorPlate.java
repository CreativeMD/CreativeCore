package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiColorPlate extends GuiControl {
    
    private Color color;
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private DisplayColor colorPlate;
    
    public GuiColorPlate(String name, Color color) {
        super(name);
        
        if (isClient())
            setColor(color);
    }
    
    public void setColor(Color color) {
        this.color = color;
        this.colorPlate = new DisplayColor(color);
    }
    
    public Color getColor() {
        return color;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED_NO_PADDING;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        getStyle().transparencyBackground.render(pose, rect.getWidth(), rect.getHeight());
        colorPlate.set(color);
        colorPlate.render(pose, rect.getWidth(), rect.getHeight());
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
        return 20;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 20;
    }
    
}