package team.creative.creativecore.common.gui.style;

public abstract interface ControlFormatting {
    
    public static final ControlFormatting PROGRESSBAR = new ControlFormattingStatic(ControlStyleBorder.SMALL, 0, ControlStyleFace.BAR);
    public static final ControlFormatting CLICKABLE = new ControlFormattingStatic(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting CLICKABLE_SMALL_PADDING = new ControlFormattingStatic(ControlStyleBorder.SMALL, 1, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting CLICKABLE_NO_PADDING = new ControlFormattingStatic(ControlStyleBorder.SMALL, 0, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting CLICKABLE_INACTIVE = new ControlFormattingStatic(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE_INACTIVE);
    public static final ControlFormatting CLICKABLE_INACTIVE_SMALL_PADDING = new ControlFormattingStatic(ControlStyleBorder.SMALL, 1, ControlStyleFace.CLICKABLE_INACTIVE);
    public static final ControlFormatting CLICKABLE_INACTIVE_NO_PADDING = new ControlFormattingStatic(ControlStyleBorder.SMALL, 0, ControlStyleFace.CLICKABLE_INACTIVE);
    public static final ControlFormatting CLICKABLE_NO_BORDER = new ControlFormattingStatic(ControlStyleBorder.NONE, 0, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting HEADER = new ControlFormattingStatic(ControlStyleBorder.SMALL, 2, ControlStyleFace.HEADER_BACKGROUND);
    public static final ControlFormatting NESTED = new ControlFormattingStatic(ControlStyleBorder.SMALL, 2, ControlStyleFace.NESTED_BACKGROUND);
    public static final ControlFormatting NESTED_NO_PADDING = new ControlFormattingStatic(ControlStyleBorder.SMALL, 0, ControlStyleFace.NESTED_BACKGROUND);
    public static final ControlFormatting GUI = new ControlFormattingStatic(ControlStyleBorder.BIG, 5, ControlStyleFace.BACKGROUND);
    public static final ControlFormatting TRANSPARENT = new ControlFormattingStatic(ControlStyleBorder.NONE, 0, ControlStyleFace.NONE);
    public static final ControlFormatting TRANSPARENT_NO_DISABLE = new ControlFormattingStatic(ControlStyleBorder.NONE, 0, ControlStyleFace.NONE, false);
    public static final ControlFormatting SLOT = new ControlFormattingStatic(ControlStyleBorder.NONE, 0, ControlStyleFace.SLOT, false);
    public static final ControlFormatting OUTLINE = new ControlFormattingStatic(ControlStyleBorder.SMALL, 1, ControlStyleFace.NONE);
    
    public abstract ControlStyleBorder border();
    
    public abstract int padding();
    
    public abstract ControlStyleFace face();
    
    public abstract boolean hasDisabledEffect();
    
    public static class ControlFormattingStatic implements ControlFormatting {
        
        public final ControlStyleBorder border;
        public final int padding;
        public final ControlStyleFace face;
        public final boolean hasDisabledEffect;
        
        public ControlFormattingStatic(ControlStyleBorder border, int padding, ControlStyleFace face) {
            this.border = border;
            this.padding = padding;
            this.face = face;
            this.hasDisabledEffect = true;
        }
        
        public ControlFormattingStatic(ControlStyleBorder border, int padding, ControlStyleFace face, boolean hasDisabledEffect) {
            this.border = border;
            this.padding = padding;
            this.face = face;
            this.hasDisabledEffect = hasDisabledEffect;
        }
        
        @Override
        public ControlStyleBorder border() {
            return border;
        }
        
        @Override
        public int padding() {
            return padding;
        }
        
        @Override
        public ControlStyleFace face() {
            return face;
        }
        
        @Override
        public boolean hasDisabledEffect() {
            return hasDisabledEffect;
        }
        
    }
    
    public static enum ControlStyleBorder {
        
        BIG,
        SMALL,
        NONE;
        
    }
    
    public static enum ControlStyleFace {
        
        BAR,
        CLICKABLE,
        CLICKABLE_INACTIVE,
        HEADER_BACKGROUND,
        NESTED_BACKGROUND,
        BACKGROUND,
        SLOT,
        NONE,
        DISABLED;
        
    }
    
}
