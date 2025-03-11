package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.inventory.GuiInventoryGridPreview;
import team.creative.creativecore.common.gui.control.inventory.GuiSlotViewer;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class GuiStackSelectorExtension extends GuiScrollY {
    
    public GuiExtensionCreator<GuiStackSelector, GuiStackSelectorExtension> creator;
    private String search = "";
    protected int cachedWidth;
    
    public GuiStackSelectorExtension(String name, Player player, GuiExtensionCreator<GuiStackSelector, GuiStackSelectorExtension> creator) {
        super(name);
        this.creator = creator;
        registerEventChanged((event) -> {
            if (event.control.is("searchBar")) {
                search = ((GuiTextfield) event.control).getText();
                reloadControls();
            }
        });
        registerEventClick((event) -> {
            if (event.control instanceof GuiSlotViewer && event.control.isParent(this)) {
                creator.parent.setSelected(((GuiSlotViewer) event.control).getStack());
                playSound(SoundEvents.UI_BUTTON_CLICK);
                creator.close();
            }
        });
        this.align = Align.STRETCH;
        reloadControls();
        setDim(new GuiSizeRule.GuiSizeRules().maxHeight(100));
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (super.mouseClicked(rect, x, y, button))
            creator.markKeptFocus();
        return true;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        this.cachedWidth = width;
        super.flowX(width, preferred);
    }
    
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
        
        GuiTextfield textfield = get("searchBar");
        
        clear();
        
        if (comboBox.hasSearchbar()) {
            if (textfield == null)
                textfield = new GuiTextfield("searchBar", search == null ? "" : search);
            add(textfield);
            textfield.focus();
        }
        
        for (Entry<String, ArrayList<ItemStack>> entry : stacks.entrySet()) {
            add(new GuiLabel("title").setTitle(Component.translatable(entry.getKey())));
            
            SimpleContainer container = new SimpleContainer(entry.getValue().size());
            int i = 0;
            for (ItemStack stack : entry.getValue()) {
                container.setItem(i, stack);
                i++;
            }
            add(new GuiInventoryGridPreview(entry.getKey(), container));
        }
        if (hasGui())
            reflowInternal();
    }
    
    @Override
    public void looseFocus() {
        creator.markLostFocus();
    }
    
}
