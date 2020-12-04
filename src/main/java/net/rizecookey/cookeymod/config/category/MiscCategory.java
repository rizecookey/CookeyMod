package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.Option;

public class MiscCategory extends Category {
    public Option<Boolean> showOwnNameInThirdPerson;
    public Option<Boolean> force100PercentRecharge;

    public MiscCategory() {
        showOwnNameInThirdPerson = this.register(new BooleanOption("showOwnNameInThirdPerson", this, false));
        force100PercentRecharge = this.register(new BooleanOption("force100PercentRecharge", this, false));
    }

    @Override
    public String getId() { return "misc"; }
}
