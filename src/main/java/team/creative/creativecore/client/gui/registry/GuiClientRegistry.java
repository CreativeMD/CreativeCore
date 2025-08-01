package team.creative.creativecore.client.gui.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.gui.control.collection.GuiClientCheckList;
import team.creative.creativecore.client.gui.control.collection.GuiClientComboBox;
import team.creative.creativecore.client.gui.control.collection.GuiClientComboBoxExtension;
import team.creative.creativecore.client.gui.control.collection.GuiClientComboBoxFlexible;
import team.creative.creativecore.client.gui.control.collection.GuiClientComboBoxTree;
import team.creative.creativecore.client.gui.control.collection.GuiClientListBoxBase;
import team.creative.creativecore.client.gui.control.collection.GuiClientStackSelector;
import team.creative.creativecore.client.gui.control.collection.GuiClientStackSelectorExtension;
import team.creative.creativecore.client.gui.control.inventory.GuiClientInventoryGrid;
import team.creative.creativecore.client.gui.control.inventory.GuiClientInventoryGridPreview;
import team.creative.creativecore.client.gui.control.inventory.GuiClientSlot;
import team.creative.creativecore.client.gui.control.inventory.GuiClientSlotViewer;
import team.creative.creativecore.client.gui.control.menu.GuiClientMenu.GuiClientMenuEntry;
import team.creative.creativecore.client.gui.control.menu.GuiClientMenuRoot;
import team.creative.creativecore.client.gui.control.menu.GuiClientMenuSub;
import team.creative.creativecore.client.gui.control.parent.GuiClientPanel;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollX;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollXY;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollY;
import team.creative.creativecore.client.gui.control.parent.GuiClientTable;
import team.creative.creativecore.client.gui.control.parent.GuiClientTableScrollable;
import team.creative.creativecore.client.gui.control.parent.GuiClientTabs;
import team.creative.creativecore.client.gui.control.simple.GuiClientArraySlider;
import team.creative.creativecore.client.gui.control.simple.GuiClientButton;
import team.creative.creativecore.client.gui.control.simple.GuiClientButtonContext;
import team.creative.creativecore.client.gui.control.simple.GuiClientButtonContext.GuiButtonContextMenu;
import team.creative.creativecore.client.gui.control.simple.GuiClientButtonContext.GuiClientButtonContextMenu;
import team.creative.creativecore.client.gui.control.simple.GuiClientButtonHold;
import team.creative.creativecore.client.gui.control.simple.GuiClientButtonIcon;
import team.creative.creativecore.client.gui.control.simple.GuiClientCheckBox;
import team.creative.creativecore.client.gui.control.simple.GuiClientCheckButtonIcon;
import team.creative.creativecore.client.gui.control.simple.GuiClientColorPlate;
import team.creative.creativecore.client.gui.control.simple.GuiClientColoredSteppedSlider;
import team.creative.creativecore.client.gui.control.simple.GuiClientIcon;
import team.creative.creativecore.client.gui.control.simple.GuiClientLabel;
import team.creative.creativecore.client.gui.control.simple.GuiClientListEntry;
import team.creative.creativecore.client.gui.control.simple.GuiClientProgressbar;
import team.creative.creativecore.client.gui.control.simple.GuiClientSeekBar;
import team.creative.creativecore.client.gui.control.simple.GuiClientShowItem;
import team.creative.creativecore.client.gui.control.simple.GuiClientSlider;
import team.creative.creativecore.client.gui.control.simple.GuiClientStateButton;
import team.creative.creativecore.client.gui.control.simple.GuiClientStateButtonIcon;
import team.creative.creativecore.client.gui.control.simple.GuiClientSteppedSlider;
import team.creative.creativecore.client.gui.control.simple.GuiClientTabButton;
import team.creative.creativecore.client.gui.control.simple.GuiClientTextfield;
import team.creative.creativecore.client.gui.control.timeline.GuiClientTimeline;
import team.creative.creativecore.client.gui.control.timeline.GuiClientTimelineChannel;
import team.creative.creativecore.client.gui.control.timeline.GuiClientTimelineHeader;
import team.creative.creativecore.client.gui.control.timeline.GuiClientTimelineKey;
import team.creative.creativecore.client.gui.control.tree.GuiClientTree;
import team.creative.creativecore.client.gui.control.tree.GuiClientTreeItem;
import team.creative.creativecore.client.gui.manager.GuiClientManager;
import team.creative.creativecore.client.gui.manager.GuiClientManagerItem;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.control.collection.GuiCheckList;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxExtension;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxFlexible;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxTree;
import team.creative.creativecore.common.gui.control.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelectorExtension;
import team.creative.creativecore.common.gui.control.inventory.GuiInventoryGrid;
import team.creative.creativecore.common.gui.control.inventory.GuiInventoryGridPreview;
import team.creative.creativecore.common.gui.control.inventory.GuiSlot;
import team.creative.creativecore.common.gui.control.inventory.GuiSlotViewer;
import team.creative.creativecore.common.gui.control.menu.GuiMenu.GuiMenuEntry;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot;
import team.creative.creativecore.common.gui.control.menu.GuiMenuSub;
import team.creative.creativecore.common.gui.control.parent.GuiPanel;
import team.creative.creativecore.common.gui.control.parent.GuiScrollX;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.parent.GuiTable;
import team.creative.creativecore.common.gui.control.parent.GuiTableScrollable;
import team.creative.creativecore.common.gui.control.parent.GuiTabs;
import team.creative.creativecore.common.gui.control.simple.GuiArraySlider;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiButtonContext;
import team.creative.creativecore.common.gui.control.simple.GuiButtonHold;
import team.creative.creativecore.common.gui.control.simple.GuiButtonIcon;
import team.creative.creativecore.common.gui.control.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.control.simple.GuiCheckButtonIcon;
import team.creative.creativecore.common.gui.control.simple.GuiColorPlate;
import team.creative.creativecore.common.gui.control.simple.GuiColoredSteppedSlider;
import team.creative.creativecore.common.gui.control.simple.GuiIcon;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiListEntry;
import team.creative.creativecore.common.gui.control.simple.GuiProgressbar;
import team.creative.creativecore.common.gui.control.simple.GuiSeekBar;
import team.creative.creativecore.common.gui.control.simple.GuiShowItem;
import team.creative.creativecore.common.gui.control.simple.GuiSlider;
import team.creative.creativecore.common.gui.control.simple.GuiStateButton;
import team.creative.creativecore.common.gui.control.simple.GuiStateButtonIcon;
import team.creative.creativecore.common.gui.control.simple.GuiSteppedSlider;
import team.creative.creativecore.common.gui.control.simple.GuiTabButton;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.control.timeline.GuiTimeline;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineChannel;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineHeader;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineKey;
import team.creative.creativecore.common.gui.control.tree.GuiTree;
import team.creative.creativecore.common.gui.control.tree.GuiTreeItem;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;
import team.creative.creativecore.common.util.type.list.PairList;

