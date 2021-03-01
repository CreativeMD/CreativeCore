package team.creative.creativecore.common.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.CompiledText;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.text.TextListBuilder;

public class GuiStateButton extends GuiButton {
    
    private int index = 0;
    public CompiledText[] states;
    public boolean autosize;
    
    public GuiStateButton(String name, int x, int y, int index, TextListBuilder states) {
        super(name, x, y, null);
        this.pressed = button -> {
            if (button == 1)
                previousState();
            else
                nextState();
        };
        this.index = index;
        this.autosize = true;
        buildStates(states);
    }
    
    public GuiStateButton(String name, int x, int y, int index, String... states) {
        this(name, x, y, index, new TextListBuilder().add(states));
    }
    
    protected void buildStates(TextListBuilder builder) {
        states = new CompiledText[builder.size()];
        for (int i = 0; i < builder.size(); i++) {
            states[i] = CompiledText.createAnySize();
            states[i].setText(builder.get(i));
        }
        if (index >= states.length)
            index = 0;
    }
    
    @Override
    public void setWidthLayout(int width) {
        int contentOffset = getContentOffset() * 2;
        int height = 0;
        for (CompiledText text : states) {
            text.setDimension(width, Integer.MAX_VALUE);
            height = Math.max(height, text.getTotalHeight() + contentOffset);
        }
        setWidth(width);
    }
    
    @Override
    public void setHeightLayout(int height) {
        for (CompiledText text : states)
            text.setMaxHeight(height);
        setHeight(height);
    }
    
    @Override
    public int getPreferredWidth() {
        int contentOffset = getContentOffset() * 2;
        int width = 0;
        for (CompiledText text : states)
            width = Math.max(width, text.getTotalWidth() + contentOffset);
        return width;
    }
    
    @Override
    public int getPreferredHeight() {
        int contentOffset = getContentOffset() * 2;
        int height = 0;
        for (CompiledText text : states)
            height = Math.max(height, text.getTotalHeight() + contentOffset);
        return height;
    }
    
    public void setState(int index) {
        this.index = index;
    }
    
    public int getState() {
        return index;
    }
    
    public void previousState() {
        int state = getState();
        state--;
        if (state < 0)
            state = states.length - 1;
        if (state >= states.length)
            state = 0;
        setState(state);
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public void nextState() {
        int state = getState();
        state++;
        if (state < 0)
            state = states.length - 1;
        if (state >= states.length)
            state = 0;
        setState(state);
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
        CompiledText text = states[index];
        matrix.translate(rect.getWidth() / 2 - text.usedWidth / 2, rect.getHeight() / 2 - text.usedHeight / 2, 0);
        text.render(matrix);
    }
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
}
