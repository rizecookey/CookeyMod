package net.rizecookey.cookeymod.config.option;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.config.category.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public abstract class Option<T> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private final Category category;
    private final T defaultValue;
    private T value;

    // use weak hash map to allow garbage collection of owners and thus of outdated listeners
    private final Map<Object, List<ValueChangeListener<? super T>>> valueChangeListeners = new WeakHashMap<>();

    public interface ValueChangeListener<T> {
        void onValueChanged(T oldValue, T newValue);
    }

    protected Option(String id, Category category, T defaultValue) {
        this.id = id;
        this.category = category;
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return this.category.getTranslationKey() + "." + id;
    }

    public Category getCategory() {
        return category;
    }

    public T get() {
        return this.value;
    }

    public Object getInConfigFormat() {
        return this.value;
    }

    public void set(T newValue) {
        T oldValue = this.value;
        this.value = newValue;
        valueChangeListeners.values()
                .forEach(list ->
                        list.forEach(listener -> listener.onValueChanged(oldValue, newValue)));
    }

    public T getDefault() {
        return defaultValue;
    }

    public void load(JsonNode object) {
        this.set(MAPPER.convertValue(object, new TypeReference<>() {
        }));
    }

    public void registerListener(ValueChangeListener<? super T> valueChangeListener, Object owner) {
        valueChangeListeners.computeIfAbsent(owner, _ -> new ArrayList<>())
                .add(valueChangeListener);
    }

    public void unregisterListener(ValueChangeListener<? super T> valueChangeListener, Object owner) {
        if (!valueChangeListeners.containsKey(owner)) {
            return;
        }

        valueChangeListeners.get(owner).remove(valueChangeListener);
    }

    public abstract <I, O> O accept(OptionVisitor<I, O> visitor, I input);

    public static Optional<Component[]> getTooltip(String translationId) {
        List<Component> components = new ArrayList<>();
        String tooltipKey = translationId + ".tooltip.";

        int i = 0;
        while (i != -1) {
            if (Language.getInstance().has(tooltipKey + i)) {
                components.add(Component.translatable(tooltipKey + i));
                i++;
            } else {
                i = -1;
            }
        }
        Component[] array = components.toArray(new Component[0]);

        return Optional.ofNullable(array.length != 0 ? array : null);
    }
}
