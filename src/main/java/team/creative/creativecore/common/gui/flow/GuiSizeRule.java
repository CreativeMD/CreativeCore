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
    
    public static class GuiSizeRuleCustom extends GuiSizeRule {
        
        public int minWidth = -1;
        public int prefWidth = -1;
        public int maxWidth = -1;
        
        public int minHeight = -1;
        public int prefHeight = -1;
        public int maxHeight = -1;
        
        public GuiSizeRuleCustom() {}
        
        public GuiSizeRuleCustom minWidth(int value) {
            this.minWidth = value;
            return this;
        }
        
        public GuiSizeRuleCustom prefWidth(int value) {
            this.prefWidth = value;
            return this;
        }
        
        public GuiSizeRuleCustom maxWidth(int value) {
            this.maxWidth = value;
            return this;
        }
        
        public GuiSizeRuleCustom minHeight(int value) {
            this.minHeight = value;
            return this;
        }
        
        public GuiSizeRuleCustom prefHeight(int value) {
            this.prefHeight = value;
            return this;
        }
        
        public GuiSizeRuleCustom maxHeight(int value) {
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
    
}
