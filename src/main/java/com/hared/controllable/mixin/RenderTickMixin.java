package com.hared.controllable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hared.controllable.FabricControllable;
import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.toast.ToastManager;

@Mixin(ToastManager.class)
public class RenderTickMixin {

	@Inject(at = @At("RETURN"), method = ("draw"))
	public void onTick(CallbackInfo info) {
		FabricControllable.getInstance().getInternalMod().handleButtonInput();
		Controllable.getInput().onRenderRide();
		FabricControllable.getInstance().getRenderEvents().onRenderScreen();
	}
	
}
