package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.Category;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleSliderOption extends Option<Double> {
    public DoubleSliderOption(String id, Category category, Double defaultValue, double from, double to) {
        super(id, category, defaultValue);
        this.entry = () -> ConfigEntryBuilder.create()
                .startLongSlider(new TranslatableComponent(this.getTranslationKey()),
                        (long) (this.get() * 100.0),
                        (long) (from * 100.0),
                        (long) (to * 100.0))
                .setTextGetter(value -> {
                    if (value == 0) {
                        return new TranslatableComponent(ModConfig.GENERIC_KEYS + ".off");
                    }
                    return new TextComponent(new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US))
                            .format(value / 100.0));
                }).setSaveConsumer(value -> this.set(value / 100.0))
                .setDefaultValue((long) (defaultValue * 100.0))
                .build();
        this.mcOption = new ProgressOption(this.getTranslationKey(), from, to, 0.01F, options -> this.get(), (options, aDouble) -> this.set(aDouble), (options, progressOption) -> new TranslatableComponent(this.getTranslationKey()).append(": " + new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US)).format(progressOption.get(options))));
    }
}
