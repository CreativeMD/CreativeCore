package team.creative.creativecore.common.util.text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.text.content.ContentItemStack;

public class TextBuilder {
    
    private static NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    private final List<Component> lines = new ArrayList<>();
    
    public TextBuilder() {}
    
    public TextBuilder add(List<Component> components) {
        for (Component component : components)
            add(component);
        return this;
    }
    
    public TextBuilder add(Component component) {
        if (lines.isEmpty())
            lines.add(component);
        else
            lines.get(lines.size() - 1).getSiblings().add(component);
        return this;
    }
    
    public TextBuilder(String text) {
        text(text);
    }
    
    public TextBuilder translateIfCan(String text) {
        String translated = GuiControl.translate(text);
        if (!translated.equals(text))
            text(translated);
        return this;
    }
    
    public TextBuilder translate(String text) {
        text(GuiControl.translate(text));
        return this;
    }
    
    public TextBuilder translate(String text, Object... param) {
        text(GuiControl.translate(text, param));
        return this;
    }
    
    public TextBuilder text(String text) {
        if (text.contains("\n")) {
            String[] lines = text.split("\\n");
            add(Component.literal(lines[0]));
            for (int i = 1; i < lines.length; i++) {
                newLine();
                add(Component.literal(lines[i]));
            }
            return this;
        }
        add(Component.literal(text));
        return this;
    }
    
    public TextBuilder number(float number) {
        return number(number, false);
    }
    
    public TextBuilder number(float number, boolean rounded) {
        if (rounded)
            text("" + Math.round(number));
        else
            text("" + format.format(number));
        return this;
    }
    
    public TextBuilder number(double number) {
        return number(number, false);
    }
    
    public TextBuilder number(double number, boolean rounded) {
        if (rounded)
            text("" + Math.round(number));
        else
            text("" + format.format(number));
        return this;
    }
    
    public TextBuilder newLine() {
        lines.add(Component.empty());
        return this;
    }
    
    public TextBuilder color(int color) {
        int r = ColorUtils.red(color);
        int g = ColorUtils.green(color);
        int b = ColorUtils.blue(color);
        int a = ColorUtils.alpha(color);
        text("" + ChatFormatting.RED + r + " " + ChatFormatting.GREEN + g + " " + ChatFormatting.BLUE + b + (a < 255 ? " " + ChatFormatting.WHITE + a : ""));
        return this;
    }
    
    public TextBuilder stack(ItemStack stack) {
        add(MutableComponent.create(new ContentItemStack(stack)));
        return this;
    }
    
    public List<Component> build() {
        return lines;
    }
    
}
