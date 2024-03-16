package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiStateButton;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.player.PlayerSelector;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorAnd;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorCommandSelector;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorGamemode;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorLevel;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorNot;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorOr;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
public abstract class GuiPlayerSelectorHandler<T extends PlayerSelector> {
    
    public static final NamedHandlerRegistry<GuiPlayerSelectorHandler> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static GuiPlayerSelectorHandler get(PlayerSelector selector) {
        return REGISTRY.get(PlayerSelector.REGISTRY.getId(selector));
    }
    
    static {
        REGISTRY.register("and", new GuiPlayerSelectorHandlerMultiple<PlayerSelectorAnd>() {
            
            @Override
            public PlayerSelectorAnd parseSelector(PlayerSelector[] selectors) {
                return new PlayerSelectorAnd(selectors);
            }
        });
        REGISTRY.register("or", new GuiPlayerSelectorHandlerMultiple<PlayerSelectorOr>() {
            
            @Override
            public PlayerSelectorOr parseSelector(PlayerSelector[] selectors) {
                return new PlayerSelectorOr(selectors);
            }
        });
        REGISTRY.register("not", new GuiPlayerSelectorHandler<PlayerSelectorNot>() {
            
            @Override
            public void createControls(PlayerSelectorDialog gui, PlayerSelector selector) {
                gui.add(new GuiPlayerSelectorButton("not", selector instanceof PlayerSelectorNot ? ((PlayerSelectorNot) selector).selector : new PlayerSelectorLevel(0)));
            }
            
            @Override
            public PlayerSelectorNot parseSelector(PlayerSelectorDialog gui) {
                GuiPlayerSelectorButton button = gui.get("not");
                PlayerSelector selector = button.get();
                if (selector != null)
                    return new PlayerSelectorNot(selector);
                return null;
            }
        });
        REGISTRY.register("level", new GuiPlayerSelectorHandler<PlayerSelectorLevel>() {
            
            @Override
            public void createControls(PlayerSelectorDialog gui, PlayerSelector selector) {
                gui.add(new GuiTextfield("content", selector instanceof PlayerSelectorLevel ? "" + ((PlayerSelectorLevel) selector).permissionLevel : "0").setNumbersOnly());
            }
            
            @Override
            public PlayerSelectorLevel parseSelector(PlayerSelectorDialog gui) {
                GuiTextfield text = gui.get("content");
                return new PlayerSelectorLevel(text.parseInteger());
            }
            
        });
        REGISTRY.register("mode", new GuiPlayerSelectorHandler<PlayerSelectorGamemode>() {
            
            @Override
            public void createControls(PlayerSelectorDialog gui, PlayerSelector selector) {
                gui.add(new GuiStateButton("mode", selector instanceof PlayerSelectorGamemode ? ((PlayerSelectorGamemode) selector).type
                        .getId() : 0, "survival", "creative", "adventure", "spectator"));
            }
            
            @Override
            public PlayerSelectorGamemode parseSelector(PlayerSelectorDialog gui) {
                GuiStateButton mode = gui.get("mode");
                return new PlayerSelectorGamemode(GameType.byId(mode.getState()));
            }
            
        });
        REGISTRY.register("selector", new GuiPlayerSelectorHandler<PlayerSelectorCommandSelector>() {
            
            @Override
            public void createControls(PlayerSelectorDialog gui, PlayerSelector selector) {
                gui.add(new GuiTextfield("content", selector instanceof PlayerSelectorCommandSelector ? ((PlayerSelectorCommandSelector) selector).pattern : "@a[]"));
            }
            
            @Override
            public PlayerSelectorCommandSelector parseSelector(PlayerSelectorDialog gui) {
                GuiTextfield text = gui.get("content");
                if (text.getText().isEmpty())
                    return null;
                return new PlayerSelectorCommandSelector(text.getText());
            }
            
        });
    }
    
    private String name;
    
    public String getName() {
        return name;
    }
    
    public abstract void createControls(PlayerSelectorDialog gui, PlayerSelector selector);
    
    public abstract T parseSelector(PlayerSelectorDialog gui);
    
    public void onChanged(PlayerSelectorDialog gui, GuiControlChangedEvent event) {}
    
    public static abstract class GuiPlayerSelectorHandlerMultiple<T extends PlayerSelector> extends GuiPlayerSelectorHandler<T> {
        
        public PlayerSelector[] getChildren(PlayerSelector selector) {
            if (selector instanceof PlayerSelectorAnd)
                return ((PlayerSelectorAnd) selector).selectors;
            else if (selector instanceof PlayerSelectorOr)
                return ((PlayerSelectorOr) selector).selectors;
            return new PlayerSelector[] { selector };
        }
        
        @Override
        public void createControls(PlayerSelectorDialog gui, PlayerSelector selector) {
            PlayerSelector[] selectors = getChildren(selector);
            List<GuiPlayerSelectorButton> buttons = new ArrayList<>();
            if (selectors != null)
                for (int i = 0; i < selectors.length; i++)
                    buttons.add(new GuiPlayerSelectorButton("" + i, selectors[i]));
            GuiListBoxBase<GuiPlayerSelectorButton> list = new GuiListBoxBase<>("list", true, buttons);
            gui.add(list);
            gui.add(new GuiButton("add", x -> list.addItem(new GuiPlayerSelectorButton("new", new PlayerSelectorLevel(0)))));
        }
        
        @Override
        public T parseSelector(PlayerSelectorDialog gui) {
            GuiListBoxBase<GuiPlayerSelectorButton> list = gui.get("list");
            PlayerSelector[] selectors = new PlayerSelector[list.size()];
            for (int i = 0; i < selectors.length; i++)
                selectors[i] = list.get(i).get();
            if (selectors.length > 0)
                return parseSelector(selectors);
            return null;
        }
        
        public abstract T parseSelector(PlayerSelector[] selectors);
    }
    
}
