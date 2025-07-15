package team.creative.creativecore.client.gui.manager;

import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.common.gui.manager.GuiManager;

public class GuiClientManager<T extends GuiManager> {
    
    public final T manager;
    
    public GuiClientManager(T manager) {
        this.manager = manager;
    }
    
    public void renderOverlay(GuiGraphics graphics, GuiClientLayer layer, int mouseX, int mouseY) {}
    
    public void mouseReleased(double x, double y, int button) {}
    
    public void mouseClickedOutside(double x, double y) {}
    
}
