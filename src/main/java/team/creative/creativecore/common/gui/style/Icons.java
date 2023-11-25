package team.creative.creativecore.common.gui.style;

import net.minecraft.resources.ResourceLocation;

public record Icons(ResourceLocation location, int minX, int minY, int width, int height) {
    
    public static final Icons EMPTY = new Icons(GuiStyleUtils.GUI_ASSETS, 224, 224, 16, 16);
    public static final Icons ARROW_RIGHT = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 0, 16, 16);
    public static final Icons ARROW_UP = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 16, 16, 16);
    public static final Icons ARROW_LEFT = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 32, 16, 16);
    public static final Icons ARROW_DOWN = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 48, 16, 16);
    public static final Icons MIRROR = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 64, 16, 16);
    public static final Icons ARROW_OUT = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 80, 16, 16);
    public static final Icons ARROW_IN = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 96, 16, 16);
    public static final Icons COORDS = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 112, 16, 16);
    public static final Icons HOUSE = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 128, 16, 16);
    public static final Icons PAUSE = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 144, 16, 16);
    public static final Icons PLAY = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 160, 16, 16);
    public static final Icons STOP = new Icons(GuiStyleUtils.GUI_ASSETS, 240, 176, 16, 16);
    
}