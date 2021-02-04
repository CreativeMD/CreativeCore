package team.creative.creativecore.common.config.gui;

import com.creativemd.creativecore.common.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.mc.JsonUtils;

public class ConfigGuiLayer extends GuiLayer {
	
	public JsonObject ROOT = new JsonObject();
	public Dist side;
	
	public final ICreativeConfigHolder rootHolder;
	public ICreativeConfigHolder holder;
	
	public boolean changed = false;
	
	public int nextAction;
	public boolean force;
	
	public ConfigGuiLayer(ICreativeConfigHolder holder, Dist side) {
		super("config", 420, 234);
		this.rootHolder = holder;
		this.holder = holder;
		this.side = side;
	}
	
	@Override
	public void create() {
		loadHolder(holder);
	}
	
	public void savePage() {
		GuiScrollBox box = (GuiScrollBox) get("box");
		JsonObject parent = null;
		for (GuiControl control : box.controls)
			if (control instanceof GuiConfigControl) {
				JsonElement element = ((GuiConfigControl) control).save();
				if (element != null) {
					if (parent == null)
						parent = JsonUtils.get(ROOT, holder.path());
					parent.add(((GuiConfigControl) control).field.name, element);
				}
			}
	}
	
	public void loadHolder(ICreativeConfigHolder holder) {
		if (!controls.isEmpty()) {
			savePage();
			controls.clear();
		}
		controls.add(new GuiLabel("/" + String.join("/", holder.path()), 0, 2));
		if (holder != rootHolder)
			controls.add(new GuiButton("back", 391, 0) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					loadHolder(holder.parent());
				}
			});
		this.holder = holder;
		
		GuiScrollBox box = new GuiScrollBox("box", 0, 21, 414, 186);
		controls.add(box);
		
		JsonObject json = JsonUtils.tryGet(ROOT, holder.path());
		
		int offsetX = 1;
		int offsetY = 1;
		for (ConfigKey key : holder.fields()) {
			if (key.requiresRestart)
				continue;
			Object value = key.get();
			String caption = translateOrDefault("config." + String.join(".", holder.path()) + "." + key.name + ".name", key.name);
			String comment = "config." + String.join(".", holder.path()) + "." + key.name + ".comment";
			if (value instanceof ICreativeConfigHolder) {
				if (!((ICreativeConfigHolder) value).isEmpty(side)) {
					box.addControl(new GuiButton(caption, offsetX, offsetY, 100) {
						
						@Override
						public void onClicked(int x, int y, int button) {
							loadHolder((ICreativeConfigHolder) value);
						}
					}.setLangTooltip(comment));
					offsetY += 21;
				}
			} else {
				if (!key.is(side))
					continue;
				
				GuiLabel label = new GuiLabel(caption + ":", offsetX, offsetY + 2);
				
				GuiConfigControl config = new GuiConfigControl((ConfigKeyField) key, 0, offsetY, 100, 14, side);
				GuiButton resetButton = new GuiButton("r", offsetX + 390, offsetY, 14) {
					
					@Override
					public void onClicked(int x, int y, int button) {
						config.reset();
						ConfigGuiLayer.this.changed = true;
					}
				};
				
				int labelWidth = 200;
				config.posX = label.posX + labelWidth + 2;
				config.width = 380 - config.posX;
				config.init(json != null ? json.get(key.name) : null);
				box.addControl(label.setLangTooltip(comment));
				box.addControl(config);
				box.addControl(resetButton.setCustomTooltip("reset to default"));
				config.setResetButton(resetButton);
				offsetY += config.height + 1;
			}
			
		}
		
		controls.add(new GuiButton("cancel", 0, 214) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				nextAction = 0;
				closeGui();
			}
		});
		
		if (side == Dist.DEDICATED_SERVER)
			controls.add(new GuiButton("client-config", 40, 214) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					nextAction = 1;
					closeGui();
				}
			});
		
		controls.add(new GuiButton("save", 390, 214) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				nextAction = 0;
				savePage();
				sendUpdate();
				force = true;
				closeGui();
			}
		});
	}
	
	public void sendUpdate() {
		if (side == Dist.DEDICATED_SERVER)
			PacketHandler.sendPacketToServer(new ConfigurationChangePacket(rootHolder, ROOT));
		else {
			rootHolder.load(false, true, JsonUtils.get(ROOT, rootHolder.path()), Dist.CLIENT);
			CreativeCore.configHandler.save(Side.CLIENT);
		}
	}
	
	@Override
	public void onDialogClosed(String text, String[] buttons, String clicked) {
		if (clicked.equals("Yes")) {
			savePage();
			sendUpdate();
		}
		if (!clicked.equals("Cancel")) {
			force = true;
			closeGui();
		}
	}
	
	@Override
	public void closeGui() {
		if (force || !changed) {
			if (nextAction == 0)
				super.closeGui();
			else if (nextAction == 1)
				GuiHandler.openGui("clientconfig", new NBTTagCompound());
		} else
			openButtonDialogDialog("Do you want to save your changes?", "Yes", "No", "Cancel");
	}
	
	@CustomEventSubscribe
	public void changed(GuiControlChangedEvent event) {
		GuiConfigControl config = getConfigControl((GuiControl) event.source);
		if (config != null) {
			changed = true;
			config.changed();
		}
	}
	
	private static GuiConfigControl getConfigControl(GuiControl control) {
		if (control instanceof GuiConfigControl)
			return (GuiConfigControl) control;
		if (control.getParent() != null)
			return getConfigControl((GuiControl) control.getParent());
		return null;
	}
	
}
