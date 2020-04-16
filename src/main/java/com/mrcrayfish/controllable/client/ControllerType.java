package com.mrcrayfish.controllable.client;

import net.minecraft.util.StringIdentifiable;

/**
 * Author: MrCrayfish
 */
public enum ControllerType implements StringIdentifiable
{
    DEFAULT("default"),
    PLAYSTATION("playstation"),
    XBOX("xbox");

    String name;

    ControllerType(String name)
    {
        this.name = name;
    }
    public static ControllerType byName(String name)
    {
        for(ControllerType controllerType : values())
        {
            if(controllerType.name.equals(name))
            {
                return controllerType;
            }
        }
        return DEFAULT;
    }

	@Override
	public String asString() {
		return this.name;
	}
}
