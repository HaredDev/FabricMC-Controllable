package com.hared.controllable.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import com.hared.controllable.FabricControllable;

import net.minecraft.client.util.InputUtil;

@Mixin(InputUtil.class)
public class InputUtilMixin {
	
	@Overwrite
	public static boolean isKeyPressed(long handle, int i) {
		if(i == 340 || i == 344)
			return FabricControllable.getInstance().getHooks().canQuickMove() ? true : GLFW.glfwGetKey(handle, i) == 1;
		else
			return GLFW.glfwGetKey(handle, i) == 1;
	}

}
