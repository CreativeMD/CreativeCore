package team.creative.creativecore.common.gui.controls.collection;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.ITextCollection;

public class GuiComboBox extends GuiLabel {
    
    protected GuiComboBoxExtension extension;
    public CompiledText[] lines;
    private int index;
    public boolean extensionLostFocus;
    
    public GuiComboBox(String name, ITextCollection builder) {
        super(name);
        lines = builder.build();
        if (index >= lines.length)
            index = 0;
        for (int i = 0; i < lines.length; i++)
            lines[i].alignment = Align.CENTER;
        updateDisplay();
    }
    
    @Override
    public void tick() {}
    
    @Override
    public void closed() {}
    
    protected void updateDisplay() {
        text = lines[index];
    }
    
    @Override
    public void flowX(int width, int preferred) {
        for (CompiledText text : lines)
            text.setDimension(width, Integer.MAX_VALUE);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        for (CompiledText text : lines)
            text.setMaxHeight(height);
    }
    
    @Override
    public int preferredWidth() {
        int contentOffset = getContentOffset() * 2;
        int width = 0;
        for (CompiledText text : lines)
            width = Math.max(width, text.getTotalWidth() + contentOffset);
        return width;
    }
    
    @Override
    public int preferredHeight(int width) {
        int contentOffset = getContentOffset() * 2;
        int height = 0;
        for (CompiledText text : lines)
            height = Math.max(height, text.getTotalHeight() + contentOffset);
        return height;
    }
    
    public int getIndex() {
        return index;
    }
    
    public boolean select(int index) {
        if (index >= 0 && index < lines.length) {
            this.index = index;
            updateDisplay();
            raiseEvent(new GuiControlChangedEvent(this));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (extension == null)
            openBox(rect);
        else
            closeBox();
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    public void openBox(Rect rect) {
        this.extension = createBox();
        GuiLayer layer = getLayer();
        GuiChildControl child = layer.addHover(extension);
        extension.init();
        child.setX((int) rect.minX);
        child.setY((int) rect.maxY);
        
        child.setWidth((int) rect.getWidth());
        child.flowX();
        child.setHeight(child.getPreferredHeight());
        child.flowY();
        
        if (child.getY() + child.getHeight() > layer.getHeight() && rect.minY >= child.getHeight())
            child.setY(child.getY() - (int) rect.getHeight() + child.getHeight());
    }
    
    protected GuiComboBoxExtension createBox() {
        return new GuiComboBoxExtension(name + "extension", this);
    }
    
    public void closeBox() {
        if (extension != null) {
            getLayer().remove(extension);
            extension = null;
        }
    }
    
    @Override
    public void looseFocus() {
        if (extensionLostFocus && extension != null)
            closeBox();
    }
}
