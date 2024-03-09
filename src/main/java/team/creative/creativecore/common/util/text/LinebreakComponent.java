package team.creative.creativecore.common.util.text;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;

public class LinebreakComponent extends MutableComponentCreative implements AdvancedComponent {
    
    public LinebreakComponent() {
        super("", Lists.newArrayList(), Style.EMPTY);
    }
    
    @Override
    public MutableComponent plainCopy() {
        return null; //new LinebreakComponent();
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
