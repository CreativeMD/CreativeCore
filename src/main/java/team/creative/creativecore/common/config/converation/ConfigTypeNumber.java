package team.creative.creativecore.common.config.converation;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.DecimalRangeSupplier;
import team.creative.creativecore.common.config.api.IntRangeSupplier;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration.SimpleConfigTypeConveration;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiSlider;
import team.creative.creativecore.common.gui.control.simple.GuiStateButton;
import team.creative.creativecore.common.gui.control.simple.GuiSteppedSlider;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class ConfigTypeNumber {
    
    private static final NumberFormat NUMBER_FORMAT = createFormat();
    private static final HashMap<Class, IntRangeSupplier> INT_SUPPLIERS = new HashMap<>();
    private static final HashMap<Class, DecimalRangeSupplier> DECIMAL_SUPPLIERS = new HashMap<>();
    
    public static IntRangeSupplier getIntRangeSupplier(Class clazz) {
        var s = INT_SUPPLIERS.get(clazz);
        if (s == null)
            try {
                INT_SUPPLIERS.put(clazz, s = (IntRangeSupplier) clazz.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException("Error when creating " + clazz + " int range supplier");
                
            }
        return s;
    }
    
    public static DecimalRangeSupplier getDecimalRangeSupplier(Class clazz) {
        var s = DECIMAL_SUPPLIERS.get(clazz);
        if (s == null)
            try {
                DECIMAL_SUPPLIERS.put(clazz, s = (DecimalRangeSupplier) clazz.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException("Error when creating " + clazz + " decimal range supplier");
                
            }
        return s;
    }
    
    private static NumberFormat createFormat() {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        format.setGroupingUsed(false);
        return format;
    }
    
    public static void init() {
        ConfigTypeConveration.registerTypes(new SimpleConfigTypeConveration<Boolean>() {
            
            @Override
            public Boolean readElement(ConfigKey key, Boolean defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isBoolean())
                    return element.getAsBoolean();
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(Boolean value, ConfigKey key, Side side) {
                return new JsonPrimitive(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiStateButton<Boolean>("data", 0, new TextMapBuilder<Boolean>().addComponent(false, Component.translatable("gui.false").setStyle(Style.EMPTY
                        .withColor(ChatFormatting.RED))).addComponent(true, Component.translatable("gui.true").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))))
                        .setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Boolean value, GuiParent parent) {
                GuiStateButton<Boolean> button = parent.get("data");
                button.select(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Boolean saveValue(GuiParent parent, ConfigKey key) {
                GuiStateButton<Boolean> button = parent.get("data");
                return button.selected();
            }
            
            @Override
            public Boolean set(ConfigKey key, Boolean value) {
                return value;
            }
            
        }, boolean.class, Boolean.class);
        
        ConfigTypeConveration.registerTypeCreator(boolean.class, () -> false);
        ConfigTypeConveration.registerTypeCreator(Boolean.class, () -> Boolean.FALSE);
        
        ConfigTypeConveration.registerTypes(new SimpleConfigTypeConveration<Number>() {
            
            @Override
            public Number readElement(ConfigKey key, Number defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber()) {
                    Class clazz = key.field().getType();
                    if (clazz == Float.class || clazz == float.class)
                        return element.getAsFloat();
                    else if (clazz == Double.class || clazz == double.class)
                        return element.getAsDouble();
                    else if (clazz == Byte.class || clazz == byte.class)
                        return element.getAsByte();
                    else if (clazz == Short.class || clazz == short.class)
                        return element.getAsShort();
                    else if (clazz == Integer.class || clazz == int.class)
                        return element.getAsInt();
                    else if (clazz == Long.class || clazz == long.class)
                        return element.getAsLong();
                    return element.getAsNumber();
                }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(Number value, ConfigKey key, Side side) {
                return new JsonPrimitive(value);
            }
            
            public boolean isDecimal(Class clazz) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                boolean decimal = isDecimal(key.field().getType());
                if (key != null) {
                    if (decimal) {
                        CreativeConfig.DecimalRange decRange = key.field().getAnnotation(CreativeConfig.DecimalRange.class);
                        if (decRange != null && decRange.slider()) {
                            parent.add(new GuiSlider("data", decRange.min(), decRange.min(), decRange.max()).setExpandableX());
                            return;
                        }
                        CreativeConfig.DecimalRangeSupplier supplier = key.field().getAnnotation(CreativeConfig.DecimalRangeSupplier.class);
                        if (supplier != null && supplier.slider()) {
                            var s = getDecimalRangeSupplier(supplier.supplier());
                            parent.add(new GuiSlider("data", s.getMin(), s.getMin(), s.getMax()).setExpandableX());
                            return;
                        }
                    } else {
                        CreativeConfig.IntRange intRange = key.field().getAnnotation(CreativeConfig.IntRange.class);
                        if (intRange != null && intRange.slider()) {
                            parent.add(new GuiSteppedSlider("data", intRange.min(), intRange.min(), intRange.max()).setExpandableX());
                            return;
                        }
                        CreativeConfig.IntRangeSupplier supplier = key.field().getAnnotation(CreativeConfig.IntRangeSupplier.class);
                        if (supplier != null && supplier.slider()) {
                            var s = getIntRangeSupplier(supplier.supplier());
                            parent.add(new GuiSteppedSlider("data", s.getMin(), s.getMin(), s.getMax()).setExpandableX());
                            return;
                        }
                    }
                }
                
                GuiTextfield textfield = (GuiTextfield) new GuiTextfield("data").setDim(30, 8).setExpandableX();
                if (decimal)
                    textfield.setFloatOnly();
                else
                    textfield.setNumbersIncludingNegativeOnly();
                parent.add(textfield);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Number value, GuiParent parent) {
                GuiControl control = parent.get("data");
                if (control instanceof GuiSteppedSlider button)
                    button.setValue(value.intValue());
                else if (control instanceof GuiSlider button)
                    button.setValue(value.doubleValue());
                else
                    ((GuiTextfield) control).setText(NUMBER_FORMAT.format(value));
            }
            
            public Number parseDecimal(Class clazz, double decimal) {
                if (clazz == Float.class || clazz == float.class)
                    return (float) decimal;
                return decimal;
            }
            
            public Number parseInt(Class clazz, int number) {
                if (clazz == Byte.class || clazz == byte.class)
                    return (byte) number;
                if (clazz == Short.class || clazz == short.class)
                    return (short) number;
                if (clazz == Long.class || clazz == long.class)
                    return (long) number;
                return number;
            }
            
            public Number parseNumber(Class clazz, String text) {
                if (clazz == Float.class || clazz == float.class)
                    try {
                        return Float.parseFloat(text);
                    } catch (NumberFormatException e) {
                        return (float) 0;
                    }
                else if (clazz == Double.class || clazz == double.class)
                    try {
                        return Double.parseDouble(text);
                    } catch (NumberFormatException e) {
                        return (double) 0;
                    }
                else if (clazz == Byte.class || clazz == byte.class)
                    try {
                        return Byte.parseByte(text);
                    } catch (NumberFormatException e) {
                        return (byte) 0;
                    }
                else if (clazz == Short.class || clazz == short.class)
                    try {
                        return Short.parseShort(text);
                    } catch (NumberFormatException e) {
                        return (short) 0;
                    }
                else if (clazz == Integer.class || clazz == int.class)
                    try {
                        return Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                else if (clazz == Long.class || clazz == long.class)
                    try {
                        return Long.parseLong(text);
                    } catch (NumberFormatException e) {
                        return (long) 0;
                    }
                else
                    return 0;
                
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Number saveValue(GuiParent parent, ConfigKey key) {
                GuiControl control = parent.get("data");
                String text;
                if (control instanceof GuiSteppedSlider button) {
                    text = "" + button.getIntValue();
                } else if (control instanceof GuiSlider button) {
                    text = "" + button.getValue();
                } else
                    text = ((GuiTextfield) control).getText();
                return parseNumber(key.field().getType(), text);
            }
            
            @Override
            public Number set(ConfigKey key, Number value) {
                if (key != null) {
                    Class clazz = key.field().getType();
                    if (isDecimal(clazz)) {
                        CreativeConfig.DecimalRange decRange = key.field().getAnnotation(CreativeConfig.DecimalRange.class);
                        if (decRange != null)
                            return parseDecimal(clazz, Mth.clamp(value.doubleValue(), decRange.min(), decRange.max()));
                        CreativeConfig.DecimalRangeSupplier supplier = key.field().getAnnotation(CreativeConfig.DecimalRangeSupplier.class);
                        if (supplier != null) {
                            var s = getDecimalRangeSupplier(supplier.supplier());
                            return parseDecimal(clazz, Mth.clamp(value.intValue(), s.getMin(), s.getMax()));
                        }
                    } else {
                        CreativeConfig.IntRange intRange = key.field().getAnnotation(CreativeConfig.IntRange.class);
                        if (intRange != null)
                            return parseInt(clazz, Mth.clamp(value.intValue(), intRange.min(), intRange.max()));
                        CreativeConfig.IntRangeSupplier supplier = key.field().getAnnotation(CreativeConfig.IntRangeSupplier.class);
                        if (supplier != null) {
                            var s = getIntRangeSupplier(supplier.supplier());
                            return parseInt(clazz, Mth.clamp(value.intValue(), s.getMin(), s.getMax()));
                        }
                    }
                }
                return value;
            }
            
        }, byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class);
        
        ConfigTypeConveration.registerTypeCreator(byte.class, () -> (byte) 0);
        ConfigTypeConveration.registerTypeCreator(Byte.class, () -> Byte.valueOf((byte) 0));
        ConfigTypeConveration.registerTypeCreator(short.class, () -> (short) 0);
        ConfigTypeConveration.registerTypeCreator(Short.class, () -> Short.valueOf((short) 0));
        ConfigTypeConveration.registerTypeCreator(int.class, () -> 0);
        ConfigTypeConveration.registerTypeCreator(Integer.class, () -> Integer.valueOf(0));
        ConfigTypeConveration.registerTypeCreator(long.class, () -> (long) 0);
        ConfigTypeConveration.registerTypeCreator(Long.class, () -> Long.valueOf(0));
        ConfigTypeConveration.registerTypeCreator(float.class, () -> 0F);
        ConfigTypeConveration.registerTypeCreator(Float.class, () -> Float.valueOf(0));
        ConfigTypeConveration.registerTypeCreator(double.class, () -> 0D);
        ConfigTypeConveration.registerTypeCreator(Double.class, () -> Double.valueOf(0));
    }
}
