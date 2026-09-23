package net.rizecookey.cookeymod.config.setting;

import net.rizecookey.cookeymod.config.category.Category;

import java.awt.Color;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(String id, Category category, Color defaultValue) {
        super(id, category, defaultValue);
    }

    @Override
    public <I, O> O accept(SettingVisitor<I, O> visitor, I input) {
        return visitor.visitColorSetting(this, input);
    }
}
