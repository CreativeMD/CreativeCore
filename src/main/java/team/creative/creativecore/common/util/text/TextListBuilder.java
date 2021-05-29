package team.creative.creativecore.common.util.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.GuiControl;

public class TextListBuilder implements ITextCollection {
    
    private List<List<ITextComponent>> list = new ArrayList<>();
    
    public TextListBuilder() {
        
    }
    
    private void addNewLine(String line) {
        addNewLine(new StringTextComponent(line));
    }
    
    private void addNewLine(ITextComponent line) {
        List<ITextComponent> newLine = new ArrayList<>();
        newLine.add(line);
        list.add(newLine);
    }
    
    private void addNewLine(List<ITextComponent> line) {
        list.add(line);
    }
    
    public TextListBuilder add(String... array) {
        for (int i = 0; i < array.length; i++)
            addNewLine(array[i]);
        return this;
    }
    
    public TextListBuilder add(List<String> list) {
        for (int i = 0; i < list.size(); i++)
            addNewLine(list.get(i));
        return this;
    }
    
    public <T> TextListBuilder add(T[] array, Function<T, String> toString) {
        for (int i = 0; i < array.length; i++)
            addNewLine(toString.apply(array[i]));
        return this;
    }
    
    public <T> TextListBuilder add(Collection<T> collection, Function<T, String> toString) {
        for (T t : collection)
            addNewLine(toString.apply(t));
        return this;
    }
    
    public <T> TextListBuilder addComponent(T[] array, Function<T, ITextComponent> toComponent) {
        for (int i = 0; i < array.length; i++)
            addNewLine(toComponent.apply(array[i]));
        return this;
    }
    
    public <T> TextListBuilder addComponent(Collection<T> collection, Function<T, ITextComponent> toComponent) {
        for (T t : collection)
            addNewLine(toComponent.apply(t));
        return this;
    }
    
    public <T> TextListBuilder addComponents(Collection<T> collection, Function<T, List<ITextComponent>> toComponent) {
        for (T t : collection)
            addNewLine(toComponent.apply(t));
        return this;
    }
    
    public TextListBuilder addTranslated(String prefix, String[] array) {
        for (int i = 0; i < array.length; i++)
            addNewLine(GuiControl.translateOrDefault(prefix + array[i], array[i]));
        return this;
    }
    
    public TextListBuilder addTranslated(String prefix, List<String> list) {
        for (int i = 0; i < list.size(); i++)
            addNewLine(GuiControl.translateOrDefault(prefix + list.get(i), list.get(i)));
        return this;
    }
    
    public <T> TextListBuilder addTranslated(String prefix, T[] array, Function<T, String> toString) {
        for (int i = 0; i < array.length; i++) {
            String text = toString.apply(array[i]);
            addNewLine(GuiControl.translateOrDefault(prefix + text, text));
        }
        return this;
    }
    
    public <T> TextListBuilder addTranslated(String prefix, Collection<T> collection, Function<T, String> toString) {
        for (T t : collection) {
            String text = toString.apply(t);
            addNewLine(GuiControl.translateOrDefault(prefix + text, text));
        }
        return this;
    }
    
    public int size() {
        return list.size();
    }
    
    public List<ITextComponent> get(int index) {
        return list.get(index);
    }
    
    @Override
    public CompiledText[] build() {
        CompiledText[] lines = new CompiledText[size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = CompiledText.createAnySize();
            lines[i].setText(get(i));
        }
        return lines;
    }
    
}
