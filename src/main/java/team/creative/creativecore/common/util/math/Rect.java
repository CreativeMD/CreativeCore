package team.creative.creativecore.common.util.math;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Rect {
	
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	
	public Rect(int x, int y, int x2, int y2) {
		this.minX = x;
		this.minY = y;
		this.maxX = x2;
		this.maxY = y2;
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public static Rect getScreenRect() {
		Minecraft mc = Minecraft.getInstance();
		return new Rect(0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
	}
	
	public boolean intersects(Rect other) {
		return this.minX < other.maxX && this.maxX > other.minX && this.minY < other.maxY && this.maxY > other.minY;
	}
	
	public Rect intersection(Rect other) {
		if (intersects(other)) {
			int x = Math.max(this.minX, other.minX);
			int y = Math.max(this.minY, other.minY);
			int x2 = Math.min(this.maxX, other.maxX);
			int y2 = Math.min(this.maxY, other.maxY);
			return new Rect(x, y, x2, y2);
		}
		return null;
	}
	
	public void shrink(int shrink) {
		minX += shrink;
		minY += shrink;
		maxX -= shrink;
		maxY -= shrink;
	}
	
	public int getWidth() {
		return maxX - minX;
	}
	
	public int getHeight() {
		return maxY - minY;
	}
	
	public int getSize(Axis axis) {
		switch (axis) {
		case X:
			return getWidth();
		case Y:
			return getHeight();
		default:
			return 0;
		}
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public void scissor() {
		MainWindow window = Minecraft.getInstance().getMainWindow();
		int realMinX = (int) (minX * window.getGuiScaleFactor());
		int realMinY = window.getHeight() - (int) ((minY + getHeight()) * window.getGuiScaleFactor());
		int realMaxX = (int) (getWidth() * window.getGuiScaleFactor());
		int realMaxY = (int) (getHeight() * window.getGuiScaleFactor());
		
		RenderSystem.enableScissor(realMinX, realMinY, realMaxX, realMaxY);
	}
	
	public Rect copy() {
		return new Rect(minX, minY, maxX, maxY);
	}
	
	public Rect child(int x, int y, int width, int height) {
		return new Rect(minX + x, minY + y, minX + x + width, minY + y + height);
	}
	
	public boolean inside(int x, int y) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY;
	}
	
}
