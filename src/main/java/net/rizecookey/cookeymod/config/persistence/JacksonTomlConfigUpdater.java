package net.rizecookey.cookeymod.config.persistence;

import net.rizecookey.cookeymod.config.setting.ArmorDamageRenderSelection;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import static net.rizecookey.cookeymod.config.persistence.JacksonTomlConfigSerialization.MAPPER;

public final class JacksonTomlConfigUpdater {
    private JacksonTomlConfigUpdater() {
    }

    public static boolean update(ObjectNode config, long from) {
        boolean modified = false;
        String animationsKey = "animations";
        String hudRenderingKey = "hudRendering";
        if (from < 2) {
            ObjectNode animationsNode = config.has(animationsKey)
                    && config.get(animationsKey).isObject()
                    ? (ObjectNode) config.get(animationsKey) : MAPPER.createObjectNode();
            ObjectNode hudRenderingNode = config.has(hudRenderingKey)
                    && config.get(hudRenderingKey).isObject()
                    ? (ObjectNode) config.get(hudRenderingKey) : MAPPER.createObjectNode();
            String[] copyToHud = new String[]{
                    "disableEffectBasedFovChange",
                    "damageColor",
                    "attackCooldownHandOffset",
                    "showDamageTintOnArmor",
                    "onlyShowShieldWhenBlocking"
            };

            for (String key : copyToHud) {
                if (animationsNode.has(key)) {
                    hudRenderingNode.set(key, animationsNode.get(key));
                    animationsNode.remove(key);
                }
            }
            config.set(animationsKey, animationsNode);
            config.set(hudRenderingKey, hudRenderingNode);
            modified = true;
        }

        if (from < 3) {
            String armorDamageTintKey = "showDamageTintOnArmor";
            ObjectNode hudRenderingNode = config.has(hudRenderingKey) ? (ObjectNode) config.get(hudRenderingKey) : MAPPER.createObjectNode();
            if (hudRenderingNode.has(armorDamageTintKey) && hudRenderingNode.isObject()) {
                boolean old = hudRenderingNode.get(armorDamageTintKey).asBoolean();
                hudRenderingNode.set(armorDamageTintKey, old
                        ? StringNode.valueOf(ArmorDamageRenderSelection.ARMOR_AND_TRIM.getInternalName())
                        : StringNode.valueOf(ArmorDamageRenderSelection.NONE.getInternalName()));
            }
        }
        return modified;
    }
}
