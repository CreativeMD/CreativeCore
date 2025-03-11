package team.creative.creativecore.common.gui.control.simple;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiButtonContext extends GuiButton {
    
    protected GuiExtensionCreator<GuiButtonContext, GuiParent> ex = new GuiExtensionCreator<>(this);
    protected TextMapBuilder<Consumer<Integer>> map;
    protected boolean isHovered = false;
    
    public GuiButtonContext(String name, TextMapBuilder<Consumer<Integer>> map) {
        super(name, map.first());
        this.map = map;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
        if (isHovered = rect.inside(x + rect.minX, y + rect.minY) && !ex.hasExtension())
            ex.open(createBox(ex), rect);
    }
    
    protected GuiParent createBox(GuiExtensionCreator<GuiButtonContext, GuiParent> creator) {
        GuiParent parent = new GuiParent() {
            @Override
            public void mouseMoved(Rect rect, double x, double y) {
                super.mouseMoved(rect, x, y);
                if (!isHovered && !find(this).rect.inside(x + rect.minX, y + rect.minY))
                    ex.close();
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
        return parent;
    }
    
}
