package team.creative.creativecore.common.util.text;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextComponent;

public class LinebreakTextComponent extends TextComponent implements IAdvancedTextComponent {
	
	@Override
	public TextComponent copyRaw() {
		return new LinebreakTextComponent();
	}
	
	@Override
	public int getWidth(FontRenderer font) {
		return 0;
	}
	
	@Override
	public int getHeight(FontRenderer font) {
		return 0;
	}
	
	@Override
	public boolean canSplit() {
		return false;
	}
	
	@Override
	public List<IAdvancedTextComponent> split(int width, boolean force) {
		return null;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public void render(MatrixStack stack, FontRenderer font, int defaultColor) {}
	
}
