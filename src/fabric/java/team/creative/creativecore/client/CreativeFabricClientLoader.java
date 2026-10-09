package team.creative.creativecore.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Function;

public class CreativeFabricClientLoader implements ICreativeClientLoader {
	@Override
	public void registerKeybind(KeyMapping key) {
		KeyMappingHelper.registerKeyMapping(key);
	}

	@Override
	public KeyMapping.Category registerCategory(Identifier identifier) {
		return KeyMapping.Category.register(identifier);
	}

	@Override
	public void registerModifyTooltip(TooltipModifier modifier) {
		ItemTooltipCallback.EVENT.register(modifier::modifyTooltip);
	}

	@Override
	public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>, B extends M> void registerMenu(MenuType<B> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
		MenuScreens.register(menuType, screenConstructor);
	}

	@Override
	public <T extends Screen, W extends AbstractWidget> void addScreenWidget(Function<Screen, T> cast, Function<T, W> factory) {
		ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
			T castResult = cast.apply(screen);
			if(castResult != null) {
				Screens.getWidgets(screen).add(factory.apply(castResult));
			}
		}));
	}
}
