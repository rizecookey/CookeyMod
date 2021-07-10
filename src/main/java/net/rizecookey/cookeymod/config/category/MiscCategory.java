package net.rizecookey.cookeymod.config.category;

import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.Option;

public class MiscCategory extends Category {
    public Option<Boolean> showOwnNameInThirdPerson;
    public Option<Boolean> force100PercentRecharge;
    public Option<Boolean> showModButton;
    public Option<Boolean> fixLocalPlayerHandling;
    //public Option<Boolean> fixCooldownDesync;

    public MiscCategory(ModConfig modConfig) {
        super(modConfig);
        showOwnNameInThirdPerson = this.register(new BooleanOption("showOwnNameInThirdPerson", this, false));
        force100PercentRecharge = this.register(new BooleanOption("force100PercentRecharge", this, false));
        BooleanOption modButtonOpt = new BooleanOption("showModButton", this, true);
        showModButton = FabricLoader.getInstance().isModLoaded("modmenu") ? this.register(modButtonOpt) : modButtonOpt;
        fixLocalPlayerHandling = this.register(new BooleanOption("fixLocalPlayerHandling", this, true));
        //fixCooldownDesync = this.register(new BooleanOption("fixCooldownDesync", this, true));
    }

    @Override
    public String getId() { return "misc"; }
}
