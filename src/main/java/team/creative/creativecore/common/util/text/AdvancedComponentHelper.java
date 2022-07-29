package team.creative.creativecore.common.util.text;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringDecomposer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.util.text.content.AdvancedContent;
import team.creative.creativecore.common.util.text.content.AdvancedContentConsumer;
import team.creative.creativecore.common.util.text.content.ContentItemStack;
import team.creative.creativecore.common.util.text.content.FormattedSingleSink;

public class AdvancedComponentHelper {
    
    public static final AdvancedStringSplitter SPLITTER = new AdvancedStringSplitter(Minecraft.getInstance().font);
    private static final Optional<Object> STOP_ITERATION = Optional.of(Unit.INSTANCE);
    
    public static boolean iterateFormatted(Component text, Style style, FormattedSingleSink sink) {
        return !visit(text, new AdvancedContentConsumer() {
            
            @Override
            public Optional accept(Style style, AdvancedContent content) {
                return sink.accept(style, content) ? Optional.empty() : STOP_ITERATION;
            }
            
            @Override
            public Optional accept(Style style, String content) {
                return StringDecomposer.iterateFormatted(content.toString(), 0, style, style, sink) ? Optional.empty() : STOP_ITERATION;
            }
            
        }, style).isPresent();
    }
    
    public static <T> Optional<T> visit(Component text, AdvancedContentConsumer<T> consumer, Style defaultStyle) {
        Style style = text.getStyle().applyTo(defaultStyle);
        Optional<T> optional = visit(text.getContents(), consumer, style);
        if (optional.isPresent())
            return optional;
        else {
            for (Component component : text.getSiblings()) {
                Optional<T> optional1 = visit(component, consumer, style);
                if (optional1.isPresent())
                    return optional1;
            }
            return Optional.empty();
        }
    }
    
    public static <T> Optional<T> visit(ComponentContents content, AdvancedContentConsumer<T> consumer, Style style) {
        if (content instanceof AdvancedContent adv)
            return adv.visit(consumer, style);
        return content.visit((cStyle, text) -> {
            return consumer.accept(cStyle, text);
        }, style);
    }
    
    public static class Serializer extends Component.Serializer {
        
        @Override
        public JsonElement serialize(Component component, Type type, JsonSerializationContext context) {
            JsonObject jsonobject = new JsonObject();
            if (!component.getStyle().isEmpty()) {
                this.serializeStyle(component.getStyle(), jsonobject, context);
            }
            
            if (!component.getSiblings().isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                
                for (Component comp : component.getSiblings())
                    jsonarray.add(this.serialize(comp, Component.class, context));
                
                jsonobject.add("extra", jsonarray);
            }
            
            ComponentContents componentcontents = component.getContents();
            if (componentcontents == ComponentContents.EMPTY)
                jsonobject.addProperty("text", "");
            else if (componentcontents instanceof ContentItemStack stack) {
                jsonobject.addProperty("itemstack", stack.stack.save(new CompoundTag()).toString());
            } else if (componentcontents instanceof LiteralContents) {
                LiteralContents literalcontents = (LiteralContents) componentcontents;
                jsonobject.addProperty("text", literalcontents.text());
            } else if (componentcontents instanceof TranslatableContents) {
                TranslatableContents translatablecontents = (TranslatableContents) componentcontents;
                jsonobject.addProperty("translate", translatablecontents.getKey());
                if (translatablecontents.getArgs().length > 0) {
                    JsonArray jsonarray1 = new JsonArray();
                    
                    for (Object object : translatablecontents.getArgs()) {
                        if (object instanceof Component) {
                            jsonarray1.add(this.serialize((Component) object, object.getClass(), context));
                        } else {
                            jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                        }
                    }
                    
                    jsonobject.add("with", jsonarray1);
                }
            } else if (componentcontents instanceof ScoreContents) {
                ScoreContents scorecontents = (ScoreContents) componentcontents;
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.addProperty("name", scorecontents.getName());
                jsonobject1.addProperty("objective", scorecontents.getObjective());
                jsonobject.add("score", jsonobject1);
            } else if (componentcontents instanceof SelectorContents) {
                SelectorContents selectorcontents = (SelectorContents) componentcontents;
                jsonobject.addProperty("selector", selectorcontents.getPattern());
                this.serializeSeparator(context, jsonobject, selectorcontents.getSeparator());
            } else if (componentcontents instanceof KeybindContents) {
                KeybindContents keybindcontents = (KeybindContents) componentcontents;
                jsonobject.addProperty("keybind", keybindcontents.getName());
            } else {
                if (!(componentcontents instanceof NbtContents)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + componentcontents + " as a Component");
                }
                
                NbtContents nbtcontents = (NbtContents) componentcontents;
                jsonobject.addProperty("nbt", nbtcontents.getNbtPath());
                jsonobject.addProperty("interpret", nbtcontents.isInterpreting());
                this.serializeSeparator(context, jsonobject, nbtcontents.getSeparator());
                DataSource datasource = nbtcontents.getDataSource();
                if (datasource instanceof BlockDataSource) {
                    BlockDataSource blockdatasource = (BlockDataSource) datasource;
                    jsonobject.addProperty("block", blockdatasource.posPattern());
                } else if (datasource instanceof EntityDataSource) {
                    EntityDataSource entitydatasource = (EntityDataSource) datasource;
                    jsonobject.addProperty("entity", entitydatasource.selectorPattern());
                } else {
                    if (!(datasource instanceof StorageDataSource)) {
                        throw new IllegalArgumentException("Don't know how to serialize " + componentcontents + " as a Component");
                    }
                    
                    StorageDataSource storagedatasource = (StorageDataSource) datasource;
                    jsonobject.addProperty("storage", storagedatasource.id().toString());
                }
            }
            
