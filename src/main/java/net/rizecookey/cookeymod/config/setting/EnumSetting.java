package net.rizecookey.cookeymod.config.setting;

import net.rizecookey.cookeymod.config.category.Category;

public class EnumSetting<T extends Enum<T> & Named> extends Setting<T> {
    private final boolean forceRestart;
    private final Class<T> enumClass;

    public EnumSetting(String id, Category category, Class<T> enumClass, T defaultValue, boolean forceRestart) {
        super(id, category, defaultValue);
        this.enumClass = enumClass;
        this.forceRestart = forceRestart;
    }

    public EnumSetting(String id, Category category, Class<T> enumClass, T defaultValue) {
        this(id, category, enumClass, defaultValue, false);
    }

    public boolean isForceRestart() {
        return forceRestart;
    }

    public Class<T> getEnumClass() {
        return enumClass;
    }

    @Override
    public <I, O> O accept(SettingVisitor<I, O> visitor, I input) {
        return visitor.visitEnumSetting(this, input);
    }
}
