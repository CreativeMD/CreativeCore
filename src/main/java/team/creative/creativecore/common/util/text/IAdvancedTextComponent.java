package team.creative.creativecore.common.util.text;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public interface IAdvancedTextComponent extends ITextComponent {
    
    public int getWidth(FontRenderer font);
    
    public int getHeight(FontRenderer font);
    
    public boolean canSplit();
    
    public List<IAdvancedTextComponent> split(int width, boolean force);
    
    public boolean isEmpty();
    
    public void render(MatrixStack stack, FontRenderer font, int defaultColor);
    
}
