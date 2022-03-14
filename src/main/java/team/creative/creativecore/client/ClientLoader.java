package team.creative.creativecore.client;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public interface ClientLoader {
    
    public void onInitializeClient();
    
    public default void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher) {}
    
}
