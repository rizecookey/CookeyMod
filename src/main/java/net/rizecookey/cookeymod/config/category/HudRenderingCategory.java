package net.rizecookey.cookeymod.config.category;

import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.ArmorDamageRenderSelection;
import net.rizecookey.cookeymod.config.setting.BooleanSetting;
import net.rizecookey.cookeymod.config.setting.ColorSetting;
import net.rizecookey.cookeymod.config.setting.DoubleSliderSetting;
import net.rizecookey.cookeymod.config.setting.EnumSetting;
import net.rizecookey.cookeymod.config.setting.FirstPersonDamageRenderSelection;

public class HudRenderingCategory extends Category {
    private final DoubleSliderSetting attackCooldownHandOffset;
    private final ColorSetting damageColor;
    private final EnumSetting<ArmorDamageRenderSelection> showDamageTintOnArmor;
    private final BooleanSetting showDamageTintOnHeldItems, showDamageTintOnCape;
    private final EnumSetting<FirstPersonDamageRenderSelection> showDamageTintInFirstPerson;
    private final BooleanSetting onlyShowShieldWhenBlocking;
    private final BooleanSetting disableEffectBasedFovChange;
    private final BooleanSetting alternativeBobbing;
    private final BooleanSetting showHandWhenInvisible;
    private final DoubleSliderSetting invisibilityHandOpacity;

    public HudRenderingCategory(ModConfig modConfig) {
        super(modConfig);
        attackCooldownHandOffset = this.register(new DoubleSliderSetting("attackCooldownHandOffset", this, 0.0, -1.0, 1.0));

        damageColor = this.register(new ColorSetting("damageColor", this, Color.ofRGBA(255, 0, 0, 77)));
        showDamageTintOnArmor = this.register(new EnumSetting<>("showDamageTintOnArmor", this, ArmorDamageRenderSelection.class, ArmorDamageRenderSelection.NONE));
        showDamageTintOnHeldItems = this.register(new BooleanSetting("showDamageTintOnHeldItems", this, false));
        showDamageTintOnCape = this.register(new BooleanSetting("showDamageTintOnCape", this, false));
        showDamageTintInFirstPerson = this.register(new EnumSetting<>("showDamageTintInFirstPerson", this, FirstPersonDamageRenderSelection.class, FirstPersonDamageRenderSelection.NONE));

        onlyShowShieldWhenBlocking = this.register(new BooleanSetting("onlyShowShieldWhenBlocking", this, false));
        disableEffectBasedFovChange = this.register(new BooleanSetting("disableEffectBasedFovChange", this, false));
        alternativeBobbing = this.register(new BooleanSetting("alternativeBobbing", this, false));
        showHandWhenInvisible = this.register(new BooleanSetting("showHandWhenInvisible", this, false));
        invisibilityHandOpacity = this.register(new DoubleSliderSetting("firstPersonHandOpacityOnInvisibility", this, 0.3, 0.0, 1.0));
    }

    @Override
    public String getId() {
        return "hudRendering";
    }

    public DoubleSliderSetting attackCooldownHandOffset() {
        return attackCooldownHandOffset;
    }

    public ColorSetting damageColor() {
        return damageColor;
    }

    public EnumSetting<ArmorDamageRenderSelection> showDamageTintOnArmor() {
        return showDamageTintOnArmor;
    }

    public BooleanSetting showDamageTintOnHeldItems() {
        return showDamageTintOnHeldItems;
    }

    public BooleanSetting showDamageTintOnCape() {
        return showDamageTintOnCape;
    }

    public EnumSetting<FirstPersonDamageRenderSelection> showDamageTintInFirstPerson() {
        return showDamageTintInFirstPerson;
    }

    public BooleanSetting onlyShowShieldWhenBlocking() {
        return onlyShowShieldWhenBlocking;
    }

    public BooleanSetting disableEffectBasedFovChange() {
        return disableEffectBasedFovChange;
    }

    public BooleanSetting alternativeBobbing() {
        return alternativeBobbing;
    }

    public BooleanSetting showHandWhenInvisible() {
        return showHandWhenInvisible;
    }

    public DoubleSliderSetting invisibilityHandOpacity() {
        return invisibilityHandOpacity;
    }
}
