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
    
    public void setWidth(int width, int availableWidth) {
        int min = getMinWidth(availableWidth);
        if (min != -1)
            width = Math.max(width, min);
        int max = getMaxWidth(availableWidth);
        if (max != -1)
            width = Math.min(width, max);
        rect.maxX = rect.minX + width;
    }
    
    public void setHeight(int height, int availableHeight) {
        int min = getMinHeight(availableHeight);
        if (min != -1)
            height = Math.max(height, min);
        int max = getMaxHeight(availableHeight);
        if (max != -1)
            height = Math.min(height, max);
        rect.maxY = rect.minY + height;
    }
    
    public int addWidth(int additional, int availableWidth) {
        int before = getWidth();
        setWidth(getWidth() + additional, availableWidth);
        return getWidth() - before;
    }
    
    public int addHeight(int additional, int availableHeight) {
        int before = getHeight();
        setHeight(getHeight() + additional, availableHeight);
        return getHeight() - before;
    }
    
    public boolean isMaxWidth(int availableWidth) {
        if (getMaxWidth(availableWidth) != -1)
            return getWidth() >= getMaxWidth(availableWidth);
        return false;
    }
    
    public boolean isMaxHeight(int availableHeight) {
        if (getMaxHeight(availableHeight) != -1)
            return getHeight() >= getMaxHeight(availableHeight);
        return false;
    }
    
    public int getMinWidth(int availableWidth) {
        int min = control.getMinWidth(availableWidth);
        if (min != -1)
            return min + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getMaxWidth(int availableWidth) {
        int max = control.getMaxWidth(availableWidth);
        if (max != -1)
            return max + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getPreferredWidth(int availableWidth) {
        return control.getPreferredWidth(availableWidth) + control.getContentOffset() * 2;
    }
    
    public int getMinHeight(int availableHeight) {
        int min = control.getMinHeight(getContentWidth(), availableHeight);
        if (min != -1)
            return min + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getMaxHeight(int availableHeight) {
        int max = control.getMaxHeight(getContentWidth(), availableHeight);
        if (max != -1)
            return max + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getPreferredHeight(int availableHeight) {
        return control.getPreferredHeight(getContentWidth(), availableHeight) + control.getContentOffset() * 2;
    }
    
    public void flowX() {
        int width = getContentWidth();
        control.flowX(width, control.preferredWidth(width));
    }
    
    public void flowY() {
        int width = getContentWidth();
        int height = getContentHeight();
        control.flowY(width, height, control.preferredHeight(width, height));
    }
    
    public int getBottom() {
        return (int) rect.maxY;
    }
    
    public boolean isMouseOver(double x, double y) {
        return rect.inside(x, y);
    }
    
}
