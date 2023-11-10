package team.creative.creativecore.common.gui.controls.inventory;

import java.util.function.BiFunction;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class GuiPlayerInventoryGrid extends GuiInventoryGrid {
    
    private int timesChanged;
    
    public GuiPlayerInventoryGrid(Player player) {
        super("player", player.getInventory(), 9, 4);
        this.timesChanged = player.getInventory().getTimesChanged();
        setExpandableX();
    }
    
    @Override
    protected void createInventoryGrid(BiFunction<Container, Integer, Slot> slotFactory) {
        for (int i = 9; i < fixedSize; i++)
            addSlot(new GuiSlot(slotFactory.apply(container, i)));
        for (int i = 0; i < 9; i++)
            addSlot(new GuiSlot(slotFactory.apply(container, i)));
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
