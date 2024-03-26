package team.creative.creativecore.common.gui.parser;

import net.minecraft.network.chat.TranslatableComponent;

@FunctionalInterface
public interface IntValueParser {
    IntValueParser NONE = (v, max) -> v + "";
    IntValueParser PIXELS = (v, max) -> v + "px";
    IntValueParser BLOCKS = (v, max) -> new TranslatableComponent("minecraft.blocks.counting", v).getString();

    String parse(int v, int max);
}
