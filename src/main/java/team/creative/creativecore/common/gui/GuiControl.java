package team.creative.creativecore.common.gui;

import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.LanguageUtils;

public abstract class GuiControl<T extends GuiControlDistHandler> {
    
    private final IGuiParent parent;
    public final T dist;
    public final String name;
    
    public GuiControl(IGuiParent parent, String name) {
        this.parent = parent;
        this.dist = parent.createDist(this);
        this.name = name;
    }
    
    // CONSTRUCTION
    
    public GuiControl setVisible(boolean visible) {
        dist.setVisible(visible);
        return this;
    }
    
    public GuiControl setFixed() {
        dist.setFixed();
        return this;
    }
    
    public GuiControl setFixedX() {
        dist.setFixedX();
        return this;
    }
    
    public GuiControl setFixedY() {
        dist.setFixedY();
        return this;
    }
    
    public GuiControl setExpandable() {
        dist.setExpandable();
        return this;
    }
    
    public GuiControl setExpandableX() {
        dist.setExpandableX();
        return this;
    }
    
    public GuiControl setExpandableY() {
        dist.setExpandableY();
        return this;
    }
    
    public GuiControl setDim(int width, int height) {
        dist.setDim(width, height);
        return this;
    }
    
    public GuiControl setDim(GuiSizeRule dim) {
        dist.setDim(dim);
        return this;
    }
    
    public GuiControl setEnabled(boolean enabled) {
        dist.setEnabled(enabled);
        return this;
    }
    
    // BASICS
    
    public void reflow() {
        if (getParent() != null)
            getParent().reflow();
    }
    
    public HolderLookup.Provider provider() {
        var player = getPlayer();
        return player != null ? player.registryAccess() : null;
    }
    
    public boolean isClient() {
        if (parent != null)
            return parent.isClient();
        return CreativeCore.loader().getEffectiveSide().isClient();
    }
    
    public GuiControl setTooltip(List<Component> tooltip) {
        dist.setTooltip(tooltip);
        return this;
    }
    
    public GuiControl setTooltip(String translate) {
        dist.setTooltip(translate);
        return this;
    }
    
    public boolean hasGui() {
        if (parent != null)
            return parent.hasGui();
        return false;
    }
    
    public boolean isParent(IGuiParent parent) {
        if (this.parent == parent)
            return true;
        return this.parent.isParent(parent);
    }
    
    public IGuiParent getParent() {
        return parent;
    }
    
    public IGuiIntegratedParent getIntegratedParent() {
        return parent.getIntegratedParent();
    }
    
    public String getNestedName() {
        if (getParent() instanceof GuiControl control)
            return control.getNestedName() + "." + name;
        return name;
    }
    
    public boolean hasLayer() {
        if (parent instanceof GuiControl control)
            return control.hasLayer();
        return false;
    }
    
    public GuiLayer getLayer() {
        if (parent instanceof GuiControl control)
            return control.getLayer();
        throw new RuntimeException("Invalid layer control");
    }
    
    public void init() {
        dist.init();
    }
    
    public void closed() {
        dist.closed();
    }
    
    public void tick() {
        dist.tick();
    }
    
    public boolean is(String name) {
        return this.name.equalsIgnoreCase(name);
    }
    
    public boolean is(String... name) {
        for (String s : name) {
            if (this.name.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
    
    public void raiseEvent(GuiEvent event) {
        if (parent != null)
            parent.raiseEvent(event);
    }
    
    // MINECRAFT
    
    public Player getPlayer() {
        return parent.getPlayer();
    }
    
    // UTILS
    
    public Rect toLayerRect(Rect rect) {
        return getParent().toLayerRect(this, rect);
    }
    
    public Rect toScreenRect(Rect rect) {
        return getParent().toScreenRect(this, rect);
    }
    
    public static MutableComponent translatable(String text) {
        return Component.literal(translate(text));
    }
    
    public static MutableComponent translatable(String text, Object... parameters) {
        return Component.literal(translate(text, parameters));
    }
    
    public static String translate(String text) {
        return LanguageUtils.translate(text);
    }
    
    public static String translate(String text, Object... parameters) {
        return LanguageUtils.translate(text, parameters);
    }
    
    public static String translateOrDefault(String text, String defaultText) {
        return LanguageUtils.translateOr(text, defaultText);
    }
    
}
