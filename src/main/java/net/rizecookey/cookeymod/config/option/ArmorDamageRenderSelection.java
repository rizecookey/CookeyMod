package net.rizecookey.cookeymod.config.option;

import net.minecraft.network.chat.Component;

public enum ArmorDamageRenderSelection implements Named {
    NONE("none", "options.cookeymod.hudRendering.showDamageTintOnArmor.none", false),
    ARMOR_ONLY("armor_only", "options.cookeymod.hudRendering.showDamageTintOnArmor.armor_only", true),
    ARMOR_AND_TRIM("armor_and_trim", "options.cookeymod.hudRendering.showDamageTintOnArmor.armor_and_trim", true);

    final String internalName;
    final String translationKey;
    final boolean onRegularArmor;

    ArmorDamageRenderSelection(String internalName, String translationKey, boolean onRegularArmor) {
        this.internalName = internalName;
        this.translationKey = translationKey;
        this.onRegularArmor = onRegularArmor;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public boolean isOnRegularArmor() {
        return onRegularArmor;
    }
}
