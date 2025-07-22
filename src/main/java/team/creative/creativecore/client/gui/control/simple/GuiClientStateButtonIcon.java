package team.creative.creativecore.client.gui.control.simple;

import team.creative.creativecore.common.gui.control.simple.GuiStateButtonIcon;
import team.creative.creativecore.common.gui.control.simple.GuiStateButtonIcon.GuiStateButtonIconDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.Icon;

public class GuiClientStateButtonIcon<T extends GuiStateButtonIcon> extends GuiClientButtonIcon<T> implements GuiStateButtonIconDist {
    
    protected Icon[] states;
    private int index;
    
    public GuiClientStateButtonIcon(T control) {
        super(control);
    }
    
    @Override
    public int getState() {
        return index;
    }
    
    @Override
    public void setIcons(Icon... icons) {
        this.states = icons;
    }
    
    @Override
    public void setState(int index) {
        this.index = index;
        this.icon = states[index];
    }
    
    @Override
    public void previousState() {
        int state = this.getState();
        --state;
        if (state < 0) {
            state = this.states.length - 1;
        }
        
        if (state >= this.states.length) {
            state = 0;
        }
        
        this.setState(state);
        this.raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public void nextState() {
        int state = this.getState();
        ++state;
        if (state < 0) {
            state = this.states.length - 1;
        }
        
        if (state >= this.states.length) {
            state = 0;
        }
        
        this.setState(state);
        this.raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        this.nextState();
        return super.mouseClicked(x, y, button);
    }
    
}
