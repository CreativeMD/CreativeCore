package team.creative.creativecore.common.gui.control.parent;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiTabButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiTabs extends GuiParent {
    
    private final List<GuiTabSpecialParent> tabs = new ArrayList<>();
    
    private GuiTabBar bar;
    
    public GuiTabs(IGuiParent parent, String name) {
        super(parent, name);
        setFlow(GuiFlow.STACK_Y);
        setAlign(Align.STRETCH);
        setVAlign(VAlign.STRETCH);
        add(bar = new GuiTabBar(parent, "bar"));
        dist().init(tabs, bar);
    }
    
    @Override
    public GuiTabsDist dist() {
        return (GuiTabsDist) super.dist();
    }
    
    @Override
    public GuiControlDistHandler createDist(GuiControl control) {
        if (control instanceof GuiTabSpecialParent special)
            return dist().createSpecialParent(special);
        return super.createDist(control);
    }
    
    public GuiParent createTab(Component component) {
        bar.addTab(component, tabs.size());
        GuiTabSpecialParent tab = new GuiTabSpecialParent(this);
        tabs.add(tab);
        return tab;
    }
    
    public GuiButton getTabButton(int index) {
        return bar.getTab(index);
    }
    
    public GuiParent getTab(int index) {
        return tabs.get(index);
    }
    
    public void select(int select) {
        dist().select(select);
    }
    
    public int index() {
        return dist().index();
    }
    
    public class GuiTabBar extends GuiParent {
        
        private GuiButton highlighted;
        private int count;
        
        public GuiTabBar(IGuiParent parent, String name) {
            super(parent, name);
            setFlow(GuiFlow.STACK_X);
        }
        
        public void highlight(int index) {
            GuiButton newSelected = (GuiButton) get(index);
            if (newSelected != highlighted && highlighted != null)
                highlighted.setFormatting(GuiTabButton.BUTTON_INACTIVE);
            newSelected.setFormatting(GuiTabButton.BUTTON_ACTIVE);
            highlighted = newSelected;
        }
        
        public void addTab(Component component, int index) {
            add(new GuiButton(getParent(), "b" + count, x -> select(index)).setTitle(component));
            count++;
        }
        
        public GuiButton getTab(int index) {
            return (GuiButton) get(index);
        }
        
        public void removeTab(int index) {
            remove(index);
        }
    }
    
    public class GuiTabSpecialParent extends GuiParent {
        
        public GuiTabSpecialParent(IGuiParent parent) {
            super(parent);
        }
        
    }
    
    public static interface GuiTabsDist extends GuiParentDistHandler {
        
        public void init(List<GuiTabSpecialParent> tabs, GuiTabBar bar);
        
        public int index();
        
        public void select(int index);
        
        public GuiParentDistHandler createSpecialParent(GuiTabSpecialParent parent);
        
    }
}
