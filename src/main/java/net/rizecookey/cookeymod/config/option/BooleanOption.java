package net.rizecookey.cookeymod.config.option;

import net.rizecookey.cookeymod.config.category.Category;

public class BooleanOption extends Option<Boolean> {
    private final boolean forceRestart;

    public BooleanOption(String id, Category category, Boolean defaultValue, boolean forceRestart) {
        super(id, category, defaultValue);
        this.forceRestart = forceRestart;
    }

    public BooleanOption(String id, Category category, Boolean defaultValue) {
        this(id, category, defaultValue, false);
    }

    @Override
    public <I, O> O accept(OptionVisitor<I, O> visitor, I input) {
        return visitor.visitBooleanOption(this, input);
    }

    public boolean isForceRestart() {
        return forceRestart;
    }
}
