package team.creative.creativecore.common.util.player;

import java.lang.reflect.InvocationTargetException;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.GuiPlayerSelectorButton;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.util.mc.PlayerUtils;
import team.creative.creativecore.common.util.registry.NamedTypeRegistry;

public abstract class PlayerSelector {
    
    public static final NamedTypeRegistry<PlayerSelector> REGISTRY = new NamedTypeRegistry<PlayerSelector>().addConstructorPattern();
    
    public static PlayerSelector read(CompoundTag nbt) {
        Class<? extends PlayerSelector> clazz = REGISTRY.get(nbt.getString("id"));
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
        REGISTRY.register("and", PlayerSelectorAnd.class);
        REGISTRY.register("or", PlayerSelectorOr.class);
        REGISTRY.register("not", PlayerSelectorNot.class);
        REGISTRY.register("level", PlayerSelectorLevel.class);
        REGISTRY.register("mode", PlayerSelectorGamemode.class);
        REGISTRY.register("selector", PlayerSelectorCommandSelector.class);
        
        ConfigTypeConveration.registerSpecialType(PlayerSelector.class::isAssignableFrom, new ConfigTypeConveration.SimpleConfigTypeConveration<PlayerSelector>() {
            
            @Override
            public PlayerSelector readElement(ConfigKey key, PlayerSelector defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    try {
                        return PlayerSelector.read(TagParser.parseTag(element.getAsString()));
                    } catch (CommandSyntaxException e) {
                        CreativeCore.LOGGER.error(e);
                    }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(PlayerSelector value, ConfigKey key, Side side) {
                return new JsonPrimitive(value.writeToNBT(new CompoundTag()).toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiPlayerSelectorButton("data", new PlayerSelectorLevel(0)));
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(PlayerSelector value, GuiParent parent) {
                GuiPlayerSelectorButton button = parent.get("data");
                button.set(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected PlayerSelector saveValue(GuiParent parent, ConfigKey key) {
                GuiPlayerSelectorButton button = parent.get("data");
                return button.get();
            }
            
            @Override
            public PlayerSelector set(ConfigKey key, PlayerSelector value) {
                return value;
            }
            
        });
    }
    
    public abstract boolean is(Player player);
    
    public abstract void readFromNBT(CompoundTag nbt);
    
    protected abstract void write(CompoundTag nbt);
    
    public abstract String info();
    
    public CompoundTag writeToNBT(CompoundTag nbt) {
        write(nbt);
        nbt.putString("id", REGISTRY.getId(getClass()));
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
        public boolean is(Player player) {
            for (int i = 0; i < selectors.length; i++)
                if (!selectors[i].is(player))
                    return false;
            return true;
        }
        
        @Override
        public void readFromNBT(CompoundTag nbt) {
            ListTag list = nbt.getList("selectors", 10);
            selectors = new PlayerSelector[list.size()];
            for (int i = 0; i < selectors.length; i++)
                selectors[i] = PlayerSelector.read(list.getCompound(i));
        }
        
        @Override
        protected void write(CompoundTag nbt) {
            ListTag list = new ListTag();
            for (int i = 0; i < selectors.length; i++)
                list.add(selectors[i].writeToNBT(new CompoundTag()));
            nbt.put("selectors", list);
        }
        
        @Override
        public String info() {
            StringBuilder text = new StringBuilder("[");
            for (int i = 0; i < selectors.length; i++) {
                if (i > 0)
                    text.append("&");
                text.append(selectors[i].info());
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
        public boolean is(Player player) {
            for (int i = 0; i < selectors.length; i++)
                if (selectors[i].is(player))
                    return true;
            return false;
        }
        
        @Override
        public void readFromNBT(CompoundTag nbt) {
            ListTag list = nbt.getList("selectors", 10);
            selectors = new PlayerSelector[list.size()];
            for (int i = 0; i < selectors.length; i++)
                selectors[i] = PlayerSelector.read(list.getCompound(i));
        }
        
        @Override
        protected void write(CompoundTag nbt) {
            ListTag list = new ListTag();
            for (int i = 0; i < selectors.length; i++)
                list.add(selectors[i].writeToNBT(new CompoundTag()));
            nbt.put("selectors", list);
        }
        
        @Override
        public String info() {
            StringBuilder text = new StringBuilder("[");
            for (int i = 0; i < selectors.length; i++) {
                if (i > 0)
                    text.append("|");
                text.append(selectors[i].info());
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
        public boolean is(Player player) {
            return !selector.is(player);
        }
        
        @Override
        public void readFromNBT(CompoundTag nbt) {
            selector = PlayerSelector.read(nbt.getCompound("child"));
        }
        
        @Override
        protected void write(CompoundTag nbt) {
            nbt.put("child", selector.writeToNBT(new CompoundTag()));
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
        public boolean is(Player player) {
            return PlayerUtils.getGameType(player) == type;
        }
        
        @Override
        public void readFromNBT(CompoundTag nbt) {
            type = GameType.byId(nbt.getInt("mode"));
        }
        
        @Override
        protected void write(CompoundTag nbt) {
            nbt.putInt("mode", type.getId());
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
        public boolean is(Player player) {
            if (player instanceof ServerPlayer) {
                ServerOpListEntry entry = player.getServer().getPlayerList().getOps().get(player.getGameProfile());
                if (entry != null)
                    return entry.getLevel() >= permissionLevel;
                return player.getServer().getOperatorUserPermissionLevel() >= permissionLevel;
            }
            return true;
        }
        
        @Override
        public void readFromNBT(CompoundTag nbt) {
            this.permissionLevel = nbt.getInt("level");
        }
        
        @Override
        protected void write(CompoundTag nbt) {
            nbt.putInt("level", permissionLevel);
        }
        
        @Override
        public String info() {
            return "level>=" + permissionLevel;
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
        public boolean is(Player player) {
            try {
                if (player instanceof ServerPlayer)
                    return EntityArgument.players().parse(new StringReader(pattern)).findPlayers(player.getServer().createCommandSourceStack()).contains(player);
                return true;
            } catch (CommandSyntaxException e) {}
            return false;
        }
        
        @Override
        public void readFromNBT(CompoundTag nbt) {
            this.pattern = nbt.getString("pattern");
        }
        
        @Override
        protected void write(CompoundTag nbt) {
            nbt.putString("pattern", pattern);
        }
        
        @Override
        public String info() {
            return pattern;
        }
        
    }
    
}
