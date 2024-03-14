package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiButtonHold extends GuiButton {
    
    public static final int initialWait = 250;
    public static final int continousWait = 100;
    
    public int clicked = -1;
    public boolean inital = false;
    public long wait = 0;
    
    public GuiButtonHold(String name, Consumer<Integer> pressed) {
        super(name, pressed);
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        wait = System.currentTimeMillis();
        clicked = button;
        inital = true;
        return super.mouseClicked(rect, x, y, button);
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        clicked = -1;
        super.mouseReleased(rect, x, y, button);
    }
    
    @Override
    public void tick() {
        if (clicked != -1)
            if (inital) {
                if (System.currentTimeMillis() - wait >= initialWait) {
                    pressed.accept(clicked);
                    wait = System.currentTimeMillis();
                    inital = false;
                }
            } else if (System.currentTimeMillis() - wait >= continousWait) {
                pressed.accept(clicked);
                wait = System.currentTimeMillis();
            }
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        if (clicked != -1 && !rect.inside(x, y))
            wait = System.currentTimeMillis();
        super.mouseMoved(rect, x, y);
    }
}