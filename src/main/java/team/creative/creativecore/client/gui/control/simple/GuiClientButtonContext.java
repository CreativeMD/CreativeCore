package team.creative.creativecore.client.gui.control.simple;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiButtonContext;
import team.creative.creativecore.common.gui.control.simple.GuiButtonContext.GuiButtonContextDist;
import team.creative.creativecore.common.gui.control.simple.GuiListEntry;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiClientButtonContext<T extends GuiButtonContext> extends GuiClientButton<T> implements GuiButtonContextDist {
    
    protected GuiExtensionCreator<GuiClientButtonContext, GuiParent> ex = new GuiExtensionCreator<>(this);
    protected TextMapBuilder<Consumer<Integer>> map;
    protected boolean isHovered = false;
    
    public GuiClientButtonContext(T control) {
        super(control);
    }
    
    @Override
    public void set(TextMapBuilder<Consumer<Integer>> map, boolean notify) {
        this.map = map;
        if (notify)
            raiseEvent(new GuiControlChangedEvent<GuiControl>(control));
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
        if (isHovered = rect.inside(x + rect.getX(), y + rect.getY()) && !ex.hasExtension())
            ex.open(createBox(ex), ExtensionDirection.BELOW_OR_ABOVE_ANY_SIZE);
    }
    
    protected GuiParent createBox(GuiExtensionCreator<GuiClientButtonContext, GuiParent> creator) {
        GuiButtonContextMenu parent = new GuiButtonContextMenu(control.getParent());
        ((GuiClientButtonContextMenu) parent.dist()).button = this;
        parent.setFlow(GuiFlow.STACK_Y);
        parent.setAlign(Align.STRETCH);
        int i = 0;
        for (Entry<Consumer<Integer>, List<Component>> entry : map.entrySet()) {
            parent.add(new GuiListEntry(control.getParent(), i + "", i, false, x -> entry.getKey().accept(x)).setTitle(entry.getValue()));
            i++;
        }
        return parent;
    }
    
    public static class GuiButtonContextMenu extends GuiParent {
        
        public GuiButtonContextMenu(IGuiParent parent) {
            super(parent);
        }
        
    }
    
    public static class GuiClientButtonContextMenu<T extends GuiButtonContextMenu> extends GuiClientParent<T> {
        
        protected GuiClientButtonContext button;
        
        public GuiClientButtonContextMenu(T control) {
            super(control);
        }
        
        @Override
        public void mouseMoved(double x, double y) {
            super.mouseMoved(x, y);
            if (!button.isHovered && !rect.inside(x + rect.getX(), y + rect.getY()))
                button.ex.close();
        }
        
        @Override
        protected ControlFormatting defaultFormatting() {
            return ControlFormatting.NESTED;
        }
        
    }
}
