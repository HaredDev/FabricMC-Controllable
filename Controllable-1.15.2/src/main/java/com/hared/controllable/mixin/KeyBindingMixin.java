package com.hared.controllable.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hared.controllable.FabricControllable;

import net.minecraft.client.options.KeyBinding;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	@Shadow @Final private String id;
	@Shadow @Final private boolean pressed;
	
	@Overwrite
	public boolean isPressed() {
	      return this.pressed || this.isControllerKeyDown();
	}

	private boolean isControllerKeyDown() {
		switch(id) {
		case "key.use" : return FabricControllable.getInstance().getHooks().isRightClicking();
		case "key.attack" : return FabricControllable.getInstance().getHooks().isLeftClicking();
		case "key.playerlist": return FabricControllable.getInstance().getHooks().canShowPlayerList();
		}
		return false;
	}
	
}
