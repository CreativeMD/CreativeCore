package team.creative.creativecore.common.util.text;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class LinebreakComponent extends MutableComponent implements AdvancedComponent {
    
    public LinebreakComponent() {
        super(ComponentContents.EMPTY, Lists.newArrayList(), Style.EMPTY);
    }
    
    @Override
    public MutableComponent plainCopy() {
        return new LinebreakComponent();
    }
    
    @Override
    public int getWidth(Font font) {
        return 0;
    }
    
    @Override
    public int getHeight(Font font) {
        return 0;
    }
    
    @Override
    public boolean canSplit() {
        return false;
    }
    
    @Override
    public List<AdvancedComponent> split(int width, boolean force) {
        return null;
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public void render(PoseStack stack, Font font, int defaultColor) {}
    
}
