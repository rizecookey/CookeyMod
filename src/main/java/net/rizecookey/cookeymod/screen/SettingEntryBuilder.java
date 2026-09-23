package net.rizecookey.cookeymod.screen;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import me.shedaniel.clothconfig2.impl.builders.LongSliderBuilder;
import me.shedaniel.math.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.BooleanSetting;
import net.rizecookey.cookeymod.config.setting.ColorSetting;
import net.rizecookey.cookeymod.config.setting.DoubleSliderSetting;
import net.rizecookey.cookeymod.config.setting.EnumSetting;
import net.rizecookey.cookeymod.config.setting.Named;
import net.rizecookey.cookeymod.config.setting.SettingVisitor;
import net.rizecookey.cookeymod.extension.clothconfig.entry.PickableColorEntry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static net.rizecookey.cookeymod.config.setting.Setting.getTooltip;

public final class SettingEntryBuilder implements SettingVisitor<ConfigEntryBuilder, AbstractConfigListEntry<?>> {
    public static final SettingEntryBuilder INSTANCE = new SettingEntryBuilder();

    private SettingEntryBuilder() {}

    @Override
    public AbstractConfigListEntry<?> visitBooleanSetting(BooleanSetting booleanSetting, ConfigEntryBuilder entryBuilder) {
        BooleanToggleBuilder builder = entryBuilder
                .startBooleanToggle(Component.translatable(booleanSetting.getTranslationKey()), booleanSetting.get())
                .setDefaultValue(booleanSetting.getDefault())
                .setSaveConsumer(booleanSetting::set);
        builder.requireRestart(booleanSetting.isForceRestart());
        builder.setTooltip(getTooltip(booleanSetting.getTranslationKey()));
        return builder.build();
    }

    @Override
    public AbstractConfigListEntry<?> visitColorSetting(ColorSetting colorSetting, ConfigEntryBuilder entryBuilder) {
        return new PickableColorEntry(Component.translatable(colorSetting.getTranslationKey()),
                colorSetting.get(),
                ConfigEntryBuilder.create().getResetButtonKey(),
                () -> colorSetting.getDefault().getColor(),
                value -> colorSetting.set(Color.ofTransparent(value)),
                () -> getTooltip(colorSetting.getTranslationKey()),
                false);
    }

    @Override
    public AbstractConfigListEntry<?> visitDoubleSliderSetting(DoubleSliderSetting doubleSliderSetting, ConfigEntryBuilder entryBuilder) {
        LongSliderBuilder builder = ConfigEntryBuilder.create()
                .startLongSlider(Component.translatable(doubleSliderSetting.getTranslationKey()),
                        (long) (doubleSliderSetting.get() * 100.0),
                        (long) (doubleSliderSetting.getFrom() * 100.0),
                        (long) (doubleSliderSetting.getTo() * 100.0))
                .setTextGetter(value -> {
                    if (value == 0) {
                        return MutableComponent.create(
                                new TranslatableContents(ModConfig.GENERIC_KEYS + ".off", null, new Object[0]));
                    }
                    return MutableComponent.create(new PlainTextContents.LiteralContents(new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US))
                            .format(value / 100.0)));
                }).setSaveConsumer(value -> doubleSliderSetting.set(value / 100.0))
                .setDefaultValue((long) (doubleSliderSetting.getDefault() * 100.0));
        builder.setTooltip(getTooltip(doubleSliderSetting.getTranslationKey()));
        return builder.build();
    }

    @Override
    public <E extends Enum<E> & Named> AbstractConfigListEntry<?> visitEnumSetting(EnumSetting<E> enumSetting, ConfigEntryBuilder entryBuilder) {
        EnumSelectorBuilder<E> builder = ConfigEntryBuilder.create()
                .startEnumSelector(Component.translatable(enumSetting.getTranslationKey()), enumSetting.getEnumClass(), enumSetting.get())
                .setEnumNameProvider(value -> ((Named) value).getDisplayName())
                .setDefaultValue(enumSetting.getDefault())
                .setSaveConsumer(enumSetting::set);
        builder.requireRestart(enumSetting.isForceRestart());
        builder.setTooltip(getTooltip(enumSetting.getTranslationKey()));
        return builder.build();
    }
}
