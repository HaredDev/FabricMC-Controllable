package com.mrcrayfish.controllable.client.settings;

import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.util.StringIdentifiable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ControllableEnumOption<T extends Enum<T> & StringIdentifiable> extends Option
{
    private Class<T> enumClass;
    private int ordinal = 0;
    private Function<GameOptions, T> getter;
    private BiConsumer<GameOptions, T> setter;
    private BiFunction<GameOptions, ControllableEnumOption<T>, String> displayNameGetter;

    public ControllableEnumOption(String title, Class<T> enumClass, Function<GameOptions, T> getter, BiConsumer<GameOptions, T> setter, BiFunction<GameOptions, ControllableEnumOption<T>, String> displayNameGetter)
    {
        super(title);
        this.enumClass = enumClass;
        this.getter = getter;
        this.setter = setter;
        this.displayNameGetter = displayNameGetter;
    }

    private void nextEnum(GameOptions options)
    {
        this.set(options, this.getEnum(++ordinal));
        Controllable.getOptions().saveOptions();
    }

    public void set(GameOptions options, T t)
    {
        this.setter.accept(options, t);
        this.ordinal = t.ordinal();
    }

    public T get(GameOptions options)
    {
        T t = this.getter.apply(options);
        this.ordinal = t.ordinal();
        return t;
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width)
    {
        return new OptionButtonWidget(x, y, width, 20, this, this.getTitle(options), (button) -> {
            this.nextEnum(options);
            button.setMessage(this.getTitle(options));
        });
    }

    public String getTitle(GameOptions options)
    {
        return this.getDisplayPrefix() + this.displayNameGetter.apply(options, this);
    }

    private T getEnum(int ordinal)
    {
        T[] e = enumClass.getEnumConstants();
        if(ordinal >= e.length) ordinal = 0;
        return e[ordinal];
    }
}
