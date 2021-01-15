package net.rizecookey.cookeymod.config.category;

import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.ColorOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.config.option.Option;
import net.rizecookey.cookeymod.event.OverlayReloadListener;

public class HudRenderingCategory extends Category {
    public Option<Double> attackCooldownHandOffset;
    public Option<Color> damageColor;
    public Option<Boolean> showDamageTintOnArmor;
    public Option<Boolean> onlyShowShieldWhenBlocking;
    public Option<Boolean> disableEffectBasedFovChange;
    public Option<Boolean> alternativeBobbing;

    public HudRenderingCategory() {
        attackCooldownHandOffset = this.register(new DoubleSliderOption("attackCooldownHandOffset", this, 0.0, -1.0, 1.0));
        damageColor = this.register(new ColorOption("damageColor", this, Color.ofRGBA(255, 0, 0, 77), true) {
            @Override
            public void set(Color value) {
                super.set(value);
                OverlayReloadListener.callEvent();
            }
        });
        showDamageTintOnArmor = this.register(new BooleanOption("showDamageTintOnArmor", this, false));
        onlyShowShieldWhenBlocking = this.register(new BooleanOption("onlyShowShieldWhenBlocking", this, false));
        disableEffectBasedFovChange = this.register(new BooleanOption("disableEffectBasedFovChange", this, false));
        alternativeBobbing = this.register(new BooleanOption("alternativeBobbing", this, false));
    }

    @Override
    public String getId() {
        return "hudRendering";
    }
}
