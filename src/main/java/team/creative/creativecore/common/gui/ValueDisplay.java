package team.creative.creativecore.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public interface ValueDisplay {
    Component BLOCK_TEXT = new TranslatableComponent("minecraft.block");
    Component BLOCKS_TEXT = new TranslatableComponent("minecraft.block");
    ValueDisplay NONE = (v, max) -> (float) Math.round(v * 100.0) / 100 + "";
    ValueDisplay PERCENT = (v, max) -> (int) ((v / max) * 100.0d) + "%";
    ValueDisplay ANGLE = (v, max) -> Math.round(v) + "°";
    ValueDisplay BLOCKS = (v, max) -> Math.round(v) + " " + (v > 1 ? BLOCKS_TEXT.getString() : BLOCK_TEXT.getString());

    String get(double value, double max);
}