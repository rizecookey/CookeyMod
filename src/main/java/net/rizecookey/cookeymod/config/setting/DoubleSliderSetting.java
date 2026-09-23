package net.rizecookey.cookeymod.config.setting;

import net.rizecookey.cookeymod.config.category.Category;

public class DoubleSliderSetting extends Setting<Double> {
    private final double from, to;

    public DoubleSliderSetting(String id, Category category, Double defaultValue, double from, double to) {
        super(id, category, defaultValue);
        this.from = from;
        this.to = to;
    }

    @Override
    public <I, O> O accept(SettingVisitor<I, O> visitor, I input) {
        return visitor.visitDoubleSliderSetting(this, input);
    }

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }
}
