package com.mrcrayfish.controllable.client.settings;

import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.GameOptions;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class ControllableBooleanOption extends BooleanOption
{
    public ControllableBooleanOption(String title, Predicate<GameOptions> getter, BiConsumer<GameOptions, Boolean> setter)
    {
        super(title, getter, setter);
    }

    @Override
    public void set(GameOptions settings)
    {
        this.set(settings, String.valueOf(!this.get(settings)));
        Controllable.getOptions().saveOptions();
    }
    
}
