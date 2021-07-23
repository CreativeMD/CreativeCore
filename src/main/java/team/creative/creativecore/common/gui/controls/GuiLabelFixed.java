package team.creative.creativecore.common.gui.controls;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiLabelFixed extends GuiLabel {
    
    private int initalWidth;
    private int initalHeight;
    
    public GuiLabelFixed(String name, int x, int y, int width, int height) {
        super(name, x, y);
        this.initalWidth = width;
        this.initalHeight = height;
        setWidth(width);
        setHeight(height);
    }
    
    @Override
    protected void updateDimension() {
        setWidth(initalWidth);
        setHeight(initalHeight);
        text.calculateDimensions();
    }
    
    @Override
    public int getMaxWidth() {
        return initalWidth;
    }
    
    @Override
    public int getPreferredWidth() {
        return initalWidth;
    }
    
    @Override
    public int getMinWidth() {
        return initalWidth;
    }
    
    @Override
    public int getMaxHeight() {
        return initalHeight;
    }
    
    @Override
    public int getPreferredHeight() {
        return initalHeight;
    }
    
    @Override
    public int getMinHeight() {
        return initalHeight;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, Rect rect, int mouseX, int mouseY) {
        matrix.translate(rect.getWidth() / 2 - text.usedWidth / 2, rect.getHeight() / 2 - text.usedHeight / 2, 0);
        text.render(matrix);
    }
    
}
