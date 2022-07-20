package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.entity.player.Player;

public class GuiPlayerInventoryGrid extends GuiInventoryGrid {
    
    private int timesChanged;
    
    public GuiPlayerInventoryGrid(Player player) {
        super("player", player.getInventory(), 9, 4);
        this.timesChanged = player.getInventory().getTimesChanged();
        setExpandableX();
        this.reverse = true;
    }
    
    @Override
    public void tick() {
        if (timesChanged != getPlayer().getInventory().getTimesChanged()) {
            setChanged();
            timesChanged = getPlayer().getInventory().getTimesChanged();
        }
        super.tick();
    }
}
