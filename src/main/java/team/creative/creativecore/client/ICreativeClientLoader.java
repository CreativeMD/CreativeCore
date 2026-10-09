package team.creative.creativecore.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

public interface ICreativeClientLoader {
	@FunctionalInterface
	public interface TooltipModifier {
		public void modifyTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> lines);
	}
	public void registerModifyTooltip(TooltipModifier modifier);

	public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>, B extends M> void registerMenu(MenuType<B> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor);
	public <T extends Screen, W extends AbstractWidget> void addScreenWidget(Function<Screen, T> cast, Function<@NonNull T, W> factory);

	public KeyMapping.Category registerCategory(Identifier identifier);

	public void registerKeybind(KeyMapping keyMapping);
}
