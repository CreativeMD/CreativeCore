package team.creative.creativecore.common.gui.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team.creative.creativecore.client.render.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.controls.GuiComboBoxExtension.GuiComboBoxEntry;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiComboBoxEntry> {
    
    public GuiComboBox comboBox;
    
    public GuiComboBoxExtension(String name, GuiComboBox comboBox, int x, int y, int width, int height) {
        super(name, x, y, width, height, false, Collections.EMPTY_LIST);
        this.comboBox = comboBox;
        List<GuiComboBoxEntry> entries = new ArrayList<>();
        for (int i = 0; i < comboBox.lines.length; i++)
            entries.add(new GuiComboBoxEntry("" + i, 0, 0, i, i == comboBox.getIndex()).set(comboBox.lines[i]));
        addAllItems(entries);
    }
    
    @Override
    public void looseFocus() {
        //if (!comboBox.isMouseOver() && !isMouseOver())
        //comboBox.closeBox();
    }
    
    @Override
    public boolean canOverlap() {
        return true;
    }
    
    public class GuiComboBoxEntry extends GuiLabel {
        
        public final int index;
        
        public GuiComboBoxEntry(String name, int x, int y, int index, boolean selected) {
            super(name, x, y);
            this.index = index;
            this.color = selected ? ColorUtils.YELLOW : ColorUtils.WHITE;
        }
        
        public GuiComboBoxEntry set(CompiledText text) {
            this.text = text;
            return this;
        }
        
        @Override
        protected CompiledText create() {
            CompiledText text = super.create();
            text.alignment = Align.CENTER;
            return text;
        }
        
        @Override
        public boolean mouseClicked(double x, double y, int button) {
            comboBox.select(index);
            comboBox.closeBox();
            return true;
        }
        
    }
    
}
