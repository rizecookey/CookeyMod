package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.BooleanSetting;

public class MiscCategory extends Category {
    private final BooleanSetting showOwnNameInThirdPerson;
    private final BooleanSetting showModButton;
    private final BooleanSetting fixCooldownDesync;

    public MiscCategory(ModConfig modConfig) {
        super(modConfig);
        showOwnNameInThirdPerson = this.register(new BooleanSetting("showOwnNameInThirdPerson", this, false));
        showModButton = this.register(new BooleanSetting("showModButton", this, true));
        fixCooldownDesync = this.register(new BooleanSetting("fixCooldownDesync", this, true));
    }

    @Override
    public String getId() {
        return "misc";
    }

    public BooleanSetting showOwnNameInThirdPerson() {
        return showOwnNameInThirdPerson;
    }

    public BooleanSetting showModButton() {
        return showModButton;
    }

    public BooleanSetting fixCooldownDesync() {
        return fixCooldownDesync;
    }
}
