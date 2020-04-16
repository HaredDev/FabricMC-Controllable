package com.mrcrayfish.controllable.client.settings;

import com.mrcrayfish.controllable.client.gui.widget.ControllableOptionSlider;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ControllableSliderPercentageOption extends DoubleOption
{
    public ControllableSliderPercentageOption(String title, double minValue, double maxValue, float stepSize, Function<GameOptions, Double> getter, BiConsumer<GameOptions, Double> setter, BiFunction<GameOptions, DoubleOption, String> displayNameGetter)
    {
        super(title, minValue, maxValue, stepSize, getter, setter, displayNameGetter);
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions settings, int x, int y, int width)
    {
        return new ControllableOptionSlider(settings, x, y, width, 20, this);
    }
}