public class GuiClientRegistry {
    
    private static final PairList<Class<? extends GuiControl>, Function<GuiControl, GuiClientControl>> CONTROL_FACTORY = new PairList<>();
    private static final Object2ObjectMap<Class<? extends GuiControl>, Function<GuiControl, GuiClientControl>> BACKUP_CONTROL_FACTORY = new Object2ObjectArrayMap<>();
    private static final List<Function<GuiControl, GuiClientControl>> CONTROL_SPECIAL_FACTORY = new ArrayList<>();
    
    private static final Object2ObjectMap<Class<? extends GuiManager>, Function<GuiManager, GuiClientManager>> MANAGER_FACTORY = new Object2ObjectArrayMap<>();
    
    public static GuiClientControl create(GuiControl control) {
        var function = CONTROL_FACTORY.getValue(control.getClass());
        if (function != null)
            return function.apply(control);
        for (int i = 0; i < CONTROL_SPECIAL_FACTORY.size(); i++) {
            var result = CONTROL_SPECIAL_FACTORY.get(i).apply(control);
            if (result != null)
                return result;
        }
        function = BACKUP_CONTROL_FACTORY.get(control.getClass());
        if (function == null) {
            for (int i = CONTROL_FACTORY.size(); --i >= 0;) { // Go in reverse order as this will most likely get a better dist handler from a "higher" superclass. Otherwise all would default to the standard GuiClientControl
                var pair = CONTROL_FACTORY.get(i);
                if (pair.key.isInstance(control)) {
                    BACKUP_CONTROL_FACTORY.put(pair.key, function = pair.value);
                    break;
                }
            }
            if (function == null)
                throw new IllegalArgumentException("No dist handler found for control " + control);
        }
        return function.apply(control);
        
    }
    
    public static GuiClientManager create(GuiManager manager) {
        var function = MANAGER_FACTORY.get(manager.getClass());
        if (function != null)
            return function.apply(manager);
        return null;
    }
    
