package net.rizecookey.cookeymod.config.category;

import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;

public class MiscCategory extends Category {
    private final BooleanOption showOwnNameInThirdPerson;
    private final BooleanOption showModButton;
    private final BooleanOption fixLocalPlayerHandling;
    private final BooleanOption fixCooldownDesync;

    public MiscCategory(ModConfig modConfig) {
        super(modConfig);
        showOwnNameInThirdPerson = this.register(new BooleanOption("showOwnNameInThirdPerson", this, false));
        BooleanOption modButtonOpt = new BooleanOption("showModButton", this, true);
        this.showModButton = FabricLoader.getInstance().isModLoaded("modmenu") ? this.register(modButtonOpt) : modButtonOpt;
        fixLocalPlayerHandling = this.register(new BooleanOption("fixLocalPlayerHandling", this, true));
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

    public BooleanOption fixLocalPlayerHandling() {
        return fixLocalPlayerHandling;
    }

    public BooleanOption fixCooldownDesync() {
        return fixCooldownDesync;
    }
}
