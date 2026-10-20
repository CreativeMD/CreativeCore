package team.creative.creativecore.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.function.Function;

public class CreativeForgeClientLoader implements ICreativeClientLoader {
	@Override
	public KeyMapping.Category registerCategory(Identifier identifier) {
		var category = new KeyMapping.Category(identifier);
		ModLoadingContext.get().getActiveContainer().getEventBus().addListener((RegisterKeyMappingsEvent event) -> {
			event.registerCategory(category);
		});
		return category;
	}

	@Override
	public void registerKeybind(KeyMapping keyMapping) {
		ModLoadingContext.get().getActiveContainer().getEventBus().addListener((RegisterKeyMappingsEvent event) -> {
			event.register(keyMapping);
		});
	}

	@Override
	public void registerModifyTooltip(TooltipModifier modifier) {
		NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> {
			modifier.modifyTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip());
		});
	}

	@Override
	public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>, B extends M> void registerMenu(MenuType<B> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
		ModLoadingContext.get().getActiveContainer().getEventBus().addListener((RegisterMenuScreensEvent event) -> {
			event.register(menuType, screenConstructor);
		});
	}

	@Override
	public <T extends Screen, W extends AbstractWidget> void addScreenWidget(Function<Screen, T> cast, Function<T, W> factory) {
		NeoForge.EVENT_BUS.addListener((ScreenEvent.Init.Post event) -> {
			var screen = cast.apply(event.getScreen());
			if(screen != null) {
				event.addListener(factory.apply(screen));
			}
		});
	}
}
