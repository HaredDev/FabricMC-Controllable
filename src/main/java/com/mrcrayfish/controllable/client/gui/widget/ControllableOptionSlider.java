package com.mrcrayfish.controllable.client.gui.widget;

import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.gui.widget.GameOptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

/**
 * Author: MrCrayfish
 */
public class ControllableOptionSlider extends GameOptionSliderWidget
{
    private final DoubleOption option;

    public ControllableOptionSlider(GameOptions settings, int x, int y, int width, int height, DoubleOption option)
    {
        super(settings, x, y, width, height, option);
        this.option = option;
    }

    @Override
    protected void applyValue()
    {
        this.option.set(this.options, this.option.method_18616(this.value));
        Controllable.getOptions().saveOptions();
    }
}
