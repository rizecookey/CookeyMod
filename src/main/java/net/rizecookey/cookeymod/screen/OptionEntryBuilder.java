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
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.ColorOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.config.option.EnumOption;
import net.rizecookey.cookeymod.config.option.Named;
import net.rizecookey.cookeymod.config.option.OptionVisitor;
import net.rizecookey.cookeymod.extension.clothconfig.entry.PickableColorEntry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static net.rizecookey.cookeymod.config.option.Option.getTooltip;

public final class OptionEntryBuilder implements OptionVisitor<ConfigEntryBuilder, AbstractConfigListEntry<?>> {
    public static final OptionEntryBuilder INSTANCE = new OptionEntryBuilder();

    private OptionEntryBuilder() {}

    @Override
    public AbstractConfigListEntry<?> visitBooleanOption(BooleanOption booleanOption, ConfigEntryBuilder entryBuilder) {
        BooleanToggleBuilder builder = entryBuilder
                .startBooleanToggle(Component.translatable(booleanOption.getTranslationKey()), booleanOption.get())
                .setDefaultValue(booleanOption.getDefault())
                .setSaveConsumer(booleanOption::set);
        builder.requireRestart(booleanOption.isForceRestart());
        builder.setTooltip(getTooltip(booleanOption.getTranslationKey()));
        return builder.build();
    }

    @Override
    public AbstractConfigListEntry<?> visitColorOption(ColorOption colorOption, ConfigEntryBuilder entryBuilder) {
        return new PickableColorEntry(Component.translatable(colorOption.getTranslationKey()),
                colorOption.get(),
                ConfigEntryBuilder.create().getResetButtonKey(),
                () -> colorOption.getDefault().getColor(),
                value -> colorOption.set(Color.ofTransparent(value)),
                () -> getTooltip(colorOption.getTranslationKey()),
                false);
    }

    @Override
    public AbstractConfigListEntry<?> visitDoubleSliderOption(DoubleSliderOption dSliderOption, ConfigEntryBuilder entryBuilder) {
        LongSliderBuilder builder = ConfigEntryBuilder.create()
                .startLongSlider(Component.translatable(dSliderOption.getTranslationKey()),
                        (long) (dSliderOption.get() * 100.0),
                        (long) (dSliderOption.getFrom() * 100.0),
                        (long) (dSliderOption.getTo() * 100.0))
                .setTextGetter(value -> {
                    if (value == 0) {
                        return MutableComponent.create(
                                new TranslatableContents(ModConfig.GENERIC_KEYS + ".off", null, new Object[0]));
                    }
                    return MutableComponent.create(new PlainTextContents.LiteralContents(new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US))
                            .format(value / 100.0)));
                }).setSaveConsumer(value -> dSliderOption.set(value / 100.0))
                .setDefaultValue((long) (dSliderOption.getDefault() * 100.0));
        builder.setTooltip(getTooltip(dSliderOption.getTranslationKey()));
        return builder.build();
    }

    @Override
    public <E extends Enum<E> & Named> AbstractConfigListEntry<?> visitEnumOption(EnumOption<E> enumOption, ConfigEntryBuilder entryBuilder) {
        EnumSelectorBuilder<E> builder = ConfigEntryBuilder.create()
                .startEnumSelector(Component.translatable(enumOption.getTranslationKey()), enumOption.getEnumClass(), enumOption.get())
                .setEnumNameProvider(value -> ((Named) value).getDisplayName())
                .setDefaultValue(enumOption.getDefault())
                .setSaveConsumer(enumOption::set);
        builder.requireRestart(enumOption.isForceRestart());
        builder.setTooltip(getTooltip(enumOption.getTranslationKey()));
        return builder.build();
    }
}
