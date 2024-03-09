package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.ITextCollection;
import team.creative.creativecore.common.util.text.TextListBuilder;

public class GuiStateButton extends GuiButton {
    
    private int index = 0;
    public CompiledText[] states;
    
    public GuiStateButton(String name, ITextCollection states) {
        this(name, 0, states);
    }
    
    public GuiStateButton(String name, int index, ITextCollection states) {
        super(name, null);
        this.pressed = button -> {
            if (button == 1)
                previousState();
            else
                nextState();
        };
        this.index = index;
        buildStates(states);
    }
    
    public GuiStateButton(String name, int index, String... states) {
        this(name, index, new TextListBuilder().add(states));
    }
    
    protected void buildStates(ITextCollection builder) {
        states = builder.build();
        if (index >= states.length)
            index = 0;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        for (CompiledText text : states)
            text.setDimension(width, Integer.MAX_VALUE);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        for (CompiledText text : states)
            text.setMaxHeight(height);
    }
    
    @Override
    public int preferredWidth(int availableWidth) {
        int width = 0;
        for (CompiledText text : states)
            width = Math.max(width, text.getTotalWidth());
        return width;
    }
    
    @Override
    public int preferredHeight(int width, int availableHeight) {
        int height = 0;
        for (CompiledText text : states)
            height = Math.max(height, text.getTotalHeight());
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
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        CompiledText text = states[index];
        PoseStack pose = graphics.pose();
        pose.translate(rect.getWidth() / 2f - text.getUsedWidth() / 2f, rect.getHeight() / 2f - text.getUsedHeight() / 2f, 0);
        text.render(pose);
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
