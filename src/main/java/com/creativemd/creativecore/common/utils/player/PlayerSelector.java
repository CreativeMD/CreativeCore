package com.creativemd.creativecore.common.utils.player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.gui.GuiPlayerSelectorButton;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.utils.mc.PlayerUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PlayerSelector {
    
    private static HashMap<String, Class<? extends PlayerSelector>> selectorTypes = new HashMap<>();
    
    public static void registerSelectorType(String id, Class<? extends PlayerSelector> clazz) {
        selectorTypes.put(id, clazz);
    }
    
    public static Class<? extends PlayerSelector> get(String id) {
        return selectorTypes.get(id);
    }
    
    public static String get(Class<? extends PlayerSelector> clazz) {
        for (Entry<String, Class<? extends PlayerSelector>> entry : selectorTypes.entrySet())
            if (entry.getValue() == clazz)
                return entry.getKey();
        throw new RuntimeException("Could not find player selector id for " + clazz);
    }
    
    public static PlayerSelector read(NBTTagCompound nbt) {
        Class<? extends PlayerSelector> clazz = get(nbt.getString("id"));
        if (clazz == null)
            throw new RuntimeException("Could not find player selector for " + nbt.getString("id"));
        
        try {
            PlayerSelector selector = clazz.getConstructor().newInstance();
            selector.readFromNBT(nbt);
            return selector;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("PlayerSelector type " + nbt.getString("id") + " does not have an empty constructor!");
        }
    }
    
    static {
        registerSelectorType("and", PlayerSelectorAnd.class);
        registerSelectorType("or", PlayerSelectorOr.class);
        registerSelectorType("not", PlayerSelectorNot.class);
        registerSelectorType("level", PlayerSelectorLevel.class);
        registerSelectorType("mode", PlayerSelectorGamemode.class);
        registerSelectorType("command", PlayerSelectorCommand.class);
        registerSelectorType("selector", PlayerSelectorCommandSelector.class);
        
        ConfigTypeConveration.registerSpecialType((x) -> PlayerSelector.class.isAssignableFrom(x), new ConfigTypeConveration.SimpleConfigTypeConveration<PlayerSelector>() {
            
            @Override
            public PlayerSelector readElement(PlayerSelector defaultValue, boolean loadDefault, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    try {
                        return PlayerSelector.read(JsonToNBT.getTagFromJson(element.getAsString()));
                    } catch (NBTException e) {
                        e.printStackTrace();
                    }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(PlayerSelector value, PlayerSelector defaultValue, boolean saveDefault) {
                return new JsonPrimitive(value.writeToNBT(new NBTTagCompound()).toString());
            }
            
            @Override
            @SideOnly(Side.CLIENT)
            public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
                parent.addControl(new GuiPlayerSelectorButton("data", 0, 0, Math.min(150, parent.width - 50), 14, new PlayerSelectorLevel(0)));
            }
            
            @Override
            @SideOnly(Side.CLIENT)
            public void loadValue(PlayerSelector value, GuiParent parent) {
                GuiPlayerSelectorButton button = (GuiPlayerSelectorButton) parent.get("data");
                button.set(value);
            }
            
            @Override
            @SideOnly(Side.CLIENT)
            protected PlayerSelector saveValue(GuiParent parent, Class clazz) {
                GuiPlayerSelectorButton button = (GuiPlayerSelectorButton) parent.get("data");
                return button.get();
            }
            
            @Override
            public PlayerSelector set(ConfigKeyField key, PlayerSelector value) {
                return value;
            }
        });
    }
    
    public abstract boolean is(EntityPlayer player);
    
    public abstract void readFromNBT(NBTTagCompound nbt);
    
    protected abstract void write(NBTTagCompound nbt);
    
    public abstract String info();
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        write(nbt);
        nbt.setString("id", get(getClass()));
        return nbt;
    }
    
    public static class PlayerSelectorAnd extends PlayerSelector {
        
        public PlayerSelector[] selectors;
        
        public PlayerSelectorAnd() {
            
        }
        
        public PlayerSelectorAnd(PlayerSelector... selector) {
            this.selectors = selector;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            for (int i = 0; i < selectors.length; i++)
                if (!selectors[i].is(player))
                    return false;
            return true;
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList list = nbt.getTagList("selectors", 10);
            selectors = new PlayerSelector[list.tagCount()];
            for (int i = 0; i < selectors.length; i++)
                selectors[i] = PlayerSelector.read(list.getCompoundTagAt(i));
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < selectors.length; i++)
                list.appendTag(selectors[i].writeToNBT(new NBTTagCompound()));
            nbt.setTag("selectors", list);
        }
        
        @Override
        public String info() {
            String text = "[";
            for (int i = 0; i < selectors.length; i++) {
                if (i > 0)
                    text += "&";
                text += selectors[i].info();
            }
            return text + "]";
        }
        
    }
    
    public static class PlayerSelectorOr extends PlayerSelector {
        
        public PlayerSelector[] selectors;
        
        public PlayerSelectorOr() {
            
        }
        
        public PlayerSelectorOr(PlayerSelector... selector) {
            this.selectors = selector;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            for (int i = 0; i < selectors.length; i++)
                if (selectors[i].is(player))
                    return true;
            return false;
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList list = nbt.getTagList("selectors", 10);
            selectors = new PlayerSelector[list.tagCount()];
            for (int i = 0; i < selectors.length; i++)
                selectors[i] = PlayerSelector.read(list.getCompoundTagAt(i));
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < selectors.length; i++)
                list.appendTag(selectors[i].writeToNBT(new NBTTagCompound()));
            nbt.setTag("selectors", list);
        }
        
        @Override
        public String info() {
            String text = "[";
            for (int i = 0; i < selectors.length; i++) {
                if (i > 0)
                    text += "|";
                text += selectors[i].info();
            }
            return text + "]";
        }
        
    }
    
    public static class PlayerSelectorNot extends PlayerSelector {
        
        public PlayerSelector selector;
        
        public PlayerSelectorNot() {
            
        }
        
        public PlayerSelectorNot(PlayerSelector selector) {
            this.selector = selector;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            return !selector.is(player);
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            selector = PlayerSelector.read(nbt.getCompoundTag("child"));
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            nbt.setTag("child", selector.writeToNBT(new NBTTagCompound()));
        }
        
        @Override
        public String info() {
            return "!" + selector.info();
        }
        
    }
    
    public static class PlayerSelectorGamemode extends PlayerSelector {
        
        public GameType type;
        
        public PlayerSelectorGamemode() {
            
        }
        
        public PlayerSelectorGamemode(GameType type) {
            this.type = type;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            return PlayerUtils.getGameType(player) == type;
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            type = GameType.getByID(nbt.getInteger("mode"));
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            nbt.setInteger("mode", type.getID());
        }
        
        @Override
        public String info() {
            return type.getName();
        }
        
    }
    
    public static class PlayerSelectorLevel extends PlayerSelector {
        
        public int permissionLevel;
        
        public PlayerSelectorLevel() {
            
        }
        
        public PlayerSelectorLevel(int permissionLevel) {
            this.permissionLevel = permissionLevel;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            if (player instanceof EntityPlayerMP) {
                UserListOpsEntry entry = player.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
                
                if (entry != null)
                    return entry.getPermissionLevel() >= permissionLevel;
                return player.getServer().getOpPermissionLevel() >= permissionLevel;
            }
            return true;
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            this.permissionLevel = nbt.getInteger("level");
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            nbt.setInteger("level", permissionLevel);
        }
        
        @Override
        public String info() {
            return "level>=" + permissionLevel;
        }
        
    }
    
    public static class PlayerSelectorCommand extends PlayerSelector {
        
        public String command;
        
        public PlayerSelectorCommand() {
            
        }
        
        public PlayerSelectorCommand(String command) {
            this.command = command;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            if (player.getServer() == null)
                return false;
            ICommand command = player.getServer().getCommandManager().getCommands().get(this.command);
            if (command != null)
                return command.checkPermission(player.getServer(), player);
            return false;
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            command = nbt.getString("command");
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            nbt.setString("command", command);
        }
        
        @Override
        public String info() {
            return "/" + command;
        }
        
    }
    
    public static class PlayerSelectorCommandSelector extends PlayerSelector {
        
        public String pattern;
        
        public PlayerSelectorCommandSelector() {
            
        }
        
        public PlayerSelectorCommandSelector(String pattern) {
            this.pattern = pattern;
        }
        
        @Override
        public boolean is(EntityPlayer player) {
            try {
                return EntitySelector.getPlayers(player, pattern).contains(player);
            } catch (CommandException e) {}
            return false;
        }
        
        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            this.pattern = nbt.getString("pattern");
        }
        
        @Override
        protected void write(NBTTagCompound nbt) {
            nbt.setString("pattern", pattern);
        }
        
        @Override
        public String info() {
            return pattern;
        }
        
    }
    
}
