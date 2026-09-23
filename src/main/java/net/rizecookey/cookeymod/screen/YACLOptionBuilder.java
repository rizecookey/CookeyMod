package net.rizecookey.cookeymod.screen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.BooleanSetting;
import net.rizecookey.cookeymod.config.setting.ColorSetting;
import net.rizecookey.cookeymod.config.setting.DoubleSliderSetting;
import net.rizecookey.cookeymod.config.setting.EnumSetting;
import net.rizecookey.cookeymod.config.setting.Named;
import net.rizecookey.cookeymod.config.setting.Setting;
import net.rizecookey.cookeymod.config.setting.SettingVisitor;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class YACLOptionBuilder implements SettingVisitor<@Nullable Void, Option<?>> {
    public static final YACLOptionBuilder INSTANCE = new YACLOptionBuilder();

    private static final DecimalFormat DOUBLE_SLIDER_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private YACLOptionBuilder() {}

    private <T> Option.Builder<T> createBuilderWithoutController(Setting<T> setting) {
        var builder = Option.<T>createBuilder()
                .name(Component.translatable(setting.getTranslationKey()))
                .binding(
                        setting.getDefault(),
                        setting::get,
                        setting::set
                );

        String descriptionKey = setting.getDescriptionTranslationKey();
        if (Language.getInstance().has(descriptionKey)) {
            Component description = Component.translatable(descriptionKey);
            builder.description(OptionDescription.of(description));
        }
        return builder;
    }

    @Override
    public Option<Boolean> visitBooleanSetting(BooleanSetting booleanSetting, @Nullable Void none) {
        var builder = createBuilderWithoutController(booleanSetting)
                .controller(TickBoxControllerBuilder::create);

        if (booleanSetting.isForceRestart()) builder.flag(OptionFlag.GAME_RESTART);

        return builder.build();
    }

    @Override
    public Option<Color> visitColorSetting(ColorSetting colorSetting, @Nullable Void none) {
        return createBuilderWithoutController(colorSetting)
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build();
    }

    @Override
    public Option<Double> visitDoubleSliderSetting(DoubleSliderSetting dSliderSetting, @Nullable Void none) {
        return createBuilderWithoutController(dSliderSetting)
                .controller(option -> DoubleSliderControllerBuilder.create(option)
                        .range(dSliderSetting.getFrom(), dSliderSetting.getTo())
                        .step(0.01)
                        .formatValue(value -> {
                            if (value == 0) {
                                return Component.translatable(ModConfig.GENERIC_KEYS + ".off");
                            }

                            return Component.literal(DOUBLE_SLIDER_FORMAT.format(value));
                        }))
                .build();
    }

    @Override
    public <E extends Enum<E> & Named> Option<E> visitEnumSetting(EnumSetting<E> enumSetting, @Nullable Void none) {
        return createBuilderWithoutController(enumSetting)
                .controller(option -> EnumControllerBuilder.create(option)
                        .enumClass(enumSetting.getEnumClass())
                        .formatValue(Named::getDisplayName))
                .build();
    }
}
