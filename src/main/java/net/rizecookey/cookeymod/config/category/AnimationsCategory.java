package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.config.option.Option;

public class AnimationsCategory extends Category {
    public final Option<Boolean> swingAndUseItem;
    public final Option<Double> sneakAnimationSpeed;
    public final Option<Boolean> disableCameraBobbing;
    public final Option<Boolean> enableToolBlocking;
    public final Option<Boolean> showEatingInThirdPerson;

    public AnimationsCategory(ModConfig modConfig) {
        super(modConfig);
        swingAndUseItem = this.register(new BooleanOption("swingAndUseItem", this, false));
        sneakAnimationSpeed = this.register(new DoubleSliderOption("sneakAnimationSpeed", this, 1.0, 0.0, 2.0));
        disableCameraBobbing = this.register(new BooleanOption("disableCameraBobbing", this, false));
        enableToolBlocking = this.register(new BooleanOption("enableToolBlocking", this, false));
        showEatingInThirdPerson = this.register(new BooleanOption("showEatingInThirdPerson", this, false));
    }

    @Override
    public String getId() {
        return "animations";
    }
}
