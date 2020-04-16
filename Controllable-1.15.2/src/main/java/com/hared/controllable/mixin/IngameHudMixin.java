package com.hared.controllable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hared.controllable.FabricControllable;

import net.minecraft.client.gui.hud.InGameHud;
@Mixin(InGameHud.class)
public class IngameHudMixin {
	
	@Inject(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.enableBlend()V"))
	public void onRenderHeldItemTooltip(CallbackInfo info) {
		FabricControllable.getInstance().getHooks().applyHotbarOffset();
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.translatef(FFF)V", ordinal = 0))
	public void onRenderOverlayMessage(CallbackInfo info) {
		FabricControllable.getInstance().getHooks().applyHotbarOffset();
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.push(Ljava/lang/String;)V", ordinal = 4))
	public void onRenderChatHud(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPreRenderOverlay();
	}
	
	@Inject(at = @At("HEAD"), method = ("renderHotbar"))
	public void onRenderHotbar(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPreRenderOverlay();
	}
	
	@Inject(at = @At("HEAD"), method = ("renderMountHealth"))
	public void onRenderMountHealth(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPreRenderOverlay();
	}
	
	@Inject(at = @At("HEAD"), method = ("renderStatusBars"))
	public void onRenderStatusBars(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPreRenderOverlay();
	}
	
	@Inject(at = @At("HEAD"), method = ("renderExperienceBar"))
	public void onRenderExperienceBar(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPreRenderOverlay();
	}
	
	@Inject(at = @At("HEAD"), method = ("renderMountJumpBar"))
	public void onRenderMountJumpBar(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPreRenderOverlay();
	}
	
	@Inject(at = @At("RETURN"), method = ("renderHotbar"))
	public void afterRenderHotbar(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPostRenderOverlay();
	}
	
	@Inject(at = @At("RETURN"), method = ("renderMountHealth"))
	public void afterRenderMountHealth(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPostRenderOverlay();
	}
	
	@Inject(at = @At("RETURN"), method = ("renderStatusBars"))
	public void afterRenderStatusBars(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPostRenderOverlay();
	}
	
	@Inject(at = @At("RETURN"), method = ("renderExperienceBar"))
	public void afterRenderExperienceBar(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPostRenderOverlay();
	}
	
	@Inject(at = @At("RETURN"), method = ("renderMountJumpBar"))
	public void afterRenderMountJumpBar(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPostRenderOverlay();
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.pop()V", ordinal = 3))
	public void afterRenderChatHud(CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onPostRenderOverlay();
	}
	
}
