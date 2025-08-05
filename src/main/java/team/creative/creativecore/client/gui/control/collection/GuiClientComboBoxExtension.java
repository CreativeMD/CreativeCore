package team.creative.creativecore.client.gui.control.collection;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.client.gui.control.simple.GuiClientLabel;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxExtension;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxExtension.GuiComboBoxExtensionDist;
import team.creative.creativecore.common.gui.control.simple.GuiListEntry;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;

public class GuiClientComboBoxExtension<T extends GuiComboBoxExtension> extends GuiClientListBoxBase<T> implements GuiComboBoxExtensionDist {
    
    public GuiExtensionCreator<? extends GuiClientComboBox<?, ?>, ? extends GuiComboBoxExtension> creator;
    public String search = "";
    
    public GuiClientComboBoxExtension(T control) {
        super(control);
    }
    
    @Override
    public void init() {
        control.registerEventChanged((event) -> {
            if (event.control.is("searchBar")) {
                search = ((GuiTextfield) event.control).getText();
                reloadControls();
            }
        });
        reloadControls();
    }
    
    @Override
    public void looseFocus() {
        creator.markLostFocus();
    }
    
    public void reloadControls() {
        if (creator == null)
            return;
        
        GuiTextfield textfield = control.get("searchBar");
        
        control.clearItems();
        
        if (search != null && search.isBlank())
            search = null;
        
        var box = creator.parent;
        
        if (box.hasSearchbar()) {
            if (textfield == null) {
                textfield = new GuiTextfield(control, "searchBar", search == null ? "" : search);
                control.addCustomControl(textfield.setExpandableX());
            }
            textfield.dist().focus();
        }
        
        List<GuiListEntry> entries = new ArrayList<>();
        int i = 0;
        for (CompiledText text : box.lines()) {
            if (search == null || text.contains(search)) {
                final int index = i;
                var label = new GuiListEntry(control, "" + i, i, i == box.selectedIndex(), x -> {
                    creator.parent.select(index);
                    creator.close();
                });
                ((GuiClientLabel) label.dist()).setText(text.copy());
                entries.add(label);
            }
            i++;
        }
        control.addAllItems(entries);
        
        if (control.hasGui())
            reflowInternal();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button)) {
            creator.markKeptFocus();
            return true;
        }
        return false;
    }
    
    @Override
    protected int maxHeight(int width, int availableWidth) {
        return 100;
    }
}
