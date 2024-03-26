package team.creative.creativecore.common.gui.style;

import net.minecraft.resources.ResourceLocation;

public record Icon(ResourceLocation location, int minX, int minY, int width, int height) {
    
    public static final Icon EMPTY = new Icon(GuiStyleUtils.GUI_ASSETS, 224, 224, 16, 16);
    public static final Icon ARROW_RIGHT = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 0, 16, 16);
    public static final Icon ARROW_UP = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 16, 16, 16);
    public static final Icon ARROW_LEFT = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 32, 16, 16);
    public static final Icon ARROW_DOWN = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 48, 16, 16);
    public static final Icon MIRROR = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 64, 16, 16);
    public static final Icon ARROW_OUT = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 80, 16, 16);
    public static final Icon ARROW_IN = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 96, 16, 16);
    public static final Icon COORDS = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 112, 16, 16);
    public static final Icon HOUSE = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 128, 16, 16);
    public static final Icon PAUSE = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 144, 16, 16);
    public static final Icon PLAY = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 160, 16, 16);
    public static final Icon STOP = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 176, 16, 16);
    public static final Icon DUPLICATE = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 192, 16, 16);
    public static final Icon MERGE = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 208, 16, 16);
    public static final Icon MOVE = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 224, 16, 16);
    public static final Icon CAMERA = new Icon(GuiStyleUtils.GUI_ASSETS, 240, 240, 16, 16);
    
}
