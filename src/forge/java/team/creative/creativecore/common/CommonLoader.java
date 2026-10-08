package team.creative.creativecore.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface CommonLoader {
    
    void onInitialize();

    void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher);
}
