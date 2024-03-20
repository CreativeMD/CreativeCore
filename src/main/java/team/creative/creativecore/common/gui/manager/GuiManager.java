package team.creative.creativecore.common.gui.manager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

import java.util.function.Function;

public abstract class GuiManager {
    
    public static final NamedHandlerRegistry<GuiManagerType> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static <T extends GuiManager> GuiManagerType<T> register(String name, Class<T> managerClass, Function<GuiLayer, T> factory) {
        GuiManagerType<T> type = new GuiManagerType<T>(name, managerClass, factory);
        REGISTRY.register(name, type);
        return type;
    }
    
    public static final GuiManagerType<GuiManagerItem> ITEM = register("item", GuiManagerItem.class, GuiManagerItem::new);
    
    public static record GuiManagerType<T extends GuiManager>(String name, Class<T> managerClass, Function<GuiLayer, T> factory) {}
    
    public final GuiLayer layer;
    
    public GuiManager(GuiLayer layer) {
        this.layer = layer;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void renderOverlay(PoseStack graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {}
    
    public void mouseReleased(double x, double y, int button) {}
    
    public void mouseClickedOutside(double x, double y) {}
    
    public void tick() {}
    
    public void closed() {}
    
}
