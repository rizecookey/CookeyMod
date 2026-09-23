package net.rizecookey.cookeymod.config.setting;

public interface SettingVisitor<I, O> {
    O visitBooleanSetting(BooleanSetting booleanSetting, I input);

    O visitColorSetting(ColorSetting colorSetting, I input);

    O visitDoubleSliderSetting(DoubleSliderSetting doubleSliderSetting, I input);

    <E extends Enum<E> & Named> O visitEnumSetting(EnumSetting<E> enumSetting, I input);
}