            return jsonobject;
        }
        
        private static Object unwrapTextArgument(Object object) {
            if (object instanceof Component component) {
                if (component.getStyle().isEmpty() && component.getSiblings().isEmpty()) {
                    ComponentContents componentcontents = component.getContents();
                    if (componentcontents instanceof LiteralContents) {
                        LiteralContents literalcontents = (LiteralContents) componentcontents;
                        return literalcontents.text();
                    }
                }
            }
            
            return object;
        }
        
        private Optional<Component> parseSeparator(Type type, JsonDeserializationContext context, JsonObject object) {
            return object.has("separator") ? Optional.of(this.deserialize(object.get("separator"), type, context)) : Optional.empty();
        }
        
        private void serializeSeparator(JsonSerializationContext context, JsonObject object, Optional<Component> optional) {
            optional.ifPresent((comp) -> object.add("separator", this.serialize(comp, comp.getClass(), context)));
        }
        
        private void serializeStyle(Style style, JsonObject object, JsonSerializationContext context) {
            JsonElement jsonelement = context.serialize(style);
            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = (JsonObject) jsonelement;
                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
                    object.add(entry.getKey(), entry.getValue());
            }
        }
        
        @Override
        public MutableComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (element.isJsonPrimitive())
                return Component.literal(element.getAsString());
            else if (!element.isJsonObject()) {
                if (element.isJsonArray()) {
                    JsonArray jsonarray1 = element.getAsJsonArray();
                    MutableComponent mutablecomponent1 = null;
                    
                    for (JsonElement jsonelement : jsonarray1) {
                        MutableComponent mutablecomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), context);
                        if (mutablecomponent1 == null) {
                            mutablecomponent1 = mutablecomponent2;
                        } else {
                            mutablecomponent1.append(mutablecomponent2);
                        }
                    }
                    
                    return mutablecomponent1;
                } else
                    throw new JsonParseException("Don't know how to turn " + element + " into a Component");
            } else {
                JsonObject jsonobject = element.getAsJsonObject();
                MutableComponent mutablecomponent;
                if (jsonobject.has("text")) {
                    String s = GsonHelper.getAsString(jsonobject, "text");
                    mutablecomponent = s.isEmpty() ? Component.empty() : Component.literal(s);
                } else if (jsonobject.has("itemstack"))
                    try {
                        mutablecomponent = MutableComponent.create(new ContentItemStack(ItemStack.of(TagParser.parseTag(jsonobject.get("itemstack").getAsString()))));
                    } catch (CommandSyntaxException e) {
                        throw new JsonParseException(e);
                    }
                else if (jsonobject.has("translate")) {
                    String s1 = GsonHelper.getAsString(jsonobject, "translate");
                    if (jsonobject.has("with")) {
                        JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "with");
                        Object[] aobject = new Object[jsonarray.size()];
                        
                        for (int i = 0; i < aobject.length; ++i)
                            aobject[i] = unwrapTextArgument(this.deserialize(jsonarray.get(i), type, context));
                        
                        mutablecomponent = Component.translatable(s1, aobject);
                    } else
                        mutablecomponent = Component.translatable(s1);
                } else if (jsonobject.has("score")) {
                    JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "score");
                    if (!jsonobject1.has("name") || !jsonobject1.has("objective"))
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    
                    mutablecomponent = Component.score(GsonHelper.getAsString(jsonobject1, "name"), GsonHelper.getAsString(jsonobject1, "objective"));
                } else if (jsonobject.has("selector")) {
                    Optional<Component> optional = this.parseSeparator(type, context, jsonobject);
                    mutablecomponent = Component.selector(GsonHelper.getAsString(jsonobject, "selector"), optional);
                } else if (jsonobject.has("keybind"))
                    mutablecomponent = Component.keybind(GsonHelper.getAsString(jsonobject, "keybind"));
                else {
                    if (!jsonobject.has("nbt"))
                        throw new JsonParseException("Don't know how to turn " + element + " into a Component");
                    
                    String s2 = GsonHelper.getAsString(jsonobject, "nbt");
                    Optional<Component> optional1 = this.parseSeparator(type, context, jsonobject);
                    boolean flag = GsonHelper.getAsBoolean(jsonobject, "interpret", false);
                    DataSource datasource;
                    if (jsonobject.has("block"))
                        datasource = new BlockDataSource(GsonHelper.getAsString(jsonobject, "block"));
                    else if (jsonobject.has("entity"))
                        datasource = new EntityDataSource(GsonHelper.getAsString(jsonobject, "entity"));
                    else {
                        if (!jsonobject.has("storage"))
                            throw new JsonParseException("Don't know how to turn " + element + " into a Component");
                        
                        datasource = new StorageDataSource(new ResourceLocation(GsonHelper.getAsString(jsonobject, "storage")));
                    }
                    
                    mutablecomponent = Component.nbt(s2, flag, optional1, datasource);
                }
                
                if (jsonobject.has("extra")) {
                    JsonArray jsonarray2 = GsonHelper.getAsJsonArray(jsonobject, "extra");
                    if (jsonarray2.size() <= 0)
                        throw new JsonParseException("Unexpected empty array of components");
                    
                    for (int j = 0; j < jsonarray2.size(); ++j)
                        mutablecomponent.append(this.deserialize(jsonarray2.get(j), type, context));
                }
                
                mutablecomponent.setStyle(context.deserialize(element, Style.class));
                return mutablecomponent;
            }
        }
        
    }
}
