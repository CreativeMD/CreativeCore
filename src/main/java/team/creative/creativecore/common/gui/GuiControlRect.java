package team.creative.creativecore.common.gui;

import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiControlRect {
    
    public static int getMinWidth(GuiControl control, int availableWidth) {
        if (control.preferred != null) {
            int minWidth = control.preferred.minWidth(control, availableWidth);
            if (minWidth != -1)
                return minWidth;
        }
        return control.minWidth(availableWidth);
    }
    
    public static int getPreferredWidth(GuiControl control, int availableWidth) {
        if (control.preferred != null) {
            int prefWidth = control.preferred.preferredWidth(control, availableWidth);
            if (prefWidth != -1)
                return prefWidth;
        }
        return control.preferredWidth(availableWidth);
    }
    
    public static int getMaxWidth(GuiControl control, int availableWidth) {
        if (control.preferred != null) {
            int maxWidth = control.preferred.maxWidth(control, availableWidth);
            if (maxWidth != -1)
                return maxWidth;
        }
        return control.maxWidth(availableWidth);
    }
    
    public static int getMinHeight(GuiControl control, int width, int availableHeight) {
        if (control.preferred != null) {
            int minHeight = control.preferred.minHeight(control, width, availableHeight);
            if (minHeight != -1)
                return minHeight;
        }
        return control.minHeight(width, availableHeight);
    }
    
    public static int getPreferredHeight(GuiControl control, int width, int availableHeight) {
        if (control.preferred != null) {
            int prefHeight = control.preferred.preferredHeight(control, width, availableHeight);
            if (prefHeight != -1)
                return prefHeight;
        }
        return control.preferredHeight(width, availableHeight);
    }
    
    public static int getMaxHeight(GuiControl control, int width, int availableHeight) {
        if (control.preferred != null) {
            int maxHeight = control.preferred.maxHeight(control, width, availableHeight);
            if (maxHeight != -1)
                return maxHeight;
        }
        return control.maxHeight(width, availableHeight);
    }
    
    protected final GuiControl control;
    
    private int left;
    private int top;
    private int right;
    private int bottom;
    
    public GuiControlRect(GuiControl control) {
        this.control = control;
    }
    
    public int getX() {
        return left;
    }
    
    public void setX(int x) {
        int width = getWidth();
        left = x;
        right = x + width;
    }
    
    public int getY() {
        return top;
    }
    
    public void setY(int y) {
        int height = getHeight();
        top = y;
        bottom = y + height;
    }
    
    public int getWidth() {
        return right - left;
    }
    
    public int getHeight() {
        return bottom - top;
    }
    
    public int getBottom() {
        return bottom;
    }
    
    public int getRight() {
        return right;
    }
    
    public void setRight(int x) {
        right = x;
    }
    
    public void setBottom(int y) {
        bottom = y;
    }
    
    public int getContentWidth() {
        return getWidth() - control.getContentOffset() * 2;
    }
    
    public int getContentHeight() {
        return getHeight() - control.getContentOffset() * 2;
    }
    
    protected int clampWidth(int width, int availableWidth) {
        int min = getMinWidth(availableWidth);
        if (min != -1)
            width = Math.max(width, min);
        int max = getMaxWidth(availableWidth);
        if (max != -1)
            width = Math.min(width, max);
        return width;
    }
    
    public int setWidth(int width, int availableWidth) {
        width = clampWidth(width, availableWidth);
        right = left + width;
        return width;
    }
    
    protected int clampHeight(int height, int availableHeight) {
        int min = getMinHeight(availableHeight);
        if (min != -1)
            height = Math.max(height, min);
        int max = getMaxHeight(availableHeight);
        if (max != -1)
            height = Math.min(height, max);
        return height;
    }
    
    public int setHeight(int height, int availableHeight) {
        height = clampHeight(height, availableHeight);
        bottom = top + height;
        return height;
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
        int min = getMinWidth(control, availableWidth);
        if (min != -1)
            return min + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getMaxWidth(int availableWidth) {
        int max = getMaxWidth(control, availableWidth);
        if (max != -1)
            return max + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getPreferredWidth(int availableWidth) {
        return clampWidth(getPreferredWidth(control, availableWidth) + control.getContentOffset() * 2, availableWidth);
    }
    
    public int getMinHeight(int availableHeight) {
        
        int min = getMinHeight(control, getContentWidth(), availableHeight);
        if (min != -1)
            return min + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getMaxHeight(int availableHeight) {
        int max = getMaxHeight(control, getContentWidth(), availableHeight);
        if (max != -1)
            return max + control.getContentOffset() * 2;
        return -1;
    }
    
    public int getPreferredHeight(int availableHeight) {
        return clampHeight(getPreferredHeight(control, getContentWidth(), availableHeight) + control.getContentOffset() * 2, availableHeight);
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
    
    public boolean inside(double x, double y) {
        return x >= left && x < right && y >= top && y < bottom;
    }
    
    public boolean isExpandableX() {
        return control.isExpandableX();
    }
    
    public boolean isExpandableY() {
        return control.isExpandableY();
    }
    
    public Rect rectCopy() {
        return new Rect(left, top, right, bottom);
    }
    
    @Override
    public String toString() {
        return "[" + left + "," + top + "," + right + "," + bottom + "]";
    }
    
}
