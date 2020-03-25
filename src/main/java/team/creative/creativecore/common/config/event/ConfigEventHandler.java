package team.creative.creativecore.common.config.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.config.sync.ConfigurationPacket;
import team.creative.creativecore.common.util.mc.JsonUtils;

public class ConfigEventHandler {
	
	private final DecimalFormat df = generateFormat();
	
	private DecimalFormat generateFormat() {
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340);
		return df;
	}
	
	private final File CONFIG_DIRECTORY;
	private final Logger LOGGER;
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public ConfigEventHandler(File CONFIG_DIRECTORY, Logger LOGGER) {
		this.CONFIG_DIRECTORY = CONFIG_DIRECTORY;
		this.LOGGER = LOGGER;
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		if (!event.getPlayer().getServer().isSinglePlayer() || !isOwner(event.getPlayer().getServer())) {
			CreativeCore.NETWORK.sendToClient(new ConfigurationClientPacket(CreativeConfigRegistry.ROOT), (ServerPlayerEntity) event.getPlayer());
			syncAll((ServerPlayerEntity) event.getPlayer());
		}
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public boolean isOwner(MinecraftServer server) {
		return server.getServerOwner().equals(Minecraft.getInstance().getSession().getUsername());
	}
	
	@SubscribeEvent
	public void loadWorld(Load event) {
		if (event.getWorld().isRemote())
			load(Dist.CLIENT);
	}
	
	public void serverStarting() {
		load(Dist.DEDICATED_SERVER);
	}
	
	public void sync(ICreativeConfigHolder holder) {
		CreativeCore.NETWORK.sendToClientAll(new ConfigurationPacket(holder));
	}
	
	public void sync(ICreativeConfigHolder holder, ServerPlayerEntity player) {
		CreativeCore.NETWORK.sendToClient(new ConfigurationPacket(holder), player);
		
	}
	
	public void syncAll() {
		sync(CreativeConfigRegistry.ROOT);
	}
	
	public void syncAll(ServerPlayerEntity player) {
		sync(CreativeConfigRegistry.ROOT, player);
	}
	
	public void saveClientFields() {
		File config = new File(CONFIG_DIRECTORY, "client-fields.json");
		List<String> enabled = loadClientFieldList(CreativeConfigRegistry.ROOT);
		
		if (enabled.isEmpty()) {
			config.delete();
			return;
		}
		
		JsonArray array = new JsonArray();
		for (int i = 0; i < enabled.size(); i++)
			array.add(enabled.get(i));
		try {
			FileWriter writer = new FileWriter(config);
			try {
				GSON.toJson(array, writer);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			LOGGER.error("Failed to save client field config file {0}", e);
		}
	}
	
	public void save(String modid, Dist side) {
		try {
			Object object = CreativeConfigRegistry.ROOT.get(modid);
			File config = new File(CONFIG_DIRECTORY, modid + (side.isClient() ? "-client" : "") + ".json");
			if (object instanceof ICreativeConfigHolder || object == null) {
				ICreativeConfigHolder holder = (ICreativeConfigHolder) object;
				JsonObject json = holder.save(true, side);
				JsonUtils.cleanUp(json);
				
				if (json.size() > 0) {
					FileWriter writer = new FileWriter(config);
					
					JsonWriter jsonWriter = new JsonWriter(writer) {
						
						@Override
						public JsonWriter value(double value) throws IOException {
							if (Double.isNaN(value) || Double.isInfinite(value))
								throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
							
							return jsonValue(df.format(value));
						}
						
						@Override
						public JsonWriter value(Number value) throws IOException {
							if (value instanceof Double || value.getClass() == double.class)
								return value((double) value);
							return super.value(value);
						}
						
					};
					jsonWriter.setIndent("  ");
					
					try {
						GSON.toJson(json, jsonWriter);
					} finally {
						writer.close();
					}
				}
			} else if (config.exists())
				config.delete();
		} catch (IOException e) {
			LOGGER.error("Failed to save config file of '{0}', {1}", modid, e);
		}
		
	}
	
	public void save(Dist side) {
		for (String modid : CreativeConfigRegistry.ROOT.names())
			save(modid, side);
	}
	
	public void loadClientFields() {
		File config = new File(CONFIG_DIRECTORY, "client-fields.json");
		List<String> list;
		if (config.exists()) {
			try {
				FileReader reader = new FileReader(config);
				JsonArray array = GSON.fromJson(reader, JsonArray.class);
				if (array != null) {
					list = new ArrayList<>(array.size());
					for (int i = 0; i < array.size(); i++) {
						JsonElement element = array.get(i);
						if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
							list.add(element.getAsString());
					}
					
				} else
					list = Collections.EMPTY_LIST;
			} catch (FileNotFoundException e) {
				list = Collections.EMPTY_LIST;
				LOGGER.error("Failed to load client fields config file, {0}", e);
			}
			
		} else
			list = Collections.EMPTY_LIST;
		
		saveClientFieldList(CreativeConfigRegistry.ROOT, list);
	}
	
	public void load(String modid, Dist side) {
		Object object = CreativeConfigRegistry.ROOT.get(modid);
		if (object instanceof ICreativeConfigHolder) {
			ICreativeConfigHolder holder = (ICreativeConfigHolder) object;
			File config = new File(CONFIG_DIRECTORY, modid + (side.isClient() ? "-client" : "") + ".json");
			if (config.exists()) {
				try {
					FileReader reader = new FileReader(config);
					JsonObject json = GSON.fromJson(reader, JsonObject.class);
					if (json == null)
						json = new JsonObject();
					holder.load(true, json, side);
				} catch (FileNotFoundException e) {
					LOGGER.error("Failed to load config file of '{0}', {1}", modid, e);
				}
			} else
				holder.restoreDefault(side);
		}
	}
	
	public void load(Dist side) {
		loadClientFields();
		
		for (String modid : CreativeConfigRegistry.ROOT.names())
			load(modid, side);
		
		save(side);
	}
	
	public static List<String> loadClientFieldList(ICreativeConfigHolder holder) {
		List<String> enabled = new ArrayList<>();
		for (ConfigKey key : holder.fields())
			if (key.isWithoutForce(Dist.CLIENT))
				ConfigEventHandler.loadClientFieldList(holder, key, enabled);
		return enabled;
	}
	
	private static List<String> loadClientFieldList(ICreativeConfigHolder parent, ConfigKey field, List<String> list) {
		if (field.forceSynchronization) {
			list.add((parent.path().length > 0 ? String.join(".", parent.path()) + "." : "") + field.name);
			return list;
		}
		
		if (field.get() instanceof ICreativeConfigHolder) {
			ICreativeConfigHolder holder = (ICreativeConfigHolder) field.get();
			for (ConfigKey key : holder.fields())
				if (key.isWithoutForce(Dist.CLIENT))
					loadClientFieldList(holder, key, list);
		}
		
		return list;
	}
	
	public static void saveClientFieldList(ICreativeConfigHolder holder, List<String> enabled) {
		for (ConfigKey key : holder.fields())
			if (key.isWithoutForce(Dist.CLIENT))
				saveClientFieldList(String.join(".", holder.path()), key, enabled);
	}
	
	private static void saveClientFieldList(String path, ConfigKey field, List<String> enabled) {
		if (!path.isEmpty())
			path += ".";
		path += field.name;
		if (enabled.contains(path))
			enable(field);
		else {
			field.forceSynchronization = false;
			Object object = field.get();
			if (object instanceof ICreativeConfigHolder)
				for (ConfigKey key : ((ICreativeConfigHolder) object).fields())
					if (key.isWithoutForce(Dist.CLIENT))
						saveClientFieldList(path, key, enabled);
		}
	}
	
	private static void enable(ConfigKey field) {
		field.forceSynchronization = true;
		Object object = field.get();
		if (object instanceof ICreativeConfigHolder)
			for (ConfigKey key : ((ICreativeConfigHolder) object).fields())
				if (key.isWithoutForce(Dist.CLIENT))
					enable(key);
	}
	
	public boolean modFileExist(String modid, Dist side) {
		File config = new File(CONFIG_DIRECTORY, modid + (side.isClient() ? "-client" : "") + ".json");
		return config.exists();
	}
	
}
