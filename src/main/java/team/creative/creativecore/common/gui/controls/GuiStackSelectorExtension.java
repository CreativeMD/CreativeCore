package team.creative.creativecore.common.gui.controls;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import team.creative.creativecore.common.util.type.HashMapList;

public class GuiStackSelectorExtension extends GuiScrollBox {
    
    public GuiStackSelector comboBox;
    public String search = "";
    
    public GuiStackSelectorExtension(String name, PlayerEntity player, int x, int y, int width, int height, GuiStackSelector comboBox) {
        super(name, x, y, width, height);
        this.comboBox = comboBox;
        registerEventChanged((event) -> {
            if (event.control.is("searchBar")) {
                search = ((GuiTextfield) event.control).getText();
                reloadControls();
            }
        });
        registerEventClick((event) -> {
            if (event.control instanceof SlotViewer && event.control.getParent() == this) {
                comboBox.setSelected(((SlotViewer) event.control).stack);
                playSound(SoundEvents.UI_BUTTON_CLICK);
                comboBox.closeBox();
            }
        });
        reloadControls();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        super.mouseClicked(x, y, button);
        return true;
    }
    
    @Override
    public void looseFocus() {
        comboBox.closeBox();
    }
    
    @Override
    public boolean canOverlap() {
        return true;
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
        
        int height = 0;
        GuiTextfield textfield = null;
        if (comboBox.searchBar)
            textfield = (GuiTextfield) get("searchBar");
        
        clear();
        
        if (comboBox.searchBar) {
            height += 4;
            if (textfield == null)
                textfield = new GuiTextfield("searchBar", search == null ? "" : search, 3, height, getWidth() - 20, 14);
            add(textfield);
            height += textfield.getHeight() + 4;
            textfield.focus();
        }
        
        for (Entry<String, ArrayList<ItemStack>> entry : stacks.entrySet()) {
            GuiLabel label = new GuiLabel("title", 4, height).setTitle(new TranslationTextComponent(entry.getKey()));
            add(label);
            height += 12;
            
            int SlotsPerRow = (getWidth() - 10) / 17;
            
            int i = 0;
            for (ItemStack stack : entry.getValue()) {
                int row = i / SlotsPerRow;
                add(new SlotViewer("" + i, 3 + (i - row * SlotsPerRow) * 17, height + row * 17, stack) {
                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        return true;
                    }
                });
                i++;
            }
            
            height += Math.ceil(i / (double) SlotsPerRow) * 17;
        }
        if (getParent() != null)
            init();
    }
    
}
