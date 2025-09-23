package team.creative.creativecore.client.gui.control.parent;

import java.util.List;

import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.GuiParent.GuiParentDistHandler;
import team.creative.creativecore.common.gui.control.parent.GuiTabs;
import team.creative.creativecore.common.gui.control.parent.GuiTabs.GuiTabBar;
import team.creative.creativecore.common.gui.control.parent.GuiTabs.GuiTabSpecialParent;
import team.creative.creativecore.common.gui.control.parent.GuiTabs.GuiTabsDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientTabs<T extends GuiTabs> extends GuiClientParent<T> implements GuiTabsDist {
    
    private List<GuiTabSpecialParent> tabs;
    private GuiTabBar bar;
    private int index = -1;
    private GuiParent selected;
    private int lastWidth;
    private int lastHeight = -1;
    private int lastY;
    
    public GuiClientTabs(T control) {
        super(control);
    }
    
    @Override
    public void init(List<GuiTabSpecialParent> tabs, GuiTabBar bar) {
        this.tabs = tabs;
        this.bar = bar;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        lastWidth = width;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(width, height, preferred);
        if (selected != null) {
            lastHeight = ((GuiClientControl) selected.dist()).rect.getHeight();
            lastY = ((GuiClientControl) selected.dist()).rect.getY();
        } else
            lastHeight = -1;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public void select(int select, boolean notify) {
        if (selected != null)
            control.remove(selected);
        index = select;
        selected = tabs.get(select);
        bar.highlight(select);
        control.add(selected);
        if (lastHeight == -1 && getParent() != null)
            control.reflow();
        else {
            var rect = ((GuiClientControl) selected.dist()).rect;
            rect.setX(0);
            rect.setWidth(lastWidth, lastWidth);
            rect.flowX();
            rect.setY(lastY);
            rect.setHeight(lastHeight, lastHeight);
            rect.flowY();
        }
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public int index() {
        return index;
    }
    
    @Override
    public GuiParentDistHandler createSpecialParent(GuiTabSpecialParent parent) {
        return new GuiClientTabSpecialParent(parent);
    }
    
    private class GuiClientTabSpecialParent extends GuiClientParent<GuiTabSpecialParent> {
        
        public GuiClientTabSpecialParent(GuiTabSpecialParent control) {
            super(control);
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.NESTED;
        }
        
        @Override
        protected int preferredWidth(int availableWidth) {
            int pref = 0;
            for (GuiTabSpecialParent p : tabs)
                pref = Math.max(pref, ((GuiClientTabSpecialParent) p.dist()).preferredWidthOriginal(availableWidth));
            return pref;
        }
        
        protected int preferredWidthOriginal(int availableWidth) {
            return super.preferredWidth(availableWidth);
        }
        
        @Override
        protected int preferredHeight(int width, int availableHeight) {
            int pref = 0;
            for (GuiTabSpecialParent p : tabs)
                pref = Math.max(pref, ((GuiClientTabSpecialParent) p.dist()).preferredHeightOriginal(width, availableHeight));
            return pref;
        }
        
        protected int preferredHeightOriginal(int width, int availableHeight) {
            return super.preferredHeight(width, availableHeight);
        }
        
    }
    
}
