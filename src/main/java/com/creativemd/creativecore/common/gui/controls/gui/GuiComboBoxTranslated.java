package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.ArrayList;
import java.util.List;

public class GuiComboBoxTranslated extends GuiComboBox {
    
    public String prefix;
    public List<String> translatedLines;
    
    public GuiComboBoxTranslated(String name, int x, int y, int width, String prefix, List<String> lines) {
        super(name, x, y, width, lines);
        this.prefix = prefix;
        List<String> translatedLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++)
            translatedLines.add(getDisplay(i));
        this.translatedLines = translatedLines;
    }
    
    @Override
    public String getDisplay(int index) {
        if (translatedLines != null)
            return translatedLines.get(index);
        return translateOrDefault(prefix + "." + lines.get(index), lines.get(index));
    }
    
    @Override
    protected GuiComboBoxExtension createBox() {
        return new GuiComboBoxExtension(name + "extension", this, posX, posY + height, width - getContentOffset() * 2, 100, translatedLines);
    }
    
}
