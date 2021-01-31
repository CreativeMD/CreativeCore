package team.creative.creativecore.common.gui.integration;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;

public class GuiScreenIntegration extends Screen implements IGuiIntegratedParent, IScaleableGuiScreen {
	
	public final Minecraft mc = Minecraft.getInstance();
	private List<GuiLayer> layers = new ArrayList<>();
	protected final ScreenEventListener listener;
	
	protected GuiScreenIntegration(ITextComponent titleIn) {
		super(titleIn);
		listener = this.addListener(new ScreenEventListener(this));
	}
	
	@Override
	public int getWidth() {
		int width = 0;
		for (GuiLayer layer : layers)
			width = Math.max(width, layer.width);
		return width;
	}
	
	@Override
	public int getHeight() {
		int height = 0;
		for (GuiLayer layer : layers)
			height = Math.max(height, layer.height);
		return height;
	}
	
	@Override
	protected void init() {
		for (GuiLayer layer : layers)
			layer.init();
	}
	
	@Override
	public void tick() {
		for (GuiLayer layer : layers)
			layer.tick();
	}
	
	@Override
	public void onClose() {
		for (GuiLayer layer : layers)
			layer.closed();
	}
	
	@Override
	public boolean isContainer() {
		return false;
	}
	
	@Override
	public boolean isClient() {
		return true;
	}
	
	@Override
	public PlayerEntity getPlayer() {
		return mc.player;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		render(matrixStack, this, listener, mouseX, mouseY);
	}
	
	@Override
	public List<GuiLayer> getLayers() {
		return layers;
	}
	
	@Override
	public GuiLayer getTopLayer() {
		return layers.get(layers.size() - 1);
	}
	
	@Override
	public void closeLayer(GuiLayer layer) {
		layers.remove(layer);
		if (layers.isEmpty())
			closeScreen();
	}
	
}
