package team.creative.creativecore.common.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiControlClickEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiEventManager;
import team.creative.creativecore.common.util.math.Rect;

public abstract class GuiParent extends GuiControl implements IGuiParent, Iterable<GuiControl> {
	
	private GuiEventManager eventManager;
	private List<GuiControl> controls = new ArrayList<>();
	
	@OnlyIn(value = Dist.CLIENT)
	protected int lastRenderedHeight;
	
	public GuiParent(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	@Override
	public boolean isClient() {
		return getParent().isClient();
	}
	
	public float getScaleFactor() {
		return 1F;
	}
	
	public double getOffsetY() {
		return 0;
	}
	
	public double getOffsetX() {
		return 0;
	}
	
	@Override
	public Iterator<GuiControl> iterator() {
		return controls.iterator();
	}
	
	public GuiControl get(String name) {
		for (int i = 0; i < controls.size(); i++)
			if (controls.get(i).name.equalsIgnoreCase(name))
				return controls.get(i);
		return null;
	}
	
	public boolean has(String name) {
		for (int i = 0; i < controls.size(); i++)
			if (controls.get(i).name.equalsIgnoreCase(name))
				return true;
		return false;
	}
	
	public void add(GuiControl control) {
		control.setParent(this);
		controls.add(control);
	}
	
	public void remove(String... include) {
		controls.removeIf((x) -> ArrayUtils.contains(include, x.name));
	}
	
	public void removeExclude(String... exclude) {
		controls.removeIf((x) -> !ArrayUtils.contains(exclude, x.name));
	}
	
	public void clear() {
		controls.clear();
	}
	
	@Override
	public void moveBehind(GuiControl toMove, GuiControl reference) {
		controls.remove(toMove);
		int index = controls.indexOf(reference);
		if (index != -1 && index < controls.size() - 1)
			controls.add(index + 1, toMove);
		else
			moveBottom(toMove);
	}
	
	@Override
	public void moveInFront(GuiControl toMove, GuiControl reference) {
		controls.remove(toMove);
		int index = controls.indexOf(reference);
		if (index != -1)
			controls.add(index, toMove);
		else
			moveTop(toMove);
	}
	
	@Override
	public void moveTop(GuiControl toMove) {
		controls.remove(toMove);
		controls.add(0, toMove);
	}
	
	@Override
	public void moveBottom(GuiControl toMove) {
		controls.remove(toMove);
		controls.add(toMove);
	}
	
	@Override
	@OnlyIn(value = Dist.CLIENT)
	protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
		
		float scale = getScaleFactor();
		double xOffset = getOffsetX();
		double yOffset = getOffsetY();
		
		lastRenderedHeight = 0;
		
		for (int i = controls.size() - 1; i >= 0; i--) {
			GuiControl control = controls.get(i);
			
			if (!control.visible)
				continue;
			
			Rect controlRect = rect.child((int) ((control.x + xOffset) * scale), (int) ((control.y + yOffset) * scale), (int) (control.width * scale), (int) (control.height * scale));
			Rect realRect = rect.intersection(controlRect);
			if (realRect != null || control.canOverlap()) {
				if (control.canOverlap())
					RenderSystem.disableScissor();
				else
					realRect.scissor();
				
				matrix.push();
				matrix.translate((int) (control.x * scale), (int) (control.y * scale), 0);
				control.render(matrix, controlRect, realRect, mouseX, mouseY);
				matrix.pop();
			}
			
			lastRenderedHeight = (int) Math.max(lastRenderedHeight, (control.x + control.height) * scale);
			
		}
		
	}
	
	@Override
	public boolean isContainer() {
		return getParent().isContainer();
	}
	
	@Override
	public void init() {
		for (int i = 0; i < controls.size(); i++)
			controls.get(i).init();
	}
	
	@Override
	public void closed() {
		for (int i = 0; i < controls.size(); i++)
			controls.get(i).closed();
	}
	
	@Override
	public void tick() {
		for (int i = 0; i < controls.size(); i++)
			controls.get(i).tick();
	}
	
	@Override
	public void closeLayer(GuiLayer layer) {
		getParent().closeLayer(layer);
	}
	
	@Override
	public void openLayer(GuiLayer layer) {
		getParent().openLayer(layer);
	}
	
	@Override
	public void mouseMoved(double x, double y) {
		x *= getScaleFactor();
		y *= getScaleFactor();
		int offset = getContentOffset();
		x += getOffsetX() - offset;
		y += getOffsetY() - offset;
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable())
				control.mouseMoved(x - control.x, y - control.y);
		}
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		x *= getScaleFactor();
		y *= getScaleFactor();
		int offset = getContentOffset();
		x += getOffsetX() - offset;
		y += getOffsetY() - offset;
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.isMouseOver(x, y) && control.mouseClicked(x, y, button)) {
				raiseEvent(new GuiControlClickEvent(control, button, false));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseDoubleClicked(double x, double y, int button) {
		x *= getScaleFactor();
		y *= getScaleFactor();
		int offset = getContentOffset();
		x += getOffsetX() - offset;
		y += getOffsetY() - offset;
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.isMouseOver(x, y) && control.mouseDoubleClicked(x, y, button)) {
				raiseEvent(new GuiControlClickEvent(control, button, false));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void mouseReleased(double x, double y, int button) {
		x *= getScaleFactor();
		y *= getScaleFactor();
		int offset = getContentOffset();
		x += getOffsetX() - offset;
		y += getOffsetY() - offset;
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable())
				control.mouseReleased(x - control.x, y - control.y, button);
		}
	}
	
	@Override
	public void mouseDragged(double x, double y, int button, double dragX, double dragY, double time) {
		x *= getScaleFactor();
		y *= getScaleFactor();
		int offset = getContentOffset();
		x += getOffsetX() - offset;
		y += getOffsetY() - offset;
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable())
				control.mouseDragged(x - control.x, y - control.y, button, dragX, dragY, time);
		}
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double delta) {
		x *= getScaleFactor();
		y *= getScaleFactor();
		int offset = getContentOffset();
		x += getOffsetX() - offset;
		y += getOffsetY() - offset;
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.isMouseOver(x, y) && control.mouseScrolled(x, y, delta))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.keyPressed(keyCode, scanCode, modifiers))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.keyReleased(keyCode, scanCode, modifiers))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.charTyped(codePoint, modifiers))
				return true;
		}
		return false;
	}
	
	@Override
	public void looseFocus() {}
	
	@Override
	public void raiseEvent(GuiEvent event) {
		if (eventManager != null)
			eventManager.raiseEvent(event);
		if (!event.isCanceled())
			getParent().raiseEvent(event);
	}
	
	public void registerEventClick(Consumer<GuiControlClickEvent> consumer) {
		registerEvent(GuiControlClickEvent.class, consumer);
	}
	
	public void registerEventChanged(Consumer<GuiControlChangedEvent> consumer) {
		registerEvent(GuiControlChangedEvent.class, consumer);
	}
	
	public <T extends GuiEvent> void registerEvent(Class<T> clazz, Consumer<T> action) {
		if (eventManager == null)
			eventManager = new GuiEventManager();
		eventManager.registerEvent(clazz, action);
	}
}
