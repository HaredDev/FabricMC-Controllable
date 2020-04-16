package com.hared.controllable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;

@Mixin(KeyboardInput.class)
public class InputUpdateMixin {

	@Inject(at = @At("RETURN"), method = ("tick"))
	public void onInput(CallbackInfo info) {
		Controllable.getInput().onInputUpdate((Input)(Object)this);
	}
	
}
