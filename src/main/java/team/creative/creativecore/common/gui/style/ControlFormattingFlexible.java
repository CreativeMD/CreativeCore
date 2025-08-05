package team.creative.creativecore.common.gui.style;

public interface ControlFormattingFlexible extends ControlFormatting {
    
    public ControlFormattingFlexible setBorder(ControlStyleBorder border);
    
    public ControlFormattingFlexible setPadding(int padding);
    
    public ControlFormattingFlexible setFace(ControlStyleFace face);
    
    public ControlFormattingFlexible setDisabledEffect(boolean disabledEffect);
    
    public static class ControlFormattingFlexibleEmpty implements ControlFormattingFlexible {
        
        @Override
        public ControlStyleBorder border() {
            return ControlStyleBorder.NONE;
        }
        
        @Override
        public int padding() {
            return 0;
        }
        
        @Override
        public ControlStyleFace face() {
            return ControlStyleFace.NONE;
        }
        
        @Override
        public boolean hasDisabledEffect() {
            return false;
        }
        
        @Override
        public ControlFormattingFlexible setBorder(ControlStyleBorder border) {
            return this;
        }
        
        @Override
        public ControlFormattingFlexible setPadding(int padding) {
            return this;
        }
        
        @Override
        public ControlFormattingFlexible setFace(ControlStyleFace face) {
            return this;
        }
        
        @Override
        public ControlFormattingFlexible setDisabledEffect(boolean disabledEffect) {
            return this;
        }
        
    }
    
    public static class ControlFormattingFlexibleImpl implements ControlFormattingFlexible {
        
        private final ControlFormatting parent;
        public ControlStyleBorder border;
        public Integer padding;
        public ControlStyleFace face;
        public Boolean hasDisabledEffect;
        
        public ControlFormattingFlexibleImpl(ControlFormatting parent) {
            this.parent = parent;
        }
        
        @Override
        public ControlFormattingFlexible setBorder(ControlStyleBorder border) {
            this.border = border;
            return this;
        }
        
        @Override
        public ControlFormattingFlexible setPadding(int padding) {
            this.padding = padding;
            return this;
        }
        
        @Override
        public ControlFormattingFlexible setFace(ControlStyleFace face) {
            this.face = face;
            return this;
        }
        
        @Override
        public ControlFormattingFlexible setDisabledEffect(boolean disabledEffect) {
            hasDisabledEffect = disabledEffect;
            return this;
        }
        
        @Override
        public ControlStyleBorder border() {
            if (border != null)
                return border;
            return parent.border();
        }
        
        @Override
        public int padding() {
            if (padding != null)
                return padding;
            return parent.padding();
        }
        
        @Override
        public ControlStyleFace face() {
            if (face != null)
                return face;
            return parent.face();
        }
        
        @Override
        public boolean hasDisabledEffect() {
            if (hasDisabledEffect != null)
                return hasDisabledEffect;
            return parent.hasDisabledEffect();
        }
        
    }
    
}
