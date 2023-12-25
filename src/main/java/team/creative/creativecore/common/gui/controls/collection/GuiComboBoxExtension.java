package team.creative.creativecore.common.gui.controls.collection;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxExtension.GuiComboBoxEntry;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiComboBoxEntry> {
    
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
        
        List<GuiComboBoxEntry> entries = new ArrayList<>();
        for (int i = 0; i < comboBox.lines.length; i++)
            if (search == null || comboBox.lines[i].contains(search))
                entries.add(new GuiComboBoxEntry("" + i, i, i == comboBox.getIndex()).set(comboBox.lines[i].copy()));
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
    
    public class GuiComboBoxEntry extends GuiLabel {
        
        public final int index;
        public final boolean selected;
        
        public GuiComboBoxEntry(String name, int index, boolean selected) {
            super(name);
            this.index = index;
            this.selected = selected;
            this.setExpandableX();
        }
        
        public GuiComboBoxEntry set(CompiledText text) {
            this.text = text;
            return this;
        }
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
            if (selected)
                text.setDefaultColor(rect.inside(mouseX, mouseY) ? ColorUtils.rgba(230, 230, 0, 255) : ColorUtils.rgba(200, 200, 0, 255));
            else if (rect.inside(mouseX, mouseY))
                text.setDefaultColor(ColorUtils.YELLOW);
            else
                text.setDefaultColor(ColorUtils.WHITE);
            super.renderContent(graphics, control, rect, mouseX, mouseY);
            text.setDefaultColor(ColorUtils.WHITE);
        }
        
        @Override
        public boolean mouseClicked(Rect rect, double x, double y, int button) {
            comboBox.select(index);
            comboBox.closeBox();
            playSound(SoundEvents.UI_BUTTON_CLICK);
            return true;
        }
        
    }
    
}
