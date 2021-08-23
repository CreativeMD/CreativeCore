package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.creativemd.creativecore.common.config.gui.SubGuiPlayerSelectorDialog;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorAnd;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorCommand;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorCommandSelector;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorGamemode;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorLevel;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorNot;
import com.creativemd.creativecore.common.utils.player.PlayerSelector.PlayerSelectorOr;

import net.minecraft.world.level.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.GuiStateButton;
import team.creative.creativecore.common.gui.controls.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

@SideOnly(Side.CLIENT)
public abstract class GuiPlayerSelectorHandler<T extends PlayerSelector> {
    
    @SideOnly(Side.CLIENT)
    private static LinkedHashMap<String, GuiPlayerSelectorHandler> handlers = new LinkedHashMap<>();
    
    public static void registerGuiSelectorHandler(String name, GuiPlayerSelectorHandler handler) {
        handler.name = name;
        handlers.put(name, handler);
    }
    
    public static Set<String> getNames() {
        return handlers.keySet();
    }
    
    public static GuiPlayerSelectorHandler getHandler(PlayerSelector selector) {
        GuiPlayerSelectorHandler handler = selector != null ? handlers.get(PlayerSelector.get(selector.getClass())) : null;
        if (handler != null)
            return handler;
        return GuiPlayerSelectorHandler.defaultHandler;
    }
    
    public static GuiPlayerSelectorHandler getHandler(String name) {
        GuiPlayerSelectorHandler handler = handlers.get(name);
        if (handler == null)
            return defaultHandler;
        return handler;
    }
    
    public static GuiPlayerSelectorHandler defaultHandler;
    
    static {
        registerGuiSelectorHandler("and", new GuiPlayerSelectorHandlerMultiple<PlayerSelectorAnd>() {
            
            @Override
            public PlayerSelectorAnd parseSelector(PlayerSelector[] selectors) {
                return new PlayerSelectorAnd(selectors);
            }
        });
        registerGuiSelectorHandler("or", new GuiPlayerSelectorHandlerMultiple<PlayerSelectorOr>() {
            
            @Override
            public PlayerSelectorOr parseSelector(PlayerSelector[] selectors) {
                return new PlayerSelectorOr(selectors);
            }
        });
        registerGuiSelectorHandler("not", new GuiPlayerSelectorHandler<PlayerSelectorNot>() {
            
            @Override
            public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
                gui.addControl(new GuiPlayerSelectorButton("not", 0, 30, 100, 10, selector instanceof PlayerSelectorNot ? ((PlayerSelectorNot) selector).selector : new PlayerSelectorLevel(0)));
            }
            
            @Override
            public PlayerSelectorNot parseSelector(SubGuiPlayerSelectorDialog gui) {
                GuiPlayerSelectorButton button = (GuiPlayerSelectorButton) gui.get("not");
                PlayerSelector selector = button.get();
                if (selector != null)
                    return new PlayerSelectorNot(selector);
                return null;
            }
        });
        registerGuiSelectorHandler("level", new GuiPlayerSelectorHandler<PlayerSelectorLevel>() {
            
            @Override
            public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
                gui.addControl(new GuiTextfield("content", selector instanceof PlayerSelectorLevel ? "" + ((PlayerSelectorLevel) selector).permissionLevel : "0", 0, 30, 30, 10)
                        .setNumbersOnly());
            }
            
            @Override
            public PlayerSelectorLevel parseSelector(SubGuiPlayerSelectorDialog gui) {
                GuiTextfield text = (GuiTextfield) gui.get("content");
                return new PlayerSelectorLevel(text.parseInteger());
            }
            
        });
        registerGuiSelectorHandler("mode", new GuiPlayerSelectorHandler<PlayerSelectorGamemode>() {
            
            @Override
            public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
                gui.addControl(new GuiStateButton("mode", selector instanceof PlayerSelectorGamemode ? ((PlayerSelectorGamemode) selector).type
                        .getID() : 0, 0, 30, "survival", "creative", "adventure", "spectator"));
            }
            
            @Override
            public PlayerSelectorGamemode parseSelector(SubGuiPlayerSelectorDialog gui) {
                GuiStateButton mode = (GuiStateButton) gui.get("mode");
                return new PlayerSelectorGamemode(GameType.getByID(mode.getState()));
            }
            
        });
        registerGuiSelectorHandler("command", new GuiPlayerSelectorHandler<PlayerSelectorCommand>() {
            
            @Override
            public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
                gui.addControl(new GuiTextfield("content", selector instanceof PlayerSelectorCommand ? "" + ((PlayerSelectorCommand) selector).command : "tell", 0, 30, 100, 10));
            }
            
            @Override
            public PlayerSelectorCommand parseSelector(SubGuiPlayerSelectorDialog gui) {
                GuiTextfield text = (GuiTextfield) gui.get("content");
                if (text.text.isEmpty())
                    return null;
                return new PlayerSelectorCommand(text.text);
            }
            
        });
        registerGuiSelectorHandler("selector", new GuiPlayerSelectorHandler<PlayerSelectorCommandSelector>() {
            
            @Override
            public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
                gui.addControl(new GuiTextfield("content", selector instanceof PlayerSelectorCommandSelector ? "" + ((PlayerSelectorCommandSelector) selector).pattern : "@a[]", 0, 30, 100, 10));
            }
            
            @Override
            public PlayerSelectorCommandSelector parseSelector(SubGuiPlayerSelectorDialog gui) {
                GuiTextfield text = (GuiTextfield) gui.get("content");
                if (text.text.isEmpty())
                    return null;
                return new PlayerSelectorCommandSelector(text.text);
            }
            
        });
    }
    
    private String name;
    
    public String getName() {
        return name;
    }
    
    public abstract void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector);
    
    public abstract T parseSelector(SubGuiPlayerSelectorDialog gui);
    
    public void onChanged(SubGuiPlayerSelectorDialog gui, GuiControlChangedEvent event) {}
    
    public static abstract class GuiPlayerSelectorHandlerMultiple<T extends PlayerSelector> extends GuiPlayerSelectorHandler<T> {
        
        public PlayerSelector[] getChildren(PlayerSelector selector) {
            if (selector instanceof PlayerSelectorAnd)
                return ((PlayerSelectorAnd) selector).selectors;
            else if (selector instanceof PlayerSelectorOr)
                return ((PlayerSelectorOr) selector).selectors;
            return new PlayerSelector[] { selector };
        }
        
        @Override
        public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
            PlayerSelector[] selectors = getChildren(selector);
            List<GuiPlayerSelectorButton> buttons = new ArrayList<>();
            if (selectors != null)
                for (int i = 0; i < selectors.length; i++)
                    buttons.add(new GuiPlayerSelectorButton("" + i, 0, 30, 100, 10, selectors[i]));
            GuiListBoxBase<GuiPlayerSelectorButton> list = new GuiListBoxBase<>("list", 0, 21, 144, 100, true, buttons);
            gui.addControl(list);
            gui.addControl(new GuiButton("add", 50, 130) {
                
                @Override
                public void onClicked(int x, int y, int button) {
                    list.add(new GuiPlayerSelectorButton("new", 0, 0, 100, 10, new PlayerSelectorLevel(0)));
                }
            });
        }
        
        @Override
        public T parseSelector(SubGuiPlayerSelectorDialog gui) {
            GuiListBoxBase<GuiPlayerSelectorButton> list = (GuiListBoxBase<GuiPlayerSelectorButton>) gui.get("list");
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
