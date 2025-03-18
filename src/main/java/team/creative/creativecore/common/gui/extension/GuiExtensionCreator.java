package team.creative.creativecore.common.gui.extension;

import java.util.function.Function;

import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
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
        open(extension, rect, ExtensionDirection.BELOW_OR_ABOVE);
    }
    
    public void open(T extension, Rect rect, ExtensionDirection direction) {
        this.extension = extension;
        var layer = parent.getLayer();
        GuiChildControl child = layer.addHoverControl(extension);
        
        rect = parent.toLayerRect(new Rect(0, 0, rect.getWidth(), rect.getHeight()));
        extension.init();
        
        direction.apply(layer, child, rect, layer.getContentOffset());
    }
    
    public T get() {
        return extension;
    }
    
    public void close() {
        if (extension != null) {
            extension.closed();
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
    
    public boolean checkShouldClose() {
        boolean result = extension != null && lostFocus;
        markLostFocus();
        return result;
    }
    
    public void markLostFocus() {
        lostFocus = true;
    }
    
    public void markKeptFocus() {
        lostFocus = false;
    }
    
    public static enum ExtensionDirection {
        
        BELOW_OR_ABOVE {
            @Override
            public void apply(GuiLayer layer, GuiChildControl child, Rect rect, int layerOffset) {
                child.setX((int) rect.minX);
                child.setY((int) rect.maxY);
                
                child.setWidth((int) rect.getWidth(), (int) layer.rect.getWidth() - layerOffset * 2);
                child.flowX();
                int layerHeight = (int) layer.rect.getHeight() - layerOffset * 2;
                child.setHeight(child.getPreferredHeight(layerHeight), layerHeight);
                child.flowY();
                
                Rect absolute = layer.getIntegratedParent().toScreenRect(layer, child.rect.copy());
                Rect screen = Rect.getScreenRect();
                
                if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
                    child.setY(child.getY() - ((int) rect.getHeight() + child.getHeight()));
            }
        },
        RIGHT {
            @Override
            public void apply(GuiLayer layer, GuiChildControl child, Rect rect, int layerOffset) {
                child.setX((int) rect.maxX);
                child.setY((int) rect.minY);
                
                int layerWidth = (int) layer.rect.getWidth() - layerOffset * 2;
                child.setWidth(child.getPreferredWidth(layerWidth), layerWidth);
                child.flowX();
                int layerHeight = (int) layer.rect.getHeight() - layerOffset * 2;
                child.setHeight(child.getPreferredHeight(layerHeight), layerHeight);
                child.flowY();
                
                Rect absolute = layer.getIntegratedParent().toScreenRect(layer, child.rect.copy());
                Rect screen = Rect.getScreenRect();
                
                if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
                    child.setY((int) rect.maxY - child.getHeight());
            }
        };
        
        public abstract void apply(GuiLayer layer, GuiChildControl child, Rect rect, int layerOffset);
    }
    
}
