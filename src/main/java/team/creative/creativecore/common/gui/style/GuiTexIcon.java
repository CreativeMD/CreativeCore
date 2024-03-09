package team.creative.creativecore.common.gui.style;

import net.minecraft.resources.ResourceLocation;

public record GuiTexIcon(ResourceLocation location, int minX, int minY, int width, int height) {
    
    public static final GuiTexIcon EMPTY = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 224, 224, 16, 16);
    public static final GuiTexIcon ARROW_RIGHT = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 0, 16, 16);
    public static final GuiTexIcon ARROW_UP = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 16, 16, 16);
    public static final GuiTexIcon ARROW_LEFT = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 32, 16, 16);
    public static final GuiTexIcon ARROW_DOWN = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 48, 16, 16);
    public static final GuiTexIcon MIRROR = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 64, 16, 16);
    public static final GuiTexIcon ARROW_OUT = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 80, 16, 16);
    public static final GuiTexIcon ARROW_IN = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 96, 16, 16);
    public static final GuiTexIcon COORDS = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 112, 16, 16);
    public static final GuiTexIcon HOUSE = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 128, 16, 16);
    public static final GuiTexIcon PAUSE = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 144, 16, 16);
    public static final GuiTexIcon PLAY = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 160, 16, 16);
    public static final GuiTexIcon STOP = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 176, 16, 16);
    public static final GuiTexIcon DUPLICATE = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 192, 16, 16);
    public static final GuiTexIcon MERGE = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 208, 16, 16);
    public static final GuiTexIcon MOVE = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 224, 16, 16);
    public static final GuiTexIcon CAMERA = new GuiTexIcon(GuiStyleUtils.GUI_ASSETS, 240, 240, 16, 16);
    
}
