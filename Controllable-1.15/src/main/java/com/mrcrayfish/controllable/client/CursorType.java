package com.mrcrayfish.controllable.client;

import net.minecraft.util.StringIdentifiable;

/**
 * Author: MrCrayfish
 */
public enum CursorType implements StringIdentifiable
{
    LIGHT("light"),
    DARK("dark"),
    CONSOLE("console");

    String id;

    CursorType(String id)
    {
        this.id = id;
    }

    public static CursorType byId(String id)
    {
        for(CursorType cursorType : values())
        {
            if(cursorType.id.equals(id))
            {
                return cursorType;
            }
        }
        return LIGHT;
    }

	@Override
	public String asString() {
		return this.id;
	}
}
