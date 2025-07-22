package team.creative.creativecore.client.gui.control.simple;

import team.creative.creativecore.common.gui.control.simple.GuiButtonHold;
import team.creative.creativecore.common.gui.control.simple.GuiButtonHold.GuiButtonHoldDist;

public class GuiClientButtonHold<T extends GuiButtonHold> extends GuiClientButton<T> implements GuiButtonHoldDist {
    
    public static final int INITIAL_WAIT = 250;
    public static final int CONTINOUS_WAIT = 100;
    
    public int clicked = -1;
    public boolean inital = false;
    public long wait = 0;
    
    public GuiClientButtonHold(T control) {
        super(control);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        wait = System.currentTimeMillis();
        clicked = button;
        inital = true;
        return super.mouseClicked(x, y, button);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        clicked = -1;
        super.mouseReleased(x, y, button);
    }
    
    @Override
    public void tick() {
        if (clicked != -1)
            if (inital) {
                if (System.currentTimeMillis() - wait >= INITIAL_WAIT) {
                    pressed.accept(clicked);
                    wait = System.currentTimeMillis();
                    inital = false;
                }
            } else if (System.currentTimeMillis() - wait >= CONTINOUS_WAIT) {
                pressed.accept(clicked);
                wait = System.currentTimeMillis();
            }
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (clicked != -1 && !rect.inside(x, y))
            wait = System.currentTimeMillis();
        super.mouseMoved(x, y);
    }
    
}
