package net.rizecookey.cookeymod.config.option;

import net.minecraft.network.chat.Component;

public enum FirstPersonDamageRenderSelection implements Named {
    NONE("none", "options.cookeymod.hudRendering.showDamageTintInFirstPerson.none", false),
    HANDS_ONLY("hands_only", "options.cookeymod.hudRendering.showDamageTintInFirstPerson.hands_only", true),
    HANDS_AND_ITEMS("hands_and_items", "options.cookeymod.hudRendering.showDamageTintInFirstPerson.hands_and_items", true);

    final String internalName;
    final String translationKey;
    final boolean onHand;

    FirstPersonDamageRenderSelection(String internalName, String translationKey, boolean onHand) {
        this.internalName = internalName;
        this.translationKey = translationKey;
        this.onHand = onHand;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public boolean isOnHand() {
        return onHand;
    }
}
