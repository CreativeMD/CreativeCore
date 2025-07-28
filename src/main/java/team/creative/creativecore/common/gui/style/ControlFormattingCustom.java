package team.creative.creativecore.common.gui.style;

public class ControlFormattingCustom extends ControlFormatting {
    
    private final ControlFormatting parent;
    public ControlStyleBorder border;
    public Integer padding;
    public ControlStyleFace face;
    public Boolean hasDisabledEffect;
    
    public ControlFormattingCustom(ControlFormatting parent) {
        this.parent = parent;
    }
    
    public ControlFormattingCustom setBorder(ControlStyleBorder border) {
        this.border = border;
        return this;
    }
    
    public ControlFormattingCustom setPadding(int padding) {
        this.padding = padding;
        return this;
    }
    
    public ControlFormattingCustom setFace(ControlStyleFace face) {
        this.face = face;
        return this;
    }
    
    public ControlFormattingCustom setDisabledEffect(boolean disabledEffect) {
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
