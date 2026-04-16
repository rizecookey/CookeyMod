package net.rizecookey.cookeymod.config.option;

import com.fasterxml.jackson.databind.JsonNode;
import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.category.Category;

public class ColorOption extends Option<Color> {
    public ColorOption(String id, Category category, Color defaultValue) {
        super(id, category, defaultValue);
    }

    @Override
    public void load(JsonNode object) {
        this.set(Color.ofTransparent(object.asInt()));
    }

    @Override
    public <I, O> O accept(OptionVisitor<I, O> visitor, I input) {
        return visitor.visitColorOption(this, input);
    }

    @Override
    public Object getInConfigFormat() {
        return this.get().getColor();
    }
}
