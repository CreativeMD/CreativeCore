package com.creativemd.creativecore.common.gui.mc;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenSub extends GuiScreen implements IVanillaGUI {
	
	public final GuiScreen parentScreen;
	protected ArrayList<SubGui> layers = new ArrayList<>();
	
	/** The X size of the inventory window in pixels. */
	protected int xSize = 176;
	/** The Y size of the inventory window in pixels. */
	protected int ySize = 166;
	
	/** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
	protected int guiLeft;
	/** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
	protected int guiTop;
	
	public GuiScreenSub(GuiScreen parentScreen, SubGui gui) {
		this.parentScreen = parentScreen;
		gui.container = null;
		gui.gui = this;
		this.layers.add(gui);
		
		resize();
	}
	
	public boolean isOpened = false;
	
	@Override
	public void initGui() {
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		if (!isOpened) {
			for (int i = 0; i < layers.size(); i++) {
				layers.get(i).onOpened();
			}
			isOpened = true;
		}
	}
	
	@Override
	public ArrayList<SubGui> getLayers() {
		return layers;
	}
	
	@Override
	public void removeLayer(SubGui layer) {
		layers.remove(layer);
		resize();
	}
	
	@Override
	public void addLayer(SubGui layer) {
		layers.add(layer);
		resize();
	}
	
	public void resize() {
		this.xSize = 0;
		this.ySize = 0;
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).width > this.xSize)
				this.xSize = layers.get(i).width;
			if (layers.get(i).height > this.ySize)
				this.ySize = layers.get(i).height;
		}
	}
	
	public int getWidth() {
		return xSize;
	}
	
	public int getHeight() {
		return ySize;
	}
	
	/** Returns the max avaible scale for this gui **/
	@Override
	public int getMaxScale(Minecraft mc) {
		int k = 1000;
		int scaleFactor = 1;
		
		while (scaleFactor < k && xSize * (scaleFactor + 1) <= mc.displayWidth && ySize * (scaleFactor + 1) <= mc.displayHeight) {
			++scaleFactor;
		}
		
		return scaleFactor;
	}
	
	@Override
	public boolean hasTopLayer() {
		return layers.size() > 0;
	}
	
	@Override
	public SubGui getTopLayer() {
		if (hasTopLayer())
			return layers.get(layers.size() - 1);
		return null;
	}
	
	@Override
	public boolean isTopLayer(SubGui gui) {
		return getTopLayer() == gui;
	}
	
	@Override
	public boolean isOpen(Class<? extends SubGui> clazz) {
		for (int i = 0; i < layers.size(); i++)
			if (clazz.isInstance(layers.get(i)))
				return true;
		return false;
	}
	
	@Override
	public <T extends SubGui> T get(Class<T> clazz) {
		for (int i = 0; i < layers.size(); i++)
			if (clazz.isInstance(layers.get(i)))
				return (T) layers.get(i);
		return null;
	}
	
	@Override
	public void drawDefaultBackground() {
		
	}
	
	public void onTick() {
		for (int i = 0; i < layers.size(); i++)
			layers.get(i).onTick();
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if (layers.isEmpty())
			return;
		
		int i = layers.size() - 1;
		GlStateManager.pushMatrix();
		
		int k = guiLeft;
		int l = guiTop;
		int offX = (this.width - layers.get(i).width) / 2 - k;
		int offY = (this.height - layers.get(i).height) / 2 - l;
		GlStateManager.translate(k, l, 0);
		
		drawGuiContainerForegroundLayer();
		
		GlStateManager.translate(offX, offY, 0);
		
		Vec3d mouse = layers.get(i).getMousePos();
		GuiToolTipEvent event = layers.get(i).getToolTipEvent();
		if (event != null && layers.get(i).raiseEvent(event))
			this.drawHoveringText(event.tooltip, (int) mouse.x, (int) mouse.y, GuiRenderHelper.instance.font);
		
		GlStateManager.popMatrix();
		
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	}
	
	public void drawGuiContainerForegroundLayer() {
		for (int i = 0; i < layers.size(); i++) {
			
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			GL11.glStencilMask(~0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			
			GL11.glStencilFunc(GL11.GL_ALWAYS, 0x1, 0x1);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
			
			GlStateManager.pushMatrix();
			
			int k = guiLeft;
			int l = guiTop;
			
			GlStateManager.translate(-k, -l, 0);
			
			if (layers.get(i).hasGrayBackground())
				this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
			
			if (i == layers.size() - 1)
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
			
			int offX = (this.width - layers.get(i).width) / 2 - k;
			int offY = (this.height - layers.get(i).height) / 2 - l;
			GlStateManager.translate(k, l, 0);
			
			GlStateManager.translate(offX, offY, 0);
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			
			layers.get(i).renderControl(GuiRenderHelper.instance, 1F, GuiControl.getScreenRect());
			
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	public void onGuiClosed() {
		for (int i = 0; i < layers.size(); i++)
			layers.get(i).onClosed();
	}
	
	@Override
	public void onLayerClosed() {
		if (layers.isEmpty())
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
	}
	
	@Override
	public void keyTyped(char character, int key) throws IOException {
		if (getTopLayer() != null && !getTopLayer().onKeyPressed(character, key))
			super.keyTyped(character, key);
	}
	
	@Override
	public void handleInput() throws IOException {
		if (Mouse.isCreated()) {
			handleScrolling();
			Vec3d mouse = getTopLayer().getMousePos();
			getTopLayer().mouseMove((int) mouse.x, (int) mouse.y, 0);
		}
		super.handleInput();
	}
	
	public void handleScrolling() {
		int j = Mouse.getDWheel();
		if (j != 0) {
			Vec3d mouse = getTopLayer().getMousePos();
			getTopLayer().mouseScrolled((int) mouse.x, (int) mouse.y, j > 0 ? 1 : -1);
		}
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		getTopLayer().mousePressed(x, y, button);
	}
	
	@Override
	public void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		getTopLayer().mouseDragged(x, y, button, time);
	}
	
	@Override
	protected void mouseReleased(int x, int y, int button) {
		super.mouseReleased(x, y, button);
		onMouseReleased(x, y, button);
	}
	
	public void onMouseReleased(int x, int y, int button) {
		getTopLayer().mouseReleased(x, y, button);
	}
	
	@Override
	public void sendChat(String msg) {
		this.sendChatMessage(msg);
	}
	
	@Override
	public int getGuiLeft() {
		return guiLeft;
	}
	
	@Override
	public int getGuiTop() {
		return guiTop;
	}
}
