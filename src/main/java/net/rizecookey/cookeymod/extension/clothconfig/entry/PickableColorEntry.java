package net.rizecookey.cookeymod.extension.clothconfig.entry;

import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.math.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class PickableColorEntry extends ColorEntry {
    private final Map<ColorComponent, ColorComponentSlider> colorComponentSliders;

    public PickableColorEntry(Component name, Color value, Component resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(name, value.getColor(), resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, requiresRestart);
        this.withAlpha();
        this.textFieldWidget.cookeyMod$registerOnChange(string -> updateSlidersToFieldValue(this.getColorValue(string)));
        colorComponentSliders = new HashMap<>();

        for (ColorComponent colorComponent : ColorComponent.values()) {
            colorComponentSliders.put(colorComponent, new ColorComponentSlider(0, 0, 0, textFieldWidget.getHeight(), colorComponent, colorComponent.getExtractor().apply(value)));
        }

        List<AbstractWidget> widgets = new ArrayList<>();
        widgets.addAll(this.widgets);
        widgets.addAll(colorComponentSliders.values());
        this.widgets = widgets;
    }

    @Override
    public int getItemHeight() {
        return 2 * super.getItemHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        int slidersStartX = x + 20;
        int slidersEndX = x + entryWidth - 20;
        int sliderPadding = 5;
        int sliderWidth = (slidersEndX - slidersStartX - (colorComponentSliders.size() - 1) * sliderPadding) / colorComponentSliders.size();

        ColorComponent[] components = ColorComponent.values();
        for (int i = 0; i < components.length; i++) {
            ColorComponentSlider slider = colorComponentSliders.get(components[i]);
            slider.setX(slidersStartX + i * (sliderWidth + sliderPadding));
            slider.setY(y + super.getItemHeight());
            slider.setWidth(sliderWidth);
            slider.render(graphics, mouseX, mouseY, delta);
        }
    }

    private void updateSlidersToFieldValue(ColorValue colorValue) {
        if (colorValue.hasError()) {
            return;
        }
        Color color = Color.ofTransparent(colorValue.getColor());

        for (ColorComponent component : ColorComponent.values()) {
            ColorComponentSlider slider = colorComponentSliders.get(component);
            slider.setValueSilently(component.getExtractor().apply(color));
        }
    }

    private void updateFieldToSliderValues() {
        int colorInt = 0;
        for (ColorComponent component : ColorComponent.values()) {
            ColorComponentSlider slider = colorComponentSliders.get(component);
            colorInt |= slider.getValue() << component.getPositionInColorInt();
        }

        this.setValue(colorInt);
    }

    public class ColorComponentSlider extends AbstractSliderButton {
        private static final int COMPONENT_MIN = 0;
        private static final int COMPONENT_MAX = 255;

        private final ColorComponent colorComponent;

        public ColorComponentSlider(int x, int y, int width, int height, ColorComponent colorComponent, int value) {
            super(x, y, width, height, Component.empty(), intToSlider(value));
            this.colorComponent = colorComponent;
            this.setMessage(valueToText(value));
        }

        private static int sliderToInt(double value) {
            return COMPONENT_MIN + (int) Math.floor(value * COMPONENT_MAX);
        }

        private static double intToSlider(int value) {
            return (double) (value - COMPONENT_MIN) / (double) (COMPONENT_MAX - COMPONENT_MIN);
        }

        public int getValue() {
            return sliderToInt(this.value);
        }

        protected void setValueSilently(int value) {
            this.value = Math.clamp(intToSlider(value), 0, 1);
            this.updateMessage();
        }

        private Component valueToText(int value) {
            return Component.translatable(colorComponent.getTranslationKey(), value);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(valueToText(getValue()));
        }

        @Override
        protected void applyValue() {
            PickableColorEntry.this.updateFieldToSliderValues();
        }
    }

    public enum ColorComponent {
        RED("options.cookeymod.generic.colorpicker.red", Color::getRed, 16),
        GREEN("options.cookeymod.generic.colorpicker.green", Color::getGreen, 8),
        BLUE("options.cookeymod.generic.colorpicker.blue", Color::getBlue, 0),
        OPACITY("options.cookeymod.generic.colorpicker.opacity", Color::getAlpha, 24);

        private final String translationKey;
        private final Function<Color, Integer> extractor;
        private final int positionInColorInt;

        ColorComponent(String translationKey, Function<Color, Integer> extractor, int positionInColorInt) {
            this.translationKey = translationKey;
            this.extractor = extractor;
            this.positionInColorInt = positionInColorInt;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public Function<Color, Integer> getExtractor() {
            return this.extractor;
        }

        public int getPositionInColorInt() {
            return positionInColorInt;
        }
    }
}
