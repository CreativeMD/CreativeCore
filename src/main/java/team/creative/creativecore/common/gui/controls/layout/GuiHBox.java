package team.creative.creativecore.common.gui.controls.layout;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.VAlign;

public class GuiHBox extends GuiLayoutControl {
    
    public GuiHBox(String name, int x, int y, Align align, VAlign valign) {
        super(name, x, y, 1, 1, align, valign);
    }
    
    public GuiHBox(String name, int x, int y, VAlign valign) {
        super(name, x, y, 1, 1, Align.LEFT, valign);
    }
    
    public GuiHBox(String name, int x, int y) {
        super(name, x, y, 1, 1, Align.LEFT, VAlign.TOP);
    }
    
    @Override
    public int getPreferredWidth() {
        if (align != Align.LEFT)
            return Integer.MAX_VALUE;
        int width = -spacing;
        for (GuiControl control : this)
            width += control.getPreferredWidth() + spacing;
        return width;
    }
    
    @Override
    public void updateLayout(int width, int height) {
        int availableWidth = width / size();
        int xOffset = 0;
        for (GuiControl control : this) {
            if (align == Align.STRETCH)
                control.setWidthLayout(availableWidth);
            else
                control.setWidthLayout(Math.min(control.getPreferredWidth(), availableWidth));
            control.setX(xOffset);
            xOffset += control.getWidth() + spacing;
        }
        for (GuiControl control : this) {
            if (valign == VAlign.STRETCH)
                control.setHeightLayout(height);
            else
                control.setHeightLayout(Math.min(control.getPreferredHeight(), height));
            switch (valign) {
            case TOP:
            case STRETCH:
                control.setY(0);
                break;
            case CENTER:
                control.setY(height / 2 - control.getHeight() / 2);
                break;
            case BOTTOM:
                control.setY(height - control.getHeight());
                break;
            }
        }
        
        xOffset -= spacing;
        
        if (align != Align.LEFT && align != Align.STRETCH) {
            for (GuiControl control : this) {
                int x = 0;
                switch (align) {
                case CENTER:
                    control.setX(availableWidth / 2 - xOffset / 2 + x);
                    break;
                case RIGHT:
                    control.setX(availableWidth - xOffset + x);
                    break;
                }
                x += control.getWidth() + spacing;
            }
        } else
            setWidth(xOffset);
        
        setHeight(height);
    }
    
    @Override
    public void setWidthLayout(int width) {
        int maxWidth = width / size();
        for (GuiControl control : this)
            if (align == Align.STRETCH)
                control.setWidthLayout(maxWidth);
            else
                control.setWidthLayout(Math.min(control.getPreferredWidth(), maxWidth));
        setWidth(width);
    }
    
    @Override
    public void setHeightLayout(int height) {
        updateLayout(getWidth(), height);
    }
    
}
