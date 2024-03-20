package team.creative.creativecore.client;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ClientModInitializer;

public interface ClientLoader extends ClientModInitializer {
    
    @Override
    void onInitializeClient();
    
    default <T> void registerClientCommands(CommandDispatcher<T> dispatcher) {}
}
