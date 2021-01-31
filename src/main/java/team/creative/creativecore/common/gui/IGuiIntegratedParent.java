package team.creative.creativecore.common.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.integration.ScreenEventListener;
import team.creative.creativecore.common.util.math.Rect;

public interface IGuiIntegratedParent extends IGuiParent {
	
	public List<GuiLayer> getLayers();
	
	public GuiLayer getTopLayer();
	
	@OnlyIn(value = Dist.CLIENT)
	public default void render(MatrixStack matrixStack, Screen screen, ScreenEventListener listener, int mouseX, int mouseY) {
		int width = screen.width;
		int height = screen.height;
		
		listener.tick();
		Rect screenRect = Rect.getScreenRect();
		
		List<GuiLayer> layers = getLayers();
		
		for (int i = 0; i < layers.size(); i++) {
			GuiLayer layer = layers.get(i);
			
			if (layer.hasGrayBackground())
				GuiRenderHelper.fillGradient(matrixStack, 0, 0, width, height, -1072689136, -804253680);
			
			if (i == layers.size() - 1)
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(screen, matrixStack));
			
			matrixStack.push();
			int offX = (width - layer.width) / 2;
			int offY = (height - layer.height) / 2;
			matrixStack.translate(offX, offY, 0);
			
			RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
			Rect controlRect = new Rect(offX, offY, offX + layer.width, offY + layer.height);
			layer.render(matrixStack, screenRect.intersection(controlRect), controlRect, mouseX - offX, mouseY - offY);
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
	
	@Override
	public default void moveBehind(GuiControl toMove, GuiControl reference) {
		List<GuiLayer> layers = getLayers();
		layers.remove(toMove);
		int index = layers.indexOf(reference);
		if (index != -1 && index < layers.size() - 1)
			layers.add(index + 1, (GuiLayer) toMove);
		else
			moveBottom(toMove);
	}
	
	@Override
	public default void moveInFront(GuiControl toMove, GuiControl reference) {
		List<GuiLayer> layers = getLayers();
		layers.remove(toMove);
		int index = layers.indexOf(reference);
		if (index != -1)
			layers.add(index, (GuiLayer) toMove);
		else
			moveTop(toMove);
	}
	
	@Override
	public default void moveTop(GuiControl toMove) {
		List<GuiLayer> layers = getLayers();
		layers.remove(toMove);
		layers.add(0, (GuiLayer) toMove);
	}
	
	@Override
	public default void moveBottom(GuiControl toMove) {
		List<GuiLayer> layers = getLayers();
		layers.remove(toMove);
		layers.add((GuiLayer) toMove);
	}
	
}
