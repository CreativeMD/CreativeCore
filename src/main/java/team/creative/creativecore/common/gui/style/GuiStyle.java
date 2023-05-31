package team.creative.creativecore.common.gui.style;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.DisplayTexture;
import team.creative.creativecore.common.gui.style.display.DisplayTextureRepeat;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;
import team.creative.creativecore.common.util.type.Color;

public class GuiStyle {
    
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Gson GSON = new GsonBuilder().create();
    public static final NamedHandlerRegistry<GuiStyle> REGISTRY = new NamedHandlerRegistry<GuiStyle>(null);
    
    public static void reload() {
        try {
            Resource resource = mc.getResourceManager().getResource(GuiStyleUtils.DEFAULT_STYLE_LOCATION).orElseThrow();
            InputStream input = resource.open();
            try {
                JsonObject root = JsonParser.parseString(IOUtils.toString(input, Charsets.UTF_8)).getAsJsonObject();
                
                NamedHandlerRegistry.clearRegistry(REGISTRY);
                REGISTRY.registerDefault("default", GSON.fromJson(root, GuiStyle.class));
            } finally {
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CreativeCore.LOGGER.error("Could not load default style");
            REGISTRY.registerDefault("default", new GuiStyle());
        }
    }
    
    public static GuiStyle getStyle(String name) {
        GuiStyle cached = REGISTRY.get(name);
        if (cached != null)
            return cached;
        
        try {
            Resource resource = mc.getResourceManager().getResource(new ResourceLocation(name)).orElseThrow();
            InputStream input = resource.open();
            try {
                JsonObject root = JsonParser.parseString(IOUtils.toString(input, Charsets.UTF_8)).getAsJsonObject();
                
                cached = GSON.fromJson(root, GuiStyle.class);
                REGISTRY.register(name, cached);
                return cached;
            } finally {
                input.close();
            }
        } catch (FileNotFoundException | NoSuchElementException e) {
            REGISTRY.register(name, REGISTRY.getDefault());
            return REGISTRY.getDefault();
        } catch (Exception e) {
            e.printStackTrace();
            CreativeCore.LOGGER.error("Found invalid style " + name);
            return REGISTRY.getDefault();
        }
    }
    
    @SerializedName("font-color")
    public Color fontColor = new Color(255, 255, 255);
    @SerializedName("font-color-highlight")
    public Color fontColorHighlight = new Color(255, 255, 100);
    @SerializedName("font-color-disabled")
    public Color fontColorDisabled = new Color(100, 100, 100);
    
    @SerializedName("border-width")
    public int borderWidth = 1;
    @SerializedName("border-thick-width")
    public int borderThickWidth = 2;
    
    public StyleDisplay disabled = new DisplayColor(0, 0, 0, 0.4F);
    
    public StyleDisplay border = new DisplayColor(0, 0, 0, 1);
    @SerializedName("border-thick")
    public StyleDisplay borderThick = new DisplayColor(0, 0, 0, 1);
    
    public StyleDisplay background = new DisplayColor(0.6F, 0.6F, 0.6F, 1);
    @SerializedName("secondary-background")
    public StyleDisplay secondaryBackground = new DisplayColor(0.5F, 0.5F, 0.5F, 1);
    @SerializedName("header-background")
    public StyleDisplay headerBackground = new DisplayColor(1F, 1F, 1F, 1);
    
    public StyleDisplay bar = new DisplayColor(0.1F, 0.1F, 0.1F, 1);
    
    public StyleDisplay clickable = new DisplayColor(0.4F, 0.4F, 0.4F, 1);
    @SerializedName("clickable-highlight")
    public StyleDisplay clickableHighlight = new DisplayColor(0.5F, 0.5F, 0.5F, 1);
    
    @SerializedName("clickable-inactive")
    public StyleDisplay clickableInactive = new DisplayColor(0.25F, 0.25F, 0.25F, 1);
    @SerializedName("clickable-inactive-highlight")
    public StyleDisplay clickableInactiveHighlight = new DisplayColor(0.3F, 0.3F, 0.3F, 1);
    
    public StyleDisplay disabledBackground = new DisplayColor(0.1F, 0.1F, 0.1F, 1);
    
    public StyleDisplay slot = new DisplayTexture(GuiStyleUtils.GUI_ASSETS, 0, 0);
    @SerializedName("transparency-background")
    public StyleDisplay transparencyBackground = new DisplayTextureRepeat(GuiStyleUtils.GUI_ASSETS, 224, 240, 16, 16);
    
    public StyleDisplay get(ControlStyleBorder border) {
        return switch (border) {
            case BIG -> this.borderThick;
            case SMALL -> this.border;
            default -> StyleDisplay.NONE;
        };
    }
    
    public StyleDisplay get(ControlStyleFace face, boolean mouseOver) {
        return switch (face) {
            case BACKGROUND -> background;
            case BAR -> bar;
            case CLICKABLE -> mouseOver ? clickableHighlight : clickable;
            case CLICKABLE_INACTIVE -> mouseOver ? clickableInactiveHighlight : clickableInactive;
            case NESTED_BACKGROUND -> secondaryBackground;
            case SLOT -> slot;
            case DISABLED -> disabledBackground;
            case HEADER_BACKGROUND -> headerBackground;
            default -> StyleDisplay.NONE;
        };
    }
    
    public int getBorder(ControlStyleBorder border) {
        return switch (border) {
            case BIG -> this.borderThickWidth;
            case SMALL -> this.borderWidth;
            default -> 0;
        };
    }
    
    public int getContentOffset(ControlFormatting formatting) {
        return getBorder(formatting.border) + formatting.padding;
    }
    
}
