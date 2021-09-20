package team.creative.creativecore.common.gui.controls.collection;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxExtension.GuiComboBoxEntry;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiComboBoxEntry> {
    
    public GuiComboBox comboBox;
    
    public GuiComboBoxExtension(String name, GuiComboBox comboBox) {
        super(name, false, new ArrayList<>());
        this.comboBox = comboBox;
        List<GuiComboBoxEntry> entries = new ArrayList<>();
        for (int i = 0; i < comboBox.lines.length; i++)
            entries.add(new GuiComboBoxEntry("" + i, i, i == comboBox.getIndex()).set(comboBox.lines[i]));
        addAllItems(entries);
    }
    
    @Override
    public void looseFocus() {
        comboBox.closeBox();
    }
    
    public class GuiComboBoxEntry extends GuiLabel {
        
        public final int index;
        public final boolean selected;
        
        public GuiComboBoxEntry(String name, int index, boolean selected) {
            super(name);
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
        protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
            if (selected)
                text.defaultColor = rect.inside(mouseX, mouseY) ? ColorUtils.toInt(230, 230, 0, 255) : ColorUtils.toInt(200, 200, 0, 255);
            else if (rect.inside(mouseX, mouseY))
                text.defaultColor = ColorUtils.YELLOW;
            else
                text.defaultColor = ColorUtils.WHITE;
            super.renderContent(matrix, control, rect, mouseX, mouseY);
            text.defaultColor = ColorUtils.WHITE;
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
