package team.creative.creativecore.common.gui.controls.timeline;

public interface GuiAnimationHandler {
    
    public void loop(boolean loop);
    
    public void play();
    
    public void pause();
    
    public void stop();
    
    public void set(int tick);
    
    public int get();
    
}
