package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.config.option.Option;

public class AnimationsCategory extends Category {
    public Option<Boolean> swingAndUseItem;
    public Option<Boolean> enableOldSwing;
    public Option<Double> sneakAnimationSpeed;
    public Option<Boolean> disableCameraBobbing;
    public Option<Boolean> enableToolBlocking;
    public Option<Boolean> showEatingInThirdPerson;
    public Option<Boolean> enableDamageCameraTilt;

    public AnimationsCategory() {
        swingAndUseItem = this.register(new BooleanOption("swingAndUseItem", this, false));
        enableOldSwing = this.register(new BooleanOption("enableOldSwing", this, false));
        sneakAnimationSpeed = this.register(new DoubleSliderOption("sneakAnimationSpeed", this, 1.0, 0.0, 2.0));
        disableCameraBobbing = this.register(new BooleanOption("disableCameraBobbing", this, false));
        enableToolBlocking = this.register(new BooleanOption("enableToolBlocking", this, false));
        showEatingInThirdPerson = this.register(new BooleanOption("showEatingInThirdPerson", this, false));
        enableDamageCameraTilt = this.register(new BooleanOption("enableDamageCameraTilt", this, false));
    }

    @Override
    public String getId() {
        return "animations";
    }
}
