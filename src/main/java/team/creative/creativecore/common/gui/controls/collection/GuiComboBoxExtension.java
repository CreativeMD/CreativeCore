package team.creative.creativecore.common.gui.controls.collection;

import team.creative.creativecore.common.gui.controls.simple.GuiRowLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.util.math.geo.Rect;

import java.util.ArrayList;
import java.util.List;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiRowLabel> {
    
    public GuiComboBox comboBox;
    public String search = "";
    
    public GuiComboBoxExtension(String name, GuiComboBox comboBox) {
        super(name, false, new ArrayList<>());
        this.comboBox = comboBox;
        
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
        comboBox.extensionLostFocus = true;
    }
    
    public void reloadControls() {
        if (comboBox == null)
            return;
        
        GuiTextfield textfield = get("searchBar");
        
        clearItems();
        
        if (search != null && search.isBlank())
            search = null;
        
        if (comboBox.hasSearchbar()) {
            if (textfield == null) {
                textfield = new GuiTextfield("searchBar", search == null ? "" : search);
                addCustomControl(textfield.setExpandableX());
            }
            textfield.focus();
        }
        
        List<GuiRowLabel> entries = new ArrayList<>();
        for (int i = 0; i < comboBox.lines.length; i++)
            if (search == null || comboBox.lines[i].contains(search)) {
                final int index = i;
                entries.add(new GuiRowLabel("" + i, i, i == comboBox.getIndex(), x -> {
                    comboBox.select(index);
                    comboBox.closeBox();
                }).set(comboBox.lines[i].copy()));
            }
        addAllItems(entries);
        
        if (hasGui())
            reflowInternal();
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (super.mouseClicked(rect, x, y, button)) {
            comboBox.extensionLostFocus = false;
            return true;
        }
        return false;
    }
    
    @Override
    protected int maxHeight(int width, int availableWidth) {
        return 100;
    }
    
}
