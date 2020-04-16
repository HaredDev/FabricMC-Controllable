package com.hared.controllable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hared.controllable.FabricControllable;
import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	
	@Inject(at = @At("RETURN"), method = ("openScreen"))
	public void onGuiOpen(CallbackInfo info) {
		Controllable.getInput().onScreenInit();
	}
	
	@Inject(at = @At("HEAD"), method = ("tick"))
	public void tick(CallbackInfo info) {
		Controllable.getInput().onClientTick();
		Controllable.getInput().onRenderClient();
		FabricControllable.getInstance().getRenderEvents().onClientTick();
		FabricControllable.getInstance().getControllerEvents().onClientTick();
	}
	
}
