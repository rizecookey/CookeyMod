package net.rizecookey.cookeymod.config.category;

import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.Option;

public class MiscCategory extends Category {
    public Option<Boolean> showOwnNameInThirdPerson;
    public Option<Boolean> showModButton;

    public MiscCategory() {
        showOwnNameInThirdPerson = this.register(new BooleanOption("showOwnNameInThirdPerson", this, false));
        BooleanOption modButtonOpt = new BooleanOption("showModButton", this, true);
        this.showModButton = FabricLoader.getInstance().isModLoaded("modmenu") ? this.register(modButtonOpt) : modButtonOpt;
    }

    @Override
    public String getId() { return "misc"; }
}
