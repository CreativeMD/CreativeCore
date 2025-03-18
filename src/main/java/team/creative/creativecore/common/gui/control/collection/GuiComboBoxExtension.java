package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.control.simple.GuiRowLabel;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiRowLabel> {
    
    public GuiExtensionCreator<? extends GuiComboBox<?>, ? extends GuiComboBoxExtension> creator;
    public String search = "";
    
    public GuiComboBoxExtension(String name, GuiExtensionCreator<? extends GuiComboBox<?>, ? extends GuiComboBoxExtension> creator) {
        super(name, false, new ArrayList<>());
        this.creator = creator;
        
        registerEventChanged((event) -> {
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
        
        GuiTextfield textfield = get("searchBar");
        
        clearItems();
        
        if (search != null && search.isBlank())
            search = null;
        
        var box = creator.parent;
        
        if (box.hasSearchbar()) {
            if (textfield == null) {
                textfield = new GuiTextfield("searchBar", search == null ? "" : search);
                addCustomControl(textfield.setExpandableX());
            }
            textfield.focus();
        }
        
        List<GuiRowLabel> entries = new ArrayList<>();
        int i = 0;
        for (CompiledText text : box.lines()) {
            if (search == null || text.contains(search)) {
                final int index = i;
                entries.add(new GuiRowLabel("" + i, i, i == box.selectedIndex(), x -> {
                    creator.parent.select(index);
                    creator.close();
                }).set(text.copy()));
            }
            i++;
        }
        addAllItems(entries);
        
        if (hasGui())
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
