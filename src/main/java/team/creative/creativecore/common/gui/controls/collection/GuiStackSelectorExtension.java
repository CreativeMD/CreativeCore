package team.creative.creativecore.common.gui.controls.collection;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.controls.inventory.GuiInventoryGrid;
import team.creative.creativecore.common.gui.controls.inventory.GuiSlot;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class GuiStackSelectorExtension extends GuiScrollY {
    
    public GuiStackSelector comboBox;
    public String search = "";
    protected int cachedWidth;
    
    public GuiStackSelectorExtension(String name, Player player, GuiStackSelector comboBox) {
        super(name);
        this.comboBox = comboBox;
        registerEventChanged((event) -> {
            if (event.control.is("searchBar")) {
                search = ((GuiTextfield) event.control).getText();
                reloadControls();
            }
        });
        registerEventClick((event) -> {
            if (event.control instanceof GuiSlot && event.control.isParent(this)) {
                comboBox.setSelected(((GuiSlot) event.control).getStack());
                playSound(SoundEvents.UI_BUTTON_CLICK);
                comboBox.closeBox();
            }
        });
        this.align = Align.STRETCH;
        reloadControls();
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (super.mouseClicked(rect, x, y, button))
            comboBox.extensionLostFocus = false;
        return true;
    }
    
    @Override
    public int preferredWidth() {
        return 100;
    }
    
    @Override
    public int preferredHeight() {
        return 100;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        this.cachedWidth = width;
        super.flowX(width, preferred);
    }
    
    public void reflowInternal() {
        flowX(cachedWidth, getPreferredWidth());
        flowY(cachedHeight, getPreferredHeight());
    }
    
    public void reloadControls() {
        if (comboBox == null)
            return;
        
        HashMapList<String, ItemStack> stacks = search == null || search.equals("") ? comboBox.getStacks() : new HashMapList<>();
        
        if (search != null && !search.equals("")) {
            for (Entry<String, ArrayList<ItemStack>> entry : comboBox.getStacks().entrySet()) {
                for (ItemStack stack : entry.getValue()) {
                    if (GuiStackSelector.contains(search, stack))
                        stacks.add(entry.getKey(), stack);
                }
            }
        }
        
        GuiTextfield textfield = (GuiTextfield) get("searchBar");
        
        clear();
        
        if (comboBox.searchBar) {
            if (textfield == null)
                textfield = new GuiTextfield("searchBar", search == null ? "" : search);
            add(textfield);
            textfield.focus();
        }
        
        for (Entry<String, ArrayList<ItemStack>> entry : stacks.entrySet()) {
            add(new GuiLabel("title").setTitle(new TranslatableComponent(entry.getKey())));
            
            SimpleContainer container = new SimpleContainer(entry.getValue().size());
            int i = 0;
            for (ItemStack stack : entry.getValue()) {
                container.setItem(i, stack);
                i++;
            }
            add(new GuiInventoryGrid(entry.getKey(), container));
        }
        if (hasGui())
            reflowInternal();
    }
    
    @Override
    public void looseFocus() {
        comboBox.extensionLostFocus = true;
    }
    
}
