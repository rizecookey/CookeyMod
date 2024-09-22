package net.rizecookey.cookeymod.config;

import net.rizecookey.cookeymod.config.option.ArmorDamageRenderSelection;

import java.util.HashMap;
import java.util.Map;

public final class ConfigUpdater {
    private ConfigUpdater() {
    }

    @SuppressWarnings("unchecked")
    public static boolean update(Map<String, Object> map, long from) {
        boolean modified = false;
        String animationsKey = "animations";
        String hudRenderingKey = "hudRendering";
        if (from < 2) {
            Map<String, Object> animationsMap = map.containsKey(animationsKey)
                    && map.get(animationsKey) instanceof Map
                    ? (Map<String, Object>) map.get(animationsKey) : new HashMap<>();
            Map<String, Object> hudRenderingMap = map.containsKey(hudRenderingKey)
                    && map.get(hudRenderingKey) instanceof Map
                    ? (Map<String, Object>) map.get(hudRenderingKey) : new HashMap<>();
            String[] copyToHud = new String[]{
                    "disableEffectBasedFovChange",
                    "damageColor",
                    "attackCooldownHandOffset",
                    "showDamageTintOnArmor",
                    "onlyShowShieldWhenBlocking"
            };
            if (animationsMap != null && hudRenderingMap != null) {
                for (String key : copyToHud) {
                    if (animationsMap.containsKey(key)) {
                        hudRenderingMap.put(key, animationsMap.get(key));
                        animationsMap.remove(key);
                    }
                }
            }
            map.put(animationsKey, animationsMap);
            map.put(hudRenderingKey, hudRenderingMap);
            modified = true;
        }

        if (from < 3) {
            String armorDamageTintKey = "showDamageTintOnArmor";
            Map<String, Object> hudRenderingMap = (Map<String, Object>) map.getOrDefault(hudRenderingKey, new HashMap<>());
            if (hudRenderingMap.containsKey(armorDamageTintKey)) {
                Object old = hudRenderingMap.get(armorDamageTintKey);
                boolean oldValue = old instanceof Boolean && (boolean) old;
                hudRenderingMap.put(armorDamageTintKey, oldValue ? ArmorDamageRenderSelection.ARMOR_AND_TRIM.getInternalName() : ArmorDamageRenderSelection.NONE.getInternalName());
            }
        }
        return modified;
    }
}
