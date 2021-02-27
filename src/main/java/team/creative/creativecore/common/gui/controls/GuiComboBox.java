package team.creative.creativecore.common.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.CompiledText;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.text.TextListBuilder;

public class GuiComboBox extends GuiLabel {
    
    protected boolean autosize;
    public int color = ColorUtils.WHITE;
    protected GuiComboBoxExtension extension;
    public CompiledText[] lines;
    private int index;
    
    public GuiComboBox(String name, int x, int y, TextListBuilder lines) {
        super(name, x, y);
        buildStates(lines);
        updateDisplay();
    }
    
    protected void buildStates(TextListBuilder builder) {
        lines = new CompiledText[builder.size()];
        for (int i = 0; i < builder.size(); i++) {
            lines[i] = create();
            lines[i].setText(builder.get(i));
        }
        if (index >= lines.length)
            index = 0;
    }
    
    @Override
    public void tick() {}
    
    @Override
    public void closed() {}
    
    protected void updateDisplay() {
        text = lines[index];
    }
    
    @Override
    public void setWidthLayout(int width) {
        int contentOffset = getContentOffset() * 2;
        int height = 0;
        for (CompiledText text : lines) {
            text.setDimension(width, Integer.MAX_VALUE);
            height = Math.max(height, text.getTotalHeight() + contentOffset);
        }
        setWidth(width);
    }
    
    @Override
    public void setHeightLayout(int height) {
        for (CompiledText text : lines)
            text.setMaxHeight(height);
        setHeight(height);
    }
    
    @Override
    public int getPreferredWidth() {
        int contentOffset = getContentOffset() * 2;
        int width = 0;
        for (CompiledText text : lines)
            width = Math.max(width, text.getTotalWidth() + contentOffset);
        return width;
    }
    
    @Override
    public int getPreferredHeight() {
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
    public boolean mouseClicked(double x, double y, int button) {
        if (extension == null)
            openBox();
        else
            closeBox();
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
        text.render(matrix);
    }
    
    public void openBox() {
        this.extension = createBox();
        GuiLayer layer = getLayer();
        layer.add(extension);
        layer.moveInFront(extension);
        extension.init();
        extension.setX(getControlOffsetX() - getContentOffset());
        extension.setY(getControlOffsetY() - getContentOffset() + getHeight());
        
        if (extension.getY() + extension.getHeight() > layer.getHeight() && this.getY() >= extension.getHeight())
            extension.setY(extension.getY() - this.getHeight() + extension.getHeight());
    }
    
    protected GuiComboBoxExtension createBox() {
        return new GuiComboBoxExtension(name + "extension", this, getX(), getY() + getHeight(), getWidth(), 100);
    }
    
    public void closeBox() {
        if (extension != null) {
            getLayer().remove(extension);
            extension = null;
        }
    }
}
