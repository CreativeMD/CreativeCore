package team.creative.creativecore.common.gui.controls.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.VAlign;

public class GuiLeftRightBox extends GuiHBox {
    
    protected List<GuiControl> rightBound = new ArrayList<>();
    
    public GuiLeftRightBox(String name, int x, int y, VAlign valign) {
        super(name, x, y, Align.LEFT, valign);
    }
    
    public GuiLeftRightBox(String name, int x, int y) {
        super(name, x, y, Align.LEFT, VAlign.TOP);
    }
    
    @Override
    public void clear() {
        super.clear();
        rightBound.clear();
    }
    
    @Override
    public void remove(GuiControl control) {
        super.remove(control);
        rightBound.remove(control);
    }
    
    @Override
    public void remove(String... include) {
        super.remove(include);
        rightBound.removeIf((x) -> ArrayUtils.contains(include, x.name));
    }
    
    @Override
    public void removeExclude(String... exclude) {
        super.removeExclude(exclude);
        rightBound.removeIf((x) -> !ArrayUtils.contains(exclude, x.name));
    }
    
    public void addRight(GuiControl control) {
        add(control);
        rightBound.add(control);
    }
    
    @Override
    protected void updatePositions(int availableWidth, int xOffset) {
        if (align != Align.LEFT)
            super.updatePositions(availableWidth, xOffset);
        else {
            int leftSize = -spacing;
            int rightSize = -spacing;
            for (GuiControl control : this) {
                if (rightBound.contains(control))
                    rightSize += control.getWidth() + spacing;
                else
                    leftSize += control.getWidth() + spacing;
            }
            int xLeft = 0;
            int xRight = availableWidth - rightSize;
            for (GuiControl control : this) {
                if (rightBound.contains(control)) {
                    control.setX(xRight);
                    xRight += control.getWidth() + spacing;
                } else {
                    switch (align) {
                    case LEFT:
                        control.setX(xLeft);
                        break;
                    case CENTER:
                        control.setX(availableWidth / 2 - leftSize / 2 + xLeft);
                        break;
                    case RIGHT:
                        control.setX(availableWidth - leftSize + xLeft);
                        break;
                    case STRETCH:
                        break;
                    default:
                        break;
                    }
                    xLeft += control.getWidth() + spacing;
                }
            }
            setWidth(availableWidth);
        }
        
    }
    
}
