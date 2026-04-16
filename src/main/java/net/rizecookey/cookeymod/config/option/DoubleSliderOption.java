package net.rizecookey.cookeymod.config.option;

import com.fasterxml.jackson.databind.JsonNode;
import net.rizecookey.cookeymod.config.category.Category;

public class DoubleSliderOption extends Option<Double> {
    private final double from, to;

    public DoubleSliderOption(String id, Category category, Double defaultValue, double from, double to) {
        super(id, category, defaultValue);
        this.from = from;
        this.to = to;
    }

    @Override
    public void load(JsonNode object) {
        this.set(object.asDouble());
    }

    @Override
    public <I, O> O accept(OptionVisitor<I, O> visitor, I input) {
        return visitor.visitDoubleSliderOption(this, input);
    }

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }
}
