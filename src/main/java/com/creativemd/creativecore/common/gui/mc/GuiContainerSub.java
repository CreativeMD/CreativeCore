package com.creativemd.creativecore.common.gui.mc;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerSub extends GuiContainer {
	
	protected ArrayList<SubGui> layers;
	
	public GuiContainerSub(EntityPlayer player, SubGui gui, SubContainer container) {
		super(new ContainerSub(player, container));
		((ContainerSub) inventorySlots).gui = this;
		Minecraft.getMinecraft().player.openContainer = this.inventorySlots;
		
		layers = new ArrayList<SubGui>();
		gui.container = container;
		gui.gui = this;
		container.addListener(gui);
		this.layers.add(gui);
		
		container.onOpened();
		// gui.onOpened();
		
		resize();
		
		// SubGui.itemRender = GuiScreen.itemRender;
	}
	
	public boolean isOpened = false;
	
	@Override
	public void initGui() {
		super.initGui();
		if (!isOpened) {
			for (int i = 0; i < layers.size(); i++) {
				layers.get(i).onOpened();
			}
			isOpened = true;
		}
	}
	
	public ArrayList<SubGui> getLayers() {
		return layers;
	}
	
	public void removeLayer(SubGui layer) {
		layers.remove(layer);
		((ContainerSub) inventorySlots).layers.remove(layer.container);
		resize();
	}
	
	public void addLayer(SubGui layer) {
		layers.add(layer);
		((ContainerSub) inventorySlots).layers.add(layer.container);
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
	public int getMaxScale(Minecraft mc) {
		int k = 1000;
		int scaleFactor = 1;
		
		while (scaleFactor < k && xSize * (scaleFactor + 1) <= mc.displayWidth && ySize * (scaleFactor + 1) <= mc.displayHeight) {
			++scaleFactor;
		}
		
		/* if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
		 * --this.scaleFactor; } */
		return scaleFactor;
	}
	
	public boolean hasTopLayer() {
		return layers.size() > 0;
	}
	
	public SubGui getTopLayer() {
		if (hasTopLayer())
			return layers.get(layers.size() - 1);
		return null;
	}
	
	public boolean isTopLayer(SubGui gui) {
		return getTopLayer() == gui;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
	}
	
	@Override
	public void drawDefaultBackground() {
		
	}
	
	public void onTick() {
		
		for (int i = 0; i < layers.size(); i++)
			layers.get(i).onTick();
		
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
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
			
			int offX = (this.width - layers.get(i).width) / 2 - k;
			int offY = (this.height - layers.get(i).height) / 2 - l;
			GlStateManager.translate(k, l, 0);
			
			GlStateManager.translate(offX, offY, 0);
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			
			layers.get(i).renderControl(GuiRenderHelper.instance, 1F, GuiControl.getScreenRect());
			
			GlStateManager.popMatrix();
			
			Vec3d mouse = layers.get(i).getMousePos();
			GuiToolTipEvent event = layers.get(i).getToolTipEvent();
			if (event != null && layers.get(i).raiseEvent(event))
				this.drawHoveringText(event.tooltip, (int) mouse.x, (int) mouse.y, GuiRenderHelper.instance.font);
		}
	}
	
	@Override
	public void onGuiClosed() {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).onClosed();
		}
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
			// Mouse.setGrabbed(true);
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
		// if(button > 0)
		// onMouseMove(x, y, button);
		// else
		onMouseReleased(x, y, button);
	}
	
	public void onMouseReleased(int x, int y, int button) {
		getTopLayer().mouseReleased(x, y, button);
	}
}
