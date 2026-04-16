package net.rizecookey.cookeymod.config.option;

public interface OptionVisitor<I, O> {
    O visitBooleanOption(BooleanOption booleanOption, I input);

    O visitColorOption(ColorOption colorOption, I input);

    O visitDoubleSliderOption(DoubleSliderOption doubleSliderOption, I input);

    <E extends Enum<E> & Named> O visitEnumOption(EnumOption<E> enumOption, I input);
}
