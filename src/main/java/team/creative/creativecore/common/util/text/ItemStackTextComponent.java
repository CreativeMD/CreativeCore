package team.creative.creativecore.common.util.text;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponent;
import team.creative.creativecore.client.render.GuiRenderHelper;

public class ItemStackTextComponent extends TextComponent implements IAdvancedTextComponent {
	
	public final ItemStack stack;
	
	public ItemStackTextComponent(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public int getWidth(FontRenderer font) {
		return 16;
	}
	
	@Override
	public int getHeight(FontRenderer font) {
		return 12;
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
		return false;
	}
	
	@Override
	public void render(MatrixStack stack, FontRenderer font, int defaultColor) {
		stack.push();
		stack.translate(-2, -2, 0);
		GuiRenderHelper.drawItemStack(stack, this.stack);
		stack.pop();
	}
	
	@Override
	public TextComponent copyRaw() {
		return new ItemStackTextComponent(stack);
	}
	
}
