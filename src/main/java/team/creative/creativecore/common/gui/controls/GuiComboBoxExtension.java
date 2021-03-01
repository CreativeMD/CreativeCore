package team.creative.creativecore.common.gui.controls;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.controls.GuiComboBoxExtension.GuiComboBoxEntry;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiComboBoxEntry> {
    
    public GuiComboBox comboBox;
    
    public GuiComboBoxExtension(String name, GuiComboBox comboBox, int x, int y, int width, int height) {
        super(name, x, y, width, height, false, new ArrayList<>());
        this.comboBox = comboBox;
        List<GuiComboBoxEntry> entries = new ArrayList<>();
        for (int i = 0; i < comboBox.lines.length; i++)
            entries.add(new GuiComboBoxEntry("" + i, 0, 0, width - 4, i, i == comboBox.getIndex()).set(comboBox.lines[i]));
        addAllItems(entries);
    }
    
    @Override
    public void looseFocus() {
        comboBox.closeBox();
    }
    
    @Override
    public boolean canOverlap() {
        return true;
    }
    
    public class GuiComboBoxEntry extends GuiLabelFixed {
        
        public final int index;
        public final boolean selected;
        
        public GuiComboBoxEntry(String name, int x, int y, int width, int index, boolean selected) {
            super(name, x, y, width, 10);
            this.index = index;
            this.selected = selected;
            this.text.alignment = Align.CENTER;
        }
        
        public GuiComboBoxEntry set(CompiledText text) {
            this.text = text;
            return this;
        }
        
        @Override
        @OnlyIn(value = Dist.CLIENT)
        protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
            if (selected)
                text.defaultColor = rect.inside(mouseX, mouseY) ? ColorUtils.toInt(230, 230, 0, 255) : ColorUtils.toInt(200, 200, 0, 255);
            else if (rect.inside(mouseX, mouseY))
                text.defaultColor = ColorUtils.YELLOW;
            else
                text.defaultColor = ColorUtils.WHITE;
            super.renderContent(matrix, rect, mouseX, mouseY);
            text.defaultColor = ColorUtils.WHITE;
        }
        
        @Override
        public boolean mouseClicked(double x, double y, int button) {
            comboBox.select(index);
            comboBox.closeBox();
            return true;
        }
        
    }
    
}
