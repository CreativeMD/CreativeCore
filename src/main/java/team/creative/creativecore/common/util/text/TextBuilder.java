package team.creative.creativecore.common.util.text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class TextBuilder {
    
    private static final NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    private final List<Component> components = new ArrayList<>();
    
    public TextBuilder() {

    }
    
    public TextBuilder add(List<Component> components) {
        for (Component component : components)
            add(component);
        return this;
    }
    
    public TextBuilder add(Component component) {
        if (components.isEmpty())
            components.add(component);
        else {
            Component last = components.get(components.size() - 1);
            if (last instanceof MutableComponent)
                ((MutableComponent) last).append(component);
            else
                components.add(component);
        }
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
            add(new TextComponent(lines[0]));
            for (int i = 1; i < lines.length; i++) {
                newLine();
                add(new TextComponent(lines[i]));
            }
            return this;
        }
        add(new TextComponent(text));
        return this;
    }
    
    public TextBuilder number(float number) {
        return number(number, false);
    }
    
    public TextBuilder number(float number, boolean rounded) {
        if (rounded)
            text("" + Math.round(number));
        else
            text(format.format(number));
        return this;
    }
    
    public TextBuilder number(double number) {
        return number(number, false);
    }
    
    public TextBuilder number(double number, boolean rounded) {
        if (rounded)
            text("" + Math.round(number));
        else
            text(format.format(number));
        return this;
    }
    
    public TextBuilder newLine() {
        components.add(new LinebreakComponent());
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
        add(new ItemStackComponent(stack));
        return this;
    }
    
    public List<Component> build() {
        return components;
    }
    
}
