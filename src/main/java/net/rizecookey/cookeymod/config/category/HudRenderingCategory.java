package net.rizecookey.cookeymod.config.category;

import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.ColorOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.event.OverlayReloadListener;

public class HudRenderingCategory extends Category {
    private final DoubleSliderOption attackCooldownHandOffset;
    private final ColorOption damageColor;
    private final BooleanOption showDamageTintOnArmor;
    private final BooleanOption onlyShowShieldWhenBlocking;
    private final BooleanOption disableEffectBasedFovChange;
    private final BooleanOption alternativeBobbing;
    private final BooleanOption showHandWhenInvisible;

    public HudRenderingCategory(ModConfig modConfig) {
        super(modConfig);
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
        showHandWhenInvisible = this.register(new BooleanOption("showHandWhenInvisible", this, false));
    }

    @Override
    public String getId() {
        return "hudRendering";
    }

    public DoubleSliderOption attackCooldownHandOffset() {
        return attackCooldownHandOffset;
    }

    public ColorOption damageColor() {
        return damageColor;
    }

    public BooleanOption showDamageTintOnArmor() {
        return showDamageTintOnArmor;
    }

    public BooleanOption onlyShowShieldWhenBlocking() {
        return onlyShowShieldWhenBlocking;
    }

    public BooleanOption disableEffectBasedFovChange() {
        return disableEffectBasedFovChange;
    }

    public BooleanOption alternativeBobbing() {
        return alternativeBobbing;
    }

    public BooleanOption showHandWhenInvisible() {
        return showHandWhenInvisible;
    }
}
