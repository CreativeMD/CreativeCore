package team.creative.creativecore.common.gui.flow;

import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiSizeRule {
    
    public abstract int minWidth(GuiControl control, int availableWidth);
    
    public abstract int preferredWidth(GuiControl control, int availableWidth);
    
    public abstract int maxWidth(GuiControl control, int availableWidth);
    
    public abstract int minHeight(GuiControl control, int width, int availableHeight);
    
    public abstract int preferredHeight(GuiControl control, int width, int availableHeight);
    
    public abstract int maxHeight(GuiControl control, int width, int availableHeight);
    
    public static class GuiFixedDimension extends GuiSizeRule {
        
        public final int width;
        public final int height;
        
        public GuiFixedDimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public int minWidth(GuiControl control, int availableWidth) {
            return -1;
        }
        
        @Override
        public int preferredWidth(GuiControl control, int availableWidth) {
            return width;
        }
        
        @Override
        public int maxWidth(GuiControl control, int availableWidth) {
            return -1;
        }
        
        @Override
        public int minHeight(GuiControl control, int width, int availableHeight) {
            return -1;
        }
        
        @Override
        public int preferredHeight(GuiControl control, int width, int availableHeight) {
            return height;
        }
        
        @Override
        public int maxHeight(GuiControl control, int width, int availableHeight) {
            return -1;
        }
    }
    
    public static class GuiRatioDimension extends GuiSizeRule {
        
        public final float width;
        public final float height;
        
        public GuiRatioDimension(float width, float height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public int minWidth(GuiControl control, int availableWidth) {
            return -1;
        }
        
        @Override
        public int preferredWidth(GuiControl control, int availableWidth) {
            return (int) (width * availableWidth);
        }
        
        @Override
        public int maxWidth(GuiControl control, int availableWidth) {
            return -1;
        }
        
        @Override
        public int minHeight(GuiControl control, int width, int availableHeight) {
            return -1;
        }
        
        @Override
        public int preferredHeight(GuiControl control, int width, int availableHeight) {
            return (int) (height * availableHeight);
        }
        
        @Override
        public int maxHeight(GuiControl control, int width, int availableHeight) {
            return -1;
        }
    }
    
    public static class GuiSizeRules extends GuiSizeRule {
        
        public int minWidth = -1;
        public int prefWidth = -1;
        public int maxWidth = -1;
        
        public int minHeight = -1;
        public int prefHeight = -1;
        public int maxHeight = -1;
        
        public GuiSizeRules() {}
        
        public GuiSizeRules minWidth(int value) {
            this.minWidth = value;
            return this;
        }
        
        public GuiSizeRules prefWidth(int value) {
            this.prefWidth = value;
            return this;
        }
        
        public GuiSizeRules maxWidth(int value) {
            this.maxWidth = value;
            return this;
        }
        
        public GuiSizeRules minHeight(int value) {
            this.minHeight = value;
            return this;
        }
        
        public GuiSizeRules prefHeight(int value) {
            this.prefHeight = value;
            return this;
        }
        
        public GuiSizeRules maxHeight(int value) {
            this.maxHeight = value;
            return this;
        }
        
        @Override
        public int minWidth(GuiControl control, int availableWidth) {
            return minWidth;
        }
        
        @Override
        public int preferredWidth(GuiControl control, int availableWidth) {
            return prefWidth;
        }
        
        @Override
        public int maxWidth(GuiControl control, int availableWidth) {
            return maxWidth;
        }
        
        @Override
        public int minHeight(GuiControl control, int width, int availableHeight) {
            return minHeight;
        }
        
        @Override
        public int preferredHeight(GuiControl control, int width, int availableHeight) {
            return prefHeight;
        }
        
        @Override
        public int maxHeight(GuiControl control, int width, int availableHeight) {
            return maxHeight;
        }
        
    }
    
    public static class GuiSizeRatioRules extends GuiSizeRule {
        
        public int minWidth = -1;
        public float width = -1;
        public int maxWidth = -1;
        
        public int minHeight = -1;
        public float height = -1;
        public int maxHeight = -1;
        
        public GuiSizeRatioRules() {}
        
        public GuiSizeRatioRules(float width, float height) {
            this.width = width;
            this.height = height;
        }
        
        public GuiSizeRatioRules minWidth(int value) {
            this.minWidth = value;
            return this;
        }
        
        public GuiSizeRatioRules widthRatio(float value) {
            this.width = value;
            return this;
        }
        
        public GuiSizeRatioRules maxWidth(int value) {
            this.maxWidth = value;
            return this;
        }
        
        public GuiSizeRatioRules minHeight(int value) {
            this.minHeight = value;
            return this;
        }
        
        public GuiSizeRatioRules heightRatio(float value) {
            this.height = value;
            return this;
        }
        
        public GuiSizeRatioRules maxHeight(int value) {
            this.maxHeight = value;
            return this;
        }
        
        @Override
        public int minWidth(GuiControl control, int availableWidth) {
            return minWidth;
        }
        
        @Override
        public int preferredWidth(GuiControl control, int availableWidth) {
            if (width == -1)
                return -1;
            return (int) (width * availableWidth);
        }
        
        @Override
        public int maxWidth(GuiControl control, int availableWidth) {
            return maxWidth;
        }
        
        @Override
        public int minHeight(GuiControl control, int width, int availableHeight) {
            return minHeight;
        }
        
        @Override
        public int preferredHeight(GuiControl control, int width, int availableHeight) {
            if (height == -1)
                return -1;
            return (int) (height * availableHeight);
        }
        
        @Override
        public int maxHeight(GuiControl control, int width, int availableHeight) {
            return maxHeight;
        }
        
    }
    
}
