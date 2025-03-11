package team.creative.creativecore.common.gui.extension;

import java.util.function.Function;

import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiExtensionCreator<P extends GuiControl, T extends GuiControl> {
    
    public final P parent;
    private T extension;
    private boolean lostFocus;
    
    public GuiExtensionCreator(P parent) {
        this.parent = parent;
    }
    
    public void toggle(Function<? extends GuiExtensionCreator, T> factory, Rect rect) {
        if (extension == null)
            open((T) ((Function) factory).apply(this), rect);
        else
            close();
    }
    
    public void open(T extension, Rect rect) {
        this.extension = extension;
        var layer = parent.getLayer();
        int offset = parent.getContentOffset();
        GuiChildControl child = layer.addHoverControl(extension);
        
        rect = parent.toLayerRect(new Rect(0, 0, rect.getWidth(), rect.getHeight()));
        extension.init();
        child.setX((int) rect.minX);
        child.setY((int) rect.maxY);
        
        child.setWidth((int) rect.getWidth(), (int) layer.rect.getWidth() - offset * 2);
        child.flowX();
        int height = (int) layer.rect.getHeight() - offset * 2;
        child.setHeight(child.getPreferredHeight(height), height);
        child.flowY();
        
        Rect absolute = extension.getIntegratedParent().toScreenRect(layer, child.rect.copy());
        Rect screen = Rect.getScreenRect();
        
        if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
            child.setY(child.getY() - ((int) rect.getHeight() + child.getHeight()));
    }
    
    public void close() {
        if (extension != null) {
            parent.getLayer().remove(extension);
            extension = null;
        }
    }
    
    public boolean hasLostFocus() {
        return lostFocus;
    }
    
    public boolean hasExtension() {
        return extension != null;
    }
    
    public boolean shouldClose() {
        return extension != null && lostFocus;
    }
    
    public void markLostFocus() {
        lostFocus = true;
    }
    
    public void markKeptFocus() {
        lostFocus = false;
    }
    
}
