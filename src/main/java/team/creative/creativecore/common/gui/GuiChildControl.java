package team.creative.creativecore.common.gui;

import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiChildControl {
    
    public final GuiControl control;
    public Rect rect;
    
    public GuiChildControl(GuiControl control) {
        this.control = control;
        this.rect = new Rect(0, 0, 0, 0);
    }
    
    public int getX() {
        return (int) rect.minX;
    }
    
    public void setX(int x) {
        int width = getWidth();
        rect.minX = x;
        rect.maxX = x + width;
    }
    
    public int getY() {
        return (int) rect.minY;
    }
    
    public void setY(int y) {
        int height = getHeight();
        rect.minY = y;
        rect.maxY = y + height;
    }
    
    public int getWidth() {
        return (int) rect.getWidth();
    }
    
    public int getHeight() {
        return (int) rect.getHeight();
    }
    
    public int getContentWidth() {
        return (int) rect.getWidth() - control.getContentOffset() * 2;
    }
    
    public int getContentHeight() {
        return (int) rect.getHeight() - control.getContentOffset() * 2;
    }
    
    public void setWidth(int width) {
        int min = getMinWidth();
        if (min != -1)
            width = Math.max(width, min);
        int max = getMaxWidth();
        if (max != -1)
            width = Math.min(width, max);
        rect.maxX = rect.minX + width;
    }
    
    public void setHeight(int height) {
        int min = getMinHeight();
        if (min != -1)
            height = Math.max(height, min);
        int max = getMaxHeight();
        if (max != -1)
            height = Math.min(height, max);
        rect.maxY = rect.minY + height;
    }
    
    public int addWidth(int additional) {
        int before = getWidth();
        setWidth(getWidth() + additional);
        return getWidth() - before;
    }
    
    public int addHeight(int additional) {
        int before = getHeight();
        setHeight(getHeight() + additional);
        return getHeight() - before;
    }
    
    public boolean isMaxWidth() {
        if (getMaxWidth() != -1)
            return getWidth() >= getMaxWidth();
        return false;
    }
    
    public boolean isMaxHeight() {
        if (getMaxHeight() != -1)
            return getHeight() >= getMaxHeight();
        return false;
    }
    
    public int getMinWidth() {
        int min = control.getMinWidth();
        if (min != -1)
            return min + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getMaxWidth() {
        int max = control.getMaxWidth();
        if (max != -1)
            return max + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getPreferredWidth() {
        return control.getPreferredWidth() + control.getContentOffset() * 2;
    }
    
    public int getMinHeight() {
        int min = control.getMinHeight();
        if (min != -1)
            return min + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getMaxHeight() {
        int max = control.getMaxHeight();
        if (max != -1)
            return max + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getPreferredHeight() {
        return control.getPreferredHeight() + control.getContentOffset() * 2;
    }
    
    public void flowX() {
        control.flowX(getWidth() - control.getContentOffset() * 2, control.preferredWidth());
    }
    
    public void flowY() {
        control.flowY(getHeight() - control.getContentOffset() * 2, control.preferredHeight());
    }
    
    public int getBottom() {
        return (int) rect.maxY;
    }
    
    public boolean isMouseOver(double x, double y) {
        return rect.inside(x, y);
    }
    
}
