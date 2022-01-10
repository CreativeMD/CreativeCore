package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.entity.player.Player;

public class GuiPlayerInventoryGrid extends GuiInventoryGrid {
    
    public GuiPlayerInventoryGrid(Player player) {
        super("player", player.getInventory(), 9);
        setExpandableX();
    }
}
