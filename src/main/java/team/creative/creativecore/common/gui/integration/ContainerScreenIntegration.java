package team.creative.creativecore.common.gui.integration;

import java.lang.reflect.Field;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.util.math.Rect;

public class ContainerScreenIntegration extends ContainerScreen<ContainerIntegration> {
	
	private static final Field eventTime = ObfuscationReflectionHelper.findField(MouseHelper.class, "field_198045_j");
	public static final int DOUBLE_CLICK_TIME = 200;
	
	protected final ScreenEventListener listener;
	
	public ContainerScreenIntegration(ContainerIntegration screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		listener = this.addListener(new ScreenEventListener());
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		listener.tick();
		Rect screen = Rect.getScreenRect();
		
		List<GuiLayer> layers = getContainer().getLayers();
		
		for (int i = 0; i < layers.size(); i++) {
			GuiLayer layer = layers.get(i);
			
			if (layer.hasGrayBackground())
				this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
			
			if (i == layers.size() - 1)
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, matrixStack));
			
			matrixStack.push();
			
			matrixStack.translate(-guiLeft, -guiTop, 0);
			
			int offX = (this.width - layers.get(i).width) / 2;
			int offY = (this.height - layers.get(i).height) / 2;
			matrixStack.translate(offX, offY, 0);
			
			RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
			Rect controlRect = new Rect(offX, offY, offX + layer.width, offY + layer.height);
			layer.render(matrixStack, screen.intersection(controlRect), controlRect, mouseX - offX, mouseY - offY);
			
			matrixStack.pop();
		}
		
		if (layers.isEmpty())
			return;
		
		/*int i = layers.size() - 1;
		GlStateManager.pushMatrix();
		
		int k = guiLeft;
		int l = guiTop;
		int offX = (this.width - layers.get(i).width) / 2 - k;
		int offY = (this.height - layers.get(i).height) / 2 - l;
		matrixStack.translate(k, l, 0);
		
		GlStateManager.translatef(offX, offY, 0);
		
		Vec3d mouse = layers.get(i).getMousePos();
		GuiToolTipEvent event = layers.get(i).getToolTipEvent();
		if (event != null && layers.get(i).raiseEvent(event))
			this.drawHoveringText(event.tooltip, (int) mouse.x, (int) mouse.y, GuiRenderHelper.instance.font);
		
		GlStateManager.popMatrix();*/
	}
	
	public GuiLayer getTopLayer() {
		return getContainer().getTopLayer();
	}
	
	public int getMaxScale(int displayWidth, int displayHeight) {
		int scaleFactor = 1;
		while (scaleFactor < 100 && xSize * (scaleFactor + 1) <= displayWidth && ySize * (scaleFactor + 1) <= displayHeight)
			++scaleFactor;
		return scaleFactor;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {}
	
	public class ScreenEventListener implements IGuiEventListener {
		
		private int doubleClickButton = -1;
		private double time;
		private double x;
		private double y;
		private boolean released = false;
		
		public void tick() {
			if (doubleClickButton != -1 && getEventTime() - time > DOUBLE_CLICK_TIME)
				fireRemaingEvents();
		}
		
		public double getEventTime() {
			try {
				return eventTime.getDouble(getMinecraft().mouseHelper);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		protected void fireRemaingEvents() {
			if (doubleClickButton != -1) {
				getTopLayer().mouseClicked(x, y, doubleClickButton);
				if (released)
					getTopLayer().mouseReleased(x, y, doubleClickButton);
				doubleClickButton = -1;
				released = false;
			}
		}
		
		@Override
		public void mouseMoved(double x, double y) {
			getTopLayer().mouseMoved(x, y);
		}
		
		@Override
		public boolean mouseClicked(double x, double y, int button) {
			if (doubleClickButton == button) {
				released = false;
				doubleClickButton = -1;
				return getTopLayer().mouseDoubleClicked(x, y, button);
			}
			fireRemaingEvents();
			doubleClickButton = button;
			time = getEventTime();
			return true;
		}
		
		@Override
		public boolean mouseReleased(double x, double y, int button) {
			if (doubleClickButton == button) {
				released = true;
				return true;
			}
			fireRemaingEvents();
			return getTopLayer().mouseReleased(x, y, button);
		}
		
		@Override
		public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
			if (doubleClickButton == -1)
				return getTopLayer().mouseDragged(x, y, button, dragX, dragY, getEventTime());
			return true;
		}
		
		@Override
		public boolean mouseScrolled(double x, double y, double delta) {
			return getTopLayer().mouseScrolled(x, y, delta);
		}
		
		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			return getTopLayer().keyPressed(keyCode, scanCode, modifiers);
		}
		
		@Override
		public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
			return getTopLayer().keyReleased(keyCode, scanCode, modifiers);
		}
		
		@Override
		public boolean charTyped(char codePoint, int modifiers) {
			return getTopLayer().charTyped(codePoint, modifiers);
		}
		
		@Override
		public boolean changeFocus(boolean focus) {
			return false;
		}
	}
	
}
