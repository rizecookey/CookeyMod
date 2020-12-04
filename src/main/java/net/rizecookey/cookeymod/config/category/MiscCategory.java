package net.rizecookey.cookeymod.config.category;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.Option;

public class MiscCategory extends Category {
    public Option<Boolean> showOwnNameInThirdPerson;
    public Option<Boolean> force100PercentRecharge;
    public Option<Boolean> showModButton;

    public MiscCategory() {
        showOwnNameInThirdPerson = this.register(new BooleanOption("showOwnNameInThirdPerson", this, false));
        force100PercentRecharge = this.register(new BooleanOption("force100PercentRecharge", this, false));
        BooleanOption modButtonOpt = new BooleanOption("showModButton", this, true) {
            @Override
            public AbstractConfigListEntry<?> getConfigEntry() {
                return ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(this.getTranslationKey()), this.get())
                        .setDefaultValue(this.getDefault())
                        .setSaveConsumer(this::set)
                        .requireRestart().build();
            }
        };
        this.showModButton = FabricLoader.getInstance().isModLoaded("modmenu") ? this.register(modButtonOpt) : modButtonOpt;
    }

    @Override
    public String getId() { return "misc"; }
}
