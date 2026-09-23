package net.rizecookey.cookeymod.config.setting;

import tools.jackson.databind.JsonNode;
import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.category.Category;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(String id, Category category, Color defaultValue) {
        super(id, category, defaultValue);
    }

    @Override
    public void load(JsonNode object) {
        this.set(Color.ofTransparent(object.asInt()));
    }

    @Override
    public <I, O> O accept(SettingVisitor<I, O> visitor, I input) {
        return visitor.visitColorSetting(this, input);
    }

    @Override
    public Object getInConfigFormat() {
        return this.get().getColor();
    }
}
