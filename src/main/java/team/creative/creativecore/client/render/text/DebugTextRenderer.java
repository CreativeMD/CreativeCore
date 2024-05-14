package team.creative.creativecore.client.render.text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Environment(EnvType.CLIENT)
public class DebugTextRenderer {
    
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    
    private static String format(Object value) {
        if (value instanceof Double || value instanceof Float)
            return DECIMAL_FORMAT.format(value);
        return value.toString();
    }
    
    protected final List<String> lines = new ArrayList<>();
    
    public DebugTextRenderer() {
        lines.add("");
    }
    
    public DebugTextRenderer text(String text) {
        lines.set(lines.size() - 1, lines.get(lines.size() - 1) + text);
        return this;
    }
    
    public DebugTextRenderer detail(String name, Object value) {
        StringBuilder lastLine = new StringBuilder(lines.get(lines.size() - 1));
        if (!lastLine.isEmpty())
            lastLine.append(",");
        if (name.isEmpty())
            lastLine.append(format(value));
        else
            lastLine.append(ChatFormatting.YELLOW + name + ChatFormatting.RESET + ":" + format(value));
        lines.set(lines.size() - 1, lastLine.toString());
        return this;
    }
    
    public DebugTextRenderer newLine() {
        lines.add("");
        return this;
    }
    
    public void render(Font font, GuiGraphics graphics) {
        RenderSystem.defaultBlendFunc();
        int top = 2;
        for (String msg : lines) {
            if (msg != null && !msg.isEmpty()) {
                graphics.fill(1, top - 1, 2 + font.width(msg) + 1, top + font.lineHeight - 1, -1873784752);
                graphics.drawString(font, msg, 2, top, 14737632, false);
            }
            top += font.lineHeight;
        }
    }
}
