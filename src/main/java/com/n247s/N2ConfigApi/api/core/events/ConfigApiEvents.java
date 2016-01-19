package com.n247s.N2ConfigApi.api.core.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.n247s.N2ConfigApi.api.core.ConfigFile;
import com.n247s.api.eventapi.EventApi;
import com.n247s.api.eventapi.eventsystem.EventApiCallHandler;
import com.n247s.api.eventapi.eventsystem.EventBus;
import com.n247s.api.eventapi.eventsystem.EventType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ConfigApiEvents {
	
	public static final ConfigApiEvents instance = new ConfigApiEvents();

	private ConfigApiEvents() {
	}

	public static void preInitialize()
	{
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(onPreServerJoinConfigRecieve.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(onPostServerLeaveConfigRemove.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(onPreServerJoinConfigRecieve.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(onPostServerLeaveConfigRemove.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(OnPreConfigValuesChanged.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(OnPostConfigValuesChanged.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(OnPreConfigAdded.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(OnPostConfigAdded.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(OnPreConfigRemoved.class));
		EventApi.defaultEventBusInstance.bindCallHandler(new EventApiCallHandler(OnPostConfigRemoved.class));
	}
	
	public class ConfigEvent extends EventType {
		private ConfigFile configFile;

		public ConfigEvent(ConfigFile configFile) {
			this.configFile = configFile;
		}

		@Override
		public boolean isCancelable() {
			return false;
		}
		
		public ConfigFile getConfigFile()
		{
			return this.configFile;
		}
		
		public Side getProxySide()
		{
			return FMLCommonHandler.instance().getEffectiveSide();
		}
	}
	
	public class ServerConfigEvent extends EventType
	{
		private EntityPlayer player;
		
		public ServerConfigEvent(EntityPlayer player) {
			this.player = player;
		}
		
		@Override
		public boolean isCancelable() {
			return false;
		}
		
		public EntityPlayer getPlayer()
		{
			return this.player;
		}
		
		public Side getProxySide()
		{
			return FMLCommonHandler.instance().getEffectiveSide();
		}
	}

	/**
	 * This {@link EventType} is raised before the configFiles from the server
	 * has been synchronized with the client. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class onPreServerJoinConfigRecieve extends ServerConfigEvent {
		public onPreServerJoinConfigRecieve(EntityPlayer player) {
			super(player);
		}
	}

	/**
	 * This {@link EventType} is raised after the configFiles from the server
	 * has been synchronized with the client. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class onPostServerJoinConfigRecieve extends ServerConfigEvent {
		public onPostServerJoinConfigRecieve(EntityPlayer player) {
			super(player);
		}
	}

	/**
	 * This {@link EventType} is raised before the configFiles have been cleaned
	 * up after the player leaves the server. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class onPreServerLeaveConfigRemove extends ServerConfigEvent {
		public onPreServerLeaveConfigRemove(EntityPlayer player) {
			super(player);
		}
	}

	/**
	 * This {@link EventType} is raised after the configFiles have been cleaned
	 * up after the player leaves the server. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class onPostServerLeaveConfigRemove extends ServerConfigEvent {
		public onPostServerLeaveConfigRemove(EntityPlayer player) {
			super(player);
		}
	}

	/**
	 * This {@link EventType} is raised before ConfigFile values are changed by
	 * an updatePacket. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class OnPreConfigValuesChanged extends ConfigEvent {
		public OnPreConfigValuesChanged(ConfigFile configFile) {
			super(configFile);
		}
	}

	/**
	 * This {@link EventType} is raised after ConfigFile values are changed by
	 * an updatePacket. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class OnPostConfigValuesChanged extends ConfigEvent {
		public OnPostConfigValuesChanged(ConfigFile configFile) {
			super(configFile);
		}
	}

	/**
	 * This {@link EventType} is raised before a new ConfigFile is added by an
	 * updatePacket.This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class OnPreConfigAdded extends ConfigEvent {
		public OnPreConfigAdded(ConfigFile configFile) {
			super(configFile);
		}
	}

	/**
	 * This {@link EventType} is raised after a new ConfigFile is added by an
	 * updatePacket.This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class OnPostConfigAdded extends ConfigEvent {
		public OnPostConfigAdded(ConfigFile configFile) {
			super(configFile);
		}
	}

	/**
	 * This {@link EventType} is raised before a ConfigFile is removed by an
	 * updatePacket. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class OnPreConfigRemoved extends ConfigEvent {
		public OnPreConfigRemoved(ConfigFile configFile) {
			super(configFile);
		}
	}

	/**
	 * This {@link EventType} is raised after a ConfigFile is removed by an
	 * updatePacket. This event is bound on the
	 * {@link EventApi#defaultEventBusInstance default EventBus}. you can
	 * register your class as follows:
	 * 
	 * <pre>
	 * EventApi.defaultEventBusInstance.RegisterEventListner(listenerClass);
	 * </pre>
	 * 
	 * @see {@link EventBus#RegisterEventListener(Object)}
	 */
	public class OnPostConfigRemoved extends ConfigEvent {
		public OnPostConfigRemoved(ConfigFile configFile) {
			super(configFile);
		}
	}
}
