package net.rizecookey.cookeymod.config.category;

import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.ColorOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.config.option.Option;

public class AnimationsCategory extends Category {
    public Option<Boolean> swingAndUseItem;
    public Option<Double> attackCooldownHandOffset;
    public Option<Boolean> enableOldSwing;
    public Option<Color> damageColor;
    public Option<Boolean> showDamageTintOnArmor;
    public Option<Double> sneakAnimationSpeed;
    public Option<Boolean> disableCameraBobbing;

    public AnimationsCategory() {
        swingAndUseItem = this.register(new BooleanOption("swingAndUseItem", this, false));
        attackCooldownHandOffset = this.register(new DoubleSliderOption("attackCooldownHandOffset", this, 0.0, -1.0, 1.0));
        enableOldSwing = this.register(new BooleanOption("enableOldSwing", this, false));
        damageColor = this.register(new ColorOption("damageColor", this, Color.ofRGBA(255, 0, 0, 77), true));
        showDamageTintOnArmor = this.register(new BooleanOption("showDamageTintOnArmor", this, false));
        sneakAnimationSpeed = this.register(new DoubleSliderOption("sneakAnimationSpeed", this, 1.0, 0.0, 2.0));
        disableCameraBobbing = this.register(new BooleanOption("disableCameraBobbing", this, false));
    }

    @Override
    public String getId() {
        return "animations";
    }
}
