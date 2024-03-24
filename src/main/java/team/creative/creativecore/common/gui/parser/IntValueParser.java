package team.creative.creativecore.common.gui.parser;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface IntValueParser {
    IntValueParser NONE = (v, max) -> v + "";
    IntValueParser PIXELS = (v, max) -> v + "px";
    IntValueParser BLOCKS = (v, max) -> Component.translatable("minecraft.blocks.counting", v).getString();

    String parse(int v, int max);
}
