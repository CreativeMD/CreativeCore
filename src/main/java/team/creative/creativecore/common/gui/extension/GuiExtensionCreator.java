package team.creative.creativecore.common.gui.extension;

import java.util.function.Function;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlRect;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiExtensionCreator<P extends GuiControl, T extends GuiControl> {
    
    public final P parent;
    private T extension;
    private boolean lostFocus;
    
    public GuiExtensionCreator(P parent) {
        this.parent = parent;
    }
    
    public void toggle(Function<? extends GuiExtensionCreator, T> factory) {
        toggle(factory, ExtensionDirection.BELOW_OR_ABOVE);
    }
    
    public void toggle(Function<? extends GuiExtensionCreator, T> factory, ExtensionDirection direction) {
        if (extension == null)
            open((T) ((Function) factory).apply(this), direction);
        else
            close();
    }
    
    public void open(T extension) {
        open(extension, ExtensionDirection.BELOW_OR_ABOVE);
    }
    
    public void open(T extension, ExtensionDirection direction) {
        open(extension, parent, direction);
    }
    
    public void open(T extension, GuiControl reference, ExtensionDirection direction) {
        this.extension = extension;
        var layer = reference.getLayer();
        layer.addHover(extension);
        
        var rect = reference.toLayerRect(new Rect(0, 0, reference.rect.getWidth(), reference.rect.getHeight()));
        extension.init();
        
        direction.apply(layer, extension.rect, rect, layer.getContentOffset());
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
            public void apply(GuiLayer layer, GuiControlRect extension, Rect creatorRect, int layerOffset) {
                extension.setX((int) creatorRect.minX);
                extension.setY((int) creatorRect.maxY);
                
                extension.setWidth((int) creatorRect.getWidth(), layer.rect.getWidth() - layerOffset * 2);
                extension.flowX();
                int layerHeight = layer.rect.getHeight() - layerOffset * 2;
                extension.setHeight(extension.getPreferredHeight(layerHeight), layerHeight);
                extension.flowY();
                
                Rect absolute = layer.getIntegratedParent().toScreenRect(layer, extension.rectCopy());
                Rect screen = Rect.getScreenRect();
                
                if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
                    extension.setY(extension.getY() - ((int) creatorRect.getHeight() + extension.getHeight()));
            }
        },
        BELOW_OR_ABOVE_ANY_SIZE {
            @Override
            public void apply(GuiLayer layer, GuiControlRect extension, Rect creatorRect, int layerOffset) {
                extension.setX((int) creatorRect.minX);
                extension.setY((int) creatorRect.maxY);
                
                int layerWidth = layer.rect.getWidth() - layerOffset * 2;
                extension.setWidth(extension.getPreferredWidth(layerWidth), layerWidth);
                extension.flowX();
                int layerHeight = layer.rect.getHeight() - layerOffset * 2;
                extension.setHeight(extension.getPreferredHeight(layerHeight), layerHeight);
                extension.flowY();
                
                Rect absolute = layer.getIntegratedParent().toScreenRect(layer, extension.rectCopy());
                Rect screen = Rect.getScreenRect();
                
                if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
                    extension.setY(extension.getY() - ((int) creatorRect.getHeight() + extension.getHeight()));
            }
        },
        RIGHT {
            @Override
            public void apply(GuiLayer layer, GuiControlRect extension, Rect creatorRect, int layerOffset) {
                extension.setX((int) creatorRect.maxX);
                extension.setY((int) creatorRect.minY);
                
                int layerWidth = layer.rect.getWidth() - layerOffset * 2;
                extension.setWidth(extension.getPreferredWidth(layerWidth), layerWidth);
                extension.flowX();
                int layerHeight = layer.rect.getHeight() - layerOffset * 2;
                extension.setHeight(extension.getPreferredHeight(layerHeight), layerHeight);
                extension.flowY();
                
                Rect absolute = layer.getIntegratedParent().toScreenRect(layer, extension.rectCopy());
                Rect screen = Rect.getScreenRect();
                
                if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
                    extension.setY((int) creatorRect.maxY - extension.getHeight());
            }
        };
        
        public abstract void apply(GuiLayer layer, GuiControlRect extension, Rect rect, int layerOffset);
    }
    
}
