package team.creative.creativecore.common.util.text;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;

public interface IAdvancedTextComponent extends FormattedText {
    
    public int getWidth(Font font);
    
    public int getHeight(Font font);
    
    public boolean canSplit();
    
    public List<IAdvancedTextComponent> split(int width, boolean force);
    
    public boolean isEmpty();
    
    public void render(PoseStack stack, Font font, int defaultColor);
    
}
