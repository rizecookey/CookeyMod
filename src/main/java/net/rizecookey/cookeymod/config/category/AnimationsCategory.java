package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.BooleanSetting;
import net.rizecookey.cookeymod.config.setting.DoubleSliderSetting;

public class AnimationsCategory extends Category {
    private final BooleanSetting swingAndUseItem;
    private final DoubleSliderSetting sneakAnimationSpeed;
    private final BooleanSetting disableCameraBobbing;
    private final BooleanSetting enableToolBlocking;
    private final BooleanSetting showEatingInThirdPerson;

    public AnimationsCategory(ModConfig modConfig) {
        super(modConfig);
        swingAndUseItem = this.register(new BooleanSetting("swingAndUseItem", this, false));
        sneakAnimationSpeed = this.register(new DoubleSliderSetting("sneakAnimationSpeed", this, 1.0, 0.0, 2.0));
        disableCameraBobbing = this.register(new BooleanSetting("disableCameraBobbing", this, false));
        enableToolBlocking = this.register(new BooleanSetting("enableToolBlocking", this, false));
        showEatingInThirdPerson = this.register(new BooleanSetting("showEatingInThirdPerson", this, false));
    }

    @Override
    public String getId() {
        return "animations";
    }

    public BooleanSetting swingAndUseItem() {
        return swingAndUseItem;
    }

    public DoubleSliderSetting sneakAnimationSpeed() {
        return sneakAnimationSpeed;
    }

    public BooleanSetting disableCameraBobbing() {
        return disableCameraBobbing;
    }

    public BooleanSetting enableToolBlocking() {
        return enableToolBlocking;
    }

    public BooleanSetting showEatingInThirdPerson() {
        return showEatingInThirdPerson;
    }
}
