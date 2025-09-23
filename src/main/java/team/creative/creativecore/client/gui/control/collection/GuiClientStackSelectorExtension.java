package team.creative.creativecore.client.gui.control.collection;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollY;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelectorExtension;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelectorExtension.GuiStackSelectorExtensionDist;
import team.creative.creativecore.common.gui.control.inventory.GuiInventoryGridPreview;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class GuiClientStackSelectorExtension<T extends GuiStackSelectorExtension> extends GuiClientScrollY<T> implements GuiStackSelectorExtensionDist {
    
    public GuiExtensionCreator<GuiClientStackSelector<?>, GuiStackSelectorExtension> creator;
    private String search = "";
    protected int cachedWidth;
    
    public GuiClientStackSelectorExtension(T control) {
        super(control);
        control.registerEventChanged(x -> {
            if (x.control.is("searchBar")) {
                search = ((GuiTextfield) x.control).getText();
                reloadControls();
            }
        });
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            creator.markKeptFocus();
        return true;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        this.cachedWidth = width;
        super.flowX(width, preferred);
    }
    
    @Override
    public void reflowInternal() {
        flowX(cachedWidth, preferredWidth(cachedWidth));
        flowY(cachedWidth, cachedHeight, preferredHeight(cachedWidth, cachedHeight));
    }
    
    public void reloadControls() {
        if (creator == null)
            return;
        
        var comboBox = creator.parent;
        
        HashMapList<String, ItemStack> stacks = search == null || search.isEmpty() ? comboBox.getStacks() : new HashMapList<>();
        
        if (search != null && !search.isEmpty()) {
            for (Entry<String, ArrayList<ItemStack>> entry : comboBox.getStacks().entrySet()) {
                for (ItemStack stack : entry.getValue()) {
                    if (GuiStackSelector.contains(search, stack))
                        stacks.add(entry.getKey(), stack);
                }
            }
        }
        
        GuiTextfield textfield = control.get("searchBar");
        
        control.clear();
        
        if (comboBox.hasSearchbar()) {
            if (textfield == null)
                textfield = new GuiTextfield(control, "searchBar", search == null ? "" : search);
            control.add(textfield.setExpandableX());
            textfield.dist().focus();
        }
        
        for (Entry<String, ArrayList<ItemStack>> entry : stacks.entrySet()) {
            if (entry.getValue().isEmpty())
                continue;
            control.add(new GuiLabel(control, "title").setTitle(Component.translatable(entry.getKey())));
            
            SimpleContainer container = new SimpleContainer(entry.getValue().size());
            int i = 0;
            for (ItemStack stack : entry.getValue()) {
                container.setItem(i, stack);
                i++;
            }
            control.add(new GuiInventoryGridPreview(control, entry.getKey(), container));
        }
        if (control.hasGui())
            reflowInternal();
    }
    
    @Override
    public void looseFocus() {
        creator.markLostFocus();
    }
}
