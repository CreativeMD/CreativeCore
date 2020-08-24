package team.creative.creativecore.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import team.creative.creativecore.client.command.ClientCommandRegistry;

public class CreativeCoreClient {
	
	private static Minecraft mc = Minecraft.getInstance();
	
	public static void init(FMLClientSetupEvent event) {
		ClientCommandRegistry.register((LiteralArgumentBuilder<ISuggestionProvider>) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("test-client")).executes((x) -> {
			mc.player.getCommandSource().sendFeedback(new StringTextComponent("Successful!"), false);
			return 1;
		}));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void chat(ClientChatEvent event) {
		String message = event.getMessage();
		if (message.startsWith("/") && ClientCommandRegistry.handleCommand(mc.player.getCommandSource(), message) != -1) {
			event.setCanceled(true);
			mc.ingameGUI.getChatGUI().addToSentMessages(message);
		}
	}
}
