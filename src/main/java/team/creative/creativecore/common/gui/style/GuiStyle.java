package team.creative.creativecore.common.gui.style;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
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
import team.creative.creativecore.common.util.type.Color;

public class GuiStyle {
    
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Gson GSON = new GsonBuilder().create();
    public static GuiStyle defaultStyle;
    private static HashMap<String, GuiStyle> cachedStyles = new HashMap<>();
    
    public static void reload() {
        try {
            Resource resource = mc.getResourceManager().getResource(GuiStyleUtils.DEFAULT_STYLE_LOCATION).orElseThrow();
            InputStream input = resource.open();
            try {
                JsonObject root = JsonParser.parseString(IOUtils.toString(input, Charsets.UTF_8)).getAsJsonObject();
                
                defaultStyle = GSON.fromJson(root, GuiStyle.class);
                
                cachedStyles.clear();
            } finally {
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CreativeCore.LOGGER.error("Could not load default style");
            defaultStyle = new GuiStyle();
        }
    }
    
    public static GuiStyle getStyle(String name) {
        GuiStyle cached = cachedStyles.get(name);
        if (cached != null)
            return cached;
        
        try {
            Resource resource = mc.getResourceManager().getResource(new ResourceLocation(name)).orElseThrow();
            InputStream input = resource.open();
            try {
                JsonObject root = JsonParser.parseString(IOUtils.toString(input, Charsets.UTF_8)).getAsJsonObject();
                
                cached = GSON.fromJson(root, GuiStyle.class);
                cachedStyles.put(name, cached);
                return cached;
            } finally {
                input.close();
            }
        } catch (FileNotFoundException | NoSuchElementException e) {
            cachedStyles.put(name, defaultStyle);
            return defaultStyle;
        } catch (Exception e) {
            e.printStackTrace();
            CreativeCore.LOGGER.error("Found invalid style " + name);
            return defaultStyle;
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
    
    public StyleDisplay bar = new DisplayColor(0.1F, 0.1F, 0.1F, 1);
    
    public StyleDisplay clickable = new DisplayColor(0.4F, 0.4F, 0.4F, 1);
    @SerializedName("clickable-highlight")
    public StyleDisplay clickableHighlight = new DisplayColor(0.5F, 0.5F, 0.5F, 1);
    
    public StyleDisplay disabledBackground = new DisplayColor(0.1F, 0.1F, 0.1F, 1);
    
    public StyleDisplay slot = new DisplayTexture(GuiStyleUtils.GUI_ASSETS, 0, 0);
    @SerializedName("transparency-background")
    public StyleDisplay transparencyBackground = new DisplayTextureRepeat(GuiStyleUtils.GUI_ASSETS, 224, 240, 16, 16);
    
    public StyleDisplay get(ControlStyleBorder border) {
        switch (border) {
            case BIG:
                return this.borderThick;
            case SMALL:
                return this.border;
            default:
                return StyleDisplay.NONE;
        }
    }
    
    public StyleDisplay get(ControlStyleFace face, boolean mouseOver) {
        switch (face) {
            case BACKGROUND:
                return background;
            case BAR:
                return bar;
            case CLICKABLE:
                if (mouseOver)
                    return clickableHighlight;
                return clickable;
            case NESTED_BACKGROUND:
                return secondaryBackground;
            case SLOT:
                return slot;
            case DISABLED:
                return disabledBackground;
            default:
                return StyleDisplay.NONE;
        }
    }
    
    public int getBorder(ControlStyleBorder border) {
        switch (border) {
            case BIG:
                return this.borderThickWidth;
            case SMALL:
                return this.borderWidth;
            default:
                return 0;
        }
    }
    
    public int getContentOffset(ControlFormatting formatting) {
        return getBorder(formatting.border) + formatting.padding;
    }
}
