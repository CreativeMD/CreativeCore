package team.creative.creativecore.common.gui.controls;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.CompiledText;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiLabel extends GuiControl {
	
	private CompiledText text;
	private boolean autosize;
	public int color = ColorUtils.WHITE;
	
	public GuiLabel(String name, int x, int y) {
		super(name, x, y, 1, 1);
		this.autosize = true;
	}
	
	public GuiLabel(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	private void updateSize() {
		if (getParent() != null && isClient() && autosize) {
			width = getContentOffset() * 2 + text.usedWidth;
			height = getContentOffset() * 2 + text.usedHeight;
		}
	}
	
	public GuiLabel setTitle(ITextComponent component) {
		if (text == null)
			if (getParent() == null)
				if (autosize)
					text = new CompiledText(Integer.MAX_VALUE, Integer.MAX_VALUE);
				else
					text = new CompiledText(width, height);
			else
				text = new CompiledText(getParentContentWidth() - x, getParentContentWidth() - y);
		text.setText(component);
		return this;
	}
	
	public GuiLabel setTitle(List<ITextComponent> components) {
		if (text == null)
			if (getParent() == null)
				if (autosize)
					text = new CompiledText(Integer.MAX_VALUE, Integer.MAX_VALUE);
				else
					text = new CompiledText(width, height);
			else
				text = new CompiledText(getParentContentWidth() - x, getParentContentWidth() - y);
		text.setText(components);
		return this;
	}
	
	@Override
	public void init() {
		if (text == null)
			setTitle(new TranslationTextComponent(getNestedName()));
		else if (autosize)
			text.setDimension(getParentContentWidth() - x, getParentContentWidth() - y);
	}
	
	@Override
	public void closed() {}
	
	@Override
	public void tick() {}
	
	@Override
	public ControlFormatting getControlFormatting() {
		return ControlFormatting.TRANSPARENT;
	}
	
	@Override
	@OnlyIn(value = Dist.CLIENT)
	protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
		text.render(matrix);
		updateSize();
	}
	
}
