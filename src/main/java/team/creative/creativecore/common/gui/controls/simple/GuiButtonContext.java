package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextMapBuilder;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class GuiButtonContext extends GuiButton {
    
    protected GuiChildControl extension;
    protected TextMapBuilder<Consumer<Integer>> map;
    protected boolean isHovered = false;
    
    public GuiButtonContext(String name, TextMapBuilder<Consumer<Integer>> map) {
        super(name, map.first());
        this.map = map;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
        if (isHovered = rect.inside(x + rect.minX, y + rect.minY)) {
            if (extension == null)
                openExtension(rect);
        }
    }
    
    public void openExtension(Rect rect) {
        GuiParent parent = new GuiParent() {
            @Override
            public void mouseMoved(Rect rect, double x, double y) {
                super.mouseMoved(rect, x, y);
                if (!isHovered && !extension.rect.inside(x + rect.minX, y + rect.minY))
                    closeExtension();
            }
            
            @Override
            public ControlFormatting getControlFormatting() {
                return ControlFormatting.NESTED;
            }
        };
        parent.flow = GuiFlow.STACK_Y;
        parent.align = Align.STRETCH;
        int i = 0;
        for (Entry<Consumer<Integer>, List<Component>> entry : map.entrySet()) {
            parent.add(new GuiRowLabel(i + "", i, false, x -> entry.getKey().accept(x)).setTitle(entry.getValue()));
            i++;
        }
        
        extension = getLayer().addHover(parent);
        
        rect = toLayerRect(new Rect(0, 0, rect.getWidth(), rect.getHeight()));
        extension.control.init();
        extension.setX((int) rect.maxX);
        extension.setY((int) rect.minY);
        
        int available = (int) getLayer().rect.getWidth() - getContentOffset() * 2;
        
        extension.setWidth(extension.getPreferredWidth(available), available);
        extension.flowX();
        int height = (int) getLayer().rect.getHeight() - getContentOffset() * 2;
        extension.setHeight(extension.getPreferredHeight(height), height);
        extension.flowY();
        
        Rect absolute = toScreenRect(extension.rect.copy());
        Rect screen = Rect.getScreenRect();
        
        if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
            extension.setY((int) extension.rect.minY + (int) rect.getHeight() - extension.getHeight());
    }
    
    public void closeExtension() {
        if (extension != null) {
            getLayer().remove(extension);
            extension = null;
        }
    }
    
}
