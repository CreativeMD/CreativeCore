package team.creative.creativecore.common.gui.style;

import net.minecraft.resources.ResourceLocation;

public record GuiIcon(ResourceLocation location, int minX, int minY, int width, int height) {
    
    public static final GuiIcon EMPTY = new GuiIcon(GuiStyle.GUI_ASSETS, 224, 224, 16, 16);
    public static final GuiIcon ARROW_RIGHT = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 0, 16, 16);
    public static final GuiIcon ARROW_UP = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 16, 16, 16);
    public static final GuiIcon ARROW_LEFT = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 32, 16, 16);
    public static final GuiIcon ARROW_DOWN = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 48, 16, 16);
    public static final GuiIcon MIRROR = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 64, 16, 16);
    public static final GuiIcon ARROW_OUT = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 80, 16, 16);
    public static final GuiIcon ARROW_IN = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 96, 16, 16);
    public static final GuiIcon COORDS = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 112, 16, 16);
    public static final GuiIcon HOUSE = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 128, 16, 16);
    public static final GuiIcon PAUSE = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 144, 16, 16);
    public static final GuiIcon PLAY = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 160, 16, 16);
    public static final GuiIcon STOP = new GuiIcon(GuiStyle.GUI_ASSETS, 240, 176, 16, 16);
    
}
