package net.rizecookey.cookeymod.config.setting;

import net.rizecookey.cookeymod.config.category.Category;

public class BooleanSetting extends Setting<Boolean> {
    private final boolean forceRestart;

    public BooleanSetting(String id, Category category, Boolean defaultValue, boolean forceRestart) {
        super(id, category, defaultValue);
        this.forceRestart = forceRestart;
    }

    public BooleanSetting(String id, Category category, Boolean defaultValue) {
        this(id, category, defaultValue, false);
    }

    @Override
    public <I, O> O accept(SettingVisitor<I, O> visitor, I input) {
        return visitor.visitBooleanSetting(this, input);
    }

    public boolean isForceRestart() {
        return forceRestart;
    }
}
