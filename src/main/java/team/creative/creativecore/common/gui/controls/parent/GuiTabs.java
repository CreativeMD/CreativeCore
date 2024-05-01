package team.creative.creativecore.common.gui.controls.parent;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.simple.GuiTabButton.GuiBorderlessButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiTabs extends GuiParent {
    
    private final List<SpecialParent> tabs = new ArrayList<>();
    private int index = -1;
    private GuiParent selected;
    private int lastWidth;
    private int lastHeight = -1;
    private int lastY;
    private GuiTabBar bar;
    
    public GuiTabs(String name) {
        super(name);
        flow = GuiFlow.STACK_Y;
        align = Align.STRETCH;
        valign = VAlign.STRETCH;
        add(bar = new GuiTabBar("bar"));
    }
    
    public GuiParent createTab(Component component) {
        bar.addTab(component, tabs.size());
        SpecialParent tab = new SpecialParent();
        tab.setParent(this);
        tabs.add(tab);
        return tab;
    }
    
    public GuiBorderlessButton getTabButton(int index) {
        return bar.getTab(index);
    }
    
    public GuiParent getTab(int index) {
        return tabs.get(index);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    public void select(int select) {
        if (selected != null)
            remove(selected);
        index = select;
        selected = tabs.get(select);
        bar.highlight(select);
        GuiChildControl control = addControl(selected);
        if (lastHeight == -1 && getParent() != null)
            reflow();
        else {
            control.setX(0);
            control.setWidth(lastWidth, lastWidth);
            control.flowX();
            control.setY(lastY);
            control.setHeight(lastHeight, lastHeight);
            control.flowY();
        }
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public int index() {
        return index;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        lastWidth = width;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(width, height, preferred);
        GuiChildControl control;
        if (selected != null && (control = find(selected)) != null) {
            lastHeight = control.getHeight();
            lastY = control.getY();
        } else
            lastHeight = -1;
    }
    
    public class GuiTabBar extends GuiParent {
        
        private GuiBorderlessButton highlighted;
        private int count;
        
        public GuiTabBar(String name) {
            super(name);
            flow = GuiFlow.STACK_X;
        }
        
        public void highlight(int index) {
            GuiBorderlessButton newSelected = (GuiBorderlessButton) controls.get(index).control;
            if (newSelected != highlighted && highlighted != null)
                highlighted.active = false;
            newSelected.active = true;
            highlighted = newSelected;
        }
        
        public void addTab(Component component, int index) {
            addControl(new GuiBorderlessButton("b" + count, x -> select(index)).setTitle(component));
            count++;
        }
        
        public GuiBorderlessButton getTab(int index) {
            return (GuiBorderlessButton) controls.get(index).control;
        }
        
        public void removeTab(int index) {
            controls.remove(index);
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.TRANSPARENT;
        }
    }
    
    private class SpecialParent extends GuiParent {
        
        public SpecialParent() {}
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.NESTED;
        }
        
        @Override
        protected int preferredWidth(int availableWidth) {
            int pref = 0;
            for (SpecialParent p : GuiTabs.this.tabs)
                pref = Math.max(pref, p.preferredWidthOriginal(availableWidth));
            return pref;
        }
        
        protected int preferredWidthOriginal(int availableWidth) {
            return super.preferredWidth(availableWidth);
        }
        
        @Override
        protected int preferredHeight(int width, int availableHeight) {
            int pref = 0;
            for (SpecialParent p : GuiTabs.this.tabs)
                pref = Math.max(pref, p.preferredHeightOriginal(width, availableHeight));
            return pref;
        }
        
        protected int preferredHeightOriginal(int width, int availableHeight) {
            return super.preferredHeight(width, availableHeight);
        }
        
    }
}
