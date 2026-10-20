package team.creative.creativecore.common;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandSourceStack;

public interface CommonLoader extends ModInitializer {
    
    @Override
    void onInitialize();
    
    default void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {}
}
