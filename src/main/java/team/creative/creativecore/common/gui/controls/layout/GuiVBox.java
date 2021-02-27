package team.creative.creativecore.common.gui.controls.layout;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.VAlign;

public class GuiVBox extends GuiLayoutControl {
    
    public GuiVBox(String name, int x, int y, Align align, VAlign valign) {
        super(name, x, y, 1, 1, align, valign);
    }
    
    public GuiVBox(String name, int x, int y, Align align) {
        super(name, x, y, 1, 1, align, VAlign.TOP);
    }
    
    public GuiVBox(String name, int x, int y) {
        super(name, x, y, 1, 1, Align.LEFT, VAlign.TOP);
    }
    
    @Override
    public int getPreferredHeight() {
        if (valign != VAlign.TOP)
            return Integer.MAX_VALUE;
        int height = -spacing;
        for (GuiControl control : this)
            height += control.getPreferredHeight() + spacing;
        return height;
    }
    
    @Override
    public void updateLayout(int width, int height) {
        for (GuiControl control : this)
            if (align == Align.STRETCH)
                control.setWidthLayout(width);
            else
                control.setWidthLayout(Math.min(control.getPreferredWidth(), width));
        int availableHeight = height / size();
        int yOffset = 0;
        for (GuiControl control : this) {
            if (valign == VAlign.STRETCH)
                control.setHeightLayout(availableHeight);
            else
                control.setHeightLayout(Math.min(control.getPreferredHeight(), availableHeight));
            switch (align) {
            case LEFT:
                control.setX(0);
                break;
            case CENTER:
                control.setX(width / 2 - control.getWidth() / 2);
                break;
            case RIGHT:
                control.setX(width - control.getWidth());
                break;
            }
            control.setY(yOffset);
            yOffset += control.getHeight() + spacing;
        }
        
        yOffset -= spacing;
        if (valign != VAlign.TOP && valign != VAlign.STRETCH) {
            int y = 0;
            for (GuiControl control : this) {
                switch (valign) {
                case CENTER:
                    control.setY(height / 2 - yOffset / 2 + y);
                    break;
                case BOTTOM:
                    control.setY(height - yOffset + y);
                    break;
                }
                y += control.getHeight() + spacing;
            }
            setHeight(height);
        } else
            setHeight(yOffset);
        
        setWidth(width);
    }
    
    @Override
    public void setWidthLayout(int width) {
        for (GuiControl control : this)
            if (align == Align.STRETCH)
                control.setWidthLayout(width);
            else
                control.setWidthLayout(Math.min(control.getPreferredWidth(), width));
        setWidth(width);
    }
    
    @Override
    public void setHeightLayout(int height) {
        updateLayout(getWidth(), height);
    }
}
