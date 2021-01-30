package team.creative.creativecore.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.Rect;

public abstract class GuiControl {
	
	private IGuiParent parent;
	public final String name;
	public boolean enabled = true;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public boolean visible;
	
	public GuiControl(String name, int x, int y, int width, int height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	// BASICS
	
	public GuiControl setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public void setParent(IGuiParent parent) {
		this.parent = parent;
	}
	
	public IGuiParent getParent() {
		return parent;
	}
	
	public GuiLayer getLayer() {
		if (parent instanceof GuiControl)
			return ((GuiControl) parent).getLayer();
		throw new RuntimeException("Invalid layer control");
	}
	
	public GuiStyle getStyle() {
		if (parent instanceof GuiControl)
			return ((GuiControl) parent).getStyle();
		throw new RuntimeException("Invalid layer control");
	}
	
	public abstract void init();
	
	public abstract void closed();
	
	public abstract void tick();
	
	public boolean is(String... name) {
		for (int i = 0; i < name.length; i++) {
			if (this.name.equalsIgnoreCase(name[i]))
				return true;
		}
		return false;
	}
	
	// INTERACTION
	
	public boolean isInterable() {
		return enabled && visible;
	}
	
	public void mouseMoved(double x, double y) {}
	
	public boolean mouseClicked(double x, double y, int button) {
		return false;
	}
	
	public boolean mouseDoubleClicked(double x, double y, int button) {
		return mouseClicked(x, y, button);
	}
	
	public boolean mouseReleased(double x, double y, int button) {
		return false;
	}
	
	public boolean mouseDragged(double x, double y, int button, double dragX, double dragY, double time) {
		return false;
	}
	
	public boolean mouseScrolled(double x, double y, double delta) {
		return false;
	}
	
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	public boolean charTyped(char codePoint, int modifiers) {
		return false;
	}
	
	public void looseFocus() {}
	
	// APPERANCE
	
	public abstract ControlFormatting getControlFormatting();
	
	public int getContentOffset() {
		return 0;
	}
	
	// RENDERING
	
	@OnlyIn(value = Dist.CLIENT)
	public boolean canOverlap() {
		return false;
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public void render(MatrixStack matrix, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
		Rect rectCopy = null;
		if (!enabled)
			rectCopy = controlRect.copy();
		
		GuiStyle style = getStyle();
		ControlFormatting formatting = getControlFormatting();
		
		style.get(formatting.border).render(matrix, controlRect);
		
		int borderWidth = style.getBorder(formatting.border);
		controlRect.shrink(borderWidth);
		style.get(formatting.face, realRect.inside(mouseX, mouseY)).render(matrix, controlRect);
		
		int margin = formatting.margin;
		controlRect.shrink(margin);
		renderContent(matrix, controlRect, mouseX - borderWidth - margin, mouseY - borderWidth - margin);
		
		if (!enabled) {
			realRect.scissor();
			style.disabled.render(matrix, rectCopy);
		}
	}
	
	@OnlyIn(value = Dist.CLIENT)
	protected abstract void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY);
	
	// MINECRAFT
	
	public PlayerEntity getPlayer() {
		return parent.getPlayer();
	}
	
	public boolean isRemote() {
		return getPlayer().world.isRemote;
	}
	
	// MANAGEMENT
	
	public void moveBehind(GuiControl reference) {
		parent.moveBehind(this, reference);
	}
	
	public void moveInFront(GuiControl reference) {
		parent.moveInFront(this, reference);
	}
	
	public void moveTop() {
		parent.moveTop(this);
	}
	
	public void moveBottom() {
		parent.moveBottom(this);
	}
	
	// UTILS
	
	@OnlyIn(value = Dist.CLIENT)
	public static String translate(String text, Object... parameters) {
		return I18n.format(text, parameters);
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public static String translateOrDefault(String text, String defaultText) {
		if (I18n.hasKey(text))
			return translate(text);
		return defaultText;
	}
	
}