    public static <T extends GuiControl> void register(Class<T> clazz, Function<T, GuiClientControl> factory) {
        CONTROL_FACTORY.add(clazz, (Function<GuiControl, GuiClientControl>) factory);
    }
    
    public static <T extends GuiManager> void registerManager(Class<T> clazz, Function<T, GuiClientManager> factory) {
        MANAGER_FACTORY.put(clazz, (Function<GuiManager, GuiClientManager>) factory);
    }
    
    public static void registerSpecial(Function<GuiControl, GuiClientControl> factory) {
        CONTROL_SPECIAL_FACTORY.add(factory);
    }
    
    static {
        register(GuiParent.class, GuiClientParent::new);
        register(GuiLayer.class, GuiClientLayer::new);
        register(GuiLabel.class, GuiClientLabel::new);
        register(GuiButton.class, GuiClientButton::new);
        register(GuiButtonHold.class, GuiClientButtonHold::new);
        register(GuiButtonContext.class, GuiClientButtonContext::new);
        register(GuiButtonContextMenu.class, GuiClientButtonContextMenu::new);
        register(GuiTextfield.class, GuiClientTextfield::new);
        register(GuiProgressbar.class, GuiClientProgressbar::new);
        register(GuiIcon.class, GuiClientIcon::new);
        register(GuiButtonIcon.class, GuiClientButtonIcon::new);
        register(GuiCheckButtonIcon.class, GuiClientCheckButtonIcon::new);
        register(GuiCheckBox.class, GuiClientCheckBox::new);
        register(GuiTabButton.class, GuiClientTabButton::new);
        register(GuiSlider.class, GuiClientSlider::new);
        register(GuiSteppedSlider.class, GuiClientSteppedSlider::new);
        register(GuiColoredSteppedSlider.class, GuiClientColoredSteppedSlider::new);
        register(GuiArraySlider.class, GuiClientArraySlider::new);
        register(GuiColorPlate.class, GuiClientColorPlate::new);
        register(GuiListEntry.class, GuiClientListEntry::new);
        register(GuiSeekBar.class, GuiClientSeekBar::new);
        register(GuiShowItem.class, GuiClientShowItem::new);
        register(GuiStateButton.class, GuiClientStateButton::new);
        register(GuiStateButtonIcon.class, GuiClientStateButtonIcon::new);
        
        register(GuiPanel.class, GuiClientPanel::new);
        register(GuiScrollX.class, GuiClientScrollX::new);
        register(GuiScrollXY.class, GuiClientScrollXY::new);
        register(GuiScrollY.class, GuiClientScrollY::new);
        register(GuiTable.class, GuiClientTable::new);
        register(GuiTableScrollable.class, GuiClientTableScrollable::new);
        register(GuiTabs.class, GuiClientTabs::new);
        
        register(GuiCheckList.class, GuiClientCheckList::new);
        register(GuiListBoxBase.class, GuiClientListBoxBase::new);
        register(GuiComboBox.class, GuiClientComboBox::new);
        register(GuiComboBoxExtension.class, GuiClientComboBoxExtension::new);
        register(GuiComboBoxFlexible.class, GuiClientComboBoxFlexible::new);
        register(GuiComboBoxTree.class, GuiClientComboBoxTree::new);
        register(GuiStackSelector.class, GuiClientStackSelector::new);
        register(GuiStackSelectorExtension.class, GuiClientStackSelectorExtension::new);
        
        register(GuiMenuEntry.class, GuiClientMenuEntry::new);
        register(GuiMenuRoot.class, GuiClientMenuRoot::new);
        register(GuiMenuSub.class, GuiClientMenuSub::new);
        
        register(GuiInventoryGrid.class, GuiClientInventoryGrid::new);
        register(GuiInventoryGridPreview.class, GuiClientInventoryGridPreview::new);
        register(GuiSlotViewer.class, GuiClientSlotViewer::new);
        register(GuiSlot.class, GuiClientSlot::new);
        
        register(GuiTimelineKey.class, GuiClientTimelineKey::new);
        register(GuiTimelineChannel.class, GuiClientTimelineChannel::new);
        register(GuiTimelineHeader.class, GuiClientTimelineHeader::new);
        register(GuiTimeline.class, GuiClientTimeline::new);
        
        register(GuiTree.class, GuiClientTree::new);
        register(GuiTreeItem.class, GuiClientTreeItem::new);
        
        registerManager(GuiManagerItem.class, GuiClientManagerItem::new);
    }
    
}
