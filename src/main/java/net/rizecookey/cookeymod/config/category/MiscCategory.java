package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;

public class MiscCategory extends Category {
    private final BooleanOption showOwnNameInThirdPerson;
    private final BooleanOption showModButton;
    private final BooleanOption fixCooldownDesync;

    public MiscCategory(ModConfig modConfig) {
        super(modConfig);
        showOwnNameInThirdPerson = this.register(new BooleanOption("showOwnNameInThirdPerson", this, false));
        showModButton = this.register(new BooleanOption("showModButton", this, true));
        fixCooldownDesync = this.register(new BooleanOption("fixCooldownDesync", this, true));
    }

    @Override
    public String getId() {
        return "misc";
    }

    public BooleanOption showOwnNameInThirdPerson() {
        return showOwnNameInThirdPerson;
    }

    public BooleanOption showModButton() {
        return showModButton;
    }

    public BooleanOption fixCooldownDesync() {
        return fixCooldownDesync;
    }
}
