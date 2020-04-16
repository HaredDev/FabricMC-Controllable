package com.hared.controllable.mixin;

import java.util.Locale;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hared.controllable.FabricControllable;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Shadow @Final private MinecraftClient client;
	@Shadow private long lastWindowFocusedTime;
	@Shadow private long lastWorldIconUpdate;
	@Shadow private ShaderEffect shader;
	@Shadow private boolean shadersEnabled;
	
	 @Overwrite
	 public void render(float tickDelta, long startTime, boolean tick) {
		 if (!this.client.isWindowFocused() && this.client.options.pauseOnLostFocus && (!this.client.options.touchscreen || !this.client.mouse.wasRightButtonClicked())) {
	         if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
	            this.client.openPauseMenu(false);
	         }
	      } else {
	         this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
	      }

	      if (!this.client.skipGameRender) {
	         int i = (int)(this.client.mouse.getX() * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth());
	         int j = (int)(this.client.mouse.getY() * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight());
	         int k = this.client.options.maxFps;
	         if (tick && this.client.world != null) {
	            this.client.getProfiler().push("level");
	            int l = Math.min(MinecraftClient.getCurrentFps(), k);
	            l = Math.max(l, 60);
	            long m = Util.getMeasuringTimeNano() - startTime;
	            long n = Math.max((long)(1000000000 / l / 4) - m, 0L);
	            this.renderWorld(tickDelta, Util.getMeasuringTimeNano() + n);
	            if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < Util.getMeasuringTimeMs() - 1000L) {
	               this.lastWorldIconUpdate = Util.getMeasuringTimeMs();
	               if (!this.client.getServer().hasIconFile()) {
	                  this.updateWorldIcon();
	               }
	            }

	            if (GLX.usePostProcess) {
	               this.client.worldRenderer.drawEntityOutlinesFramebuffer();
	               if (this.shader != null && this.shadersEnabled) {
	                  GlStateManager.matrixMode(5890);
	                  GlStateManager.pushMatrix();
	                  GlStateManager.loadIdentity();
	                  this.shader.render(tickDelta);
	                  GlStateManager.popMatrix();
	               }

	               this.client.getFramebuffer().beginWrite(true);
	            }

	            this.client.getProfiler().swap("gui");
	            if (!this.client.options.hudHidden || this.client.currentScreen != null) {
	               GlStateManager.alphaFunc(516, 0.1F);
	               this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
	               this.renderFloatingItem(this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), tickDelta);
	               this.client.inGameHud.render(tickDelta);
	            }

	            this.client.getProfiler().pop();
	         } else {
	            GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
	            GlStateManager.matrixMode(5889);
	            GlStateManager.loadIdentity();
	            GlStateManager.matrixMode(5888);
	            GlStateManager.loadIdentity();
	            this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
	         }

	         CrashReportSection crashReportSection2;
	         CrashReport crashReport2;
	         if (this.client.overlay != null) {
	            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);

	            try {
	               this.client.overlay.render(i, j, this.client.getLastFrameDuration());
	            } catch (Throwable var14) {
	               crashReport2 = CrashReport.create(var14, "Rendering overlay");
	               crashReportSection2 = crashReport2.addElement("Overlay render details");
	               crashReportSection2.add("Overlay name", () -> {
	                  return this.client.overlay.getClass().getCanonicalName();
	               });
	               throw new CrashException(crashReport2);
	            }
	         } else if (this.client.currentScreen != null) {
	            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);

	            try {
	            	float delta = this.client.getLastFrameDuration();
	            	int[] mouse = FabricControllable.getInstance().getHooks().drawScreen(this.client.currentScreen, i, j, delta);
		        	Controllable.getInput().onPreRenderScreen();
		            this.client.currentScreen.render(mouse[0], mouse[1], delta);
		            Controllable.getInput().onPostRenderScreen();
	            } catch (Throwable var13) {
	               crashReport2 = CrashReport.create(var13, "Rendering screen");
	               crashReportSection2 = crashReport2.addElement("Screen render details");
	               crashReportSection2.add("Screen name", () -> {
	                  return this.client.currentScreen.getClass().getCanonicalName();
	               });
	               crashReportSection2.add("Mouse location", () -> {
	                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY());
	               });
	               crashReportSection2.add("Screen size", () -> {
	                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.window.getScaleFactor());
	               });
	               throw new CrashException(crashReport2);
	            }
	         }

	      }
	   }

	@Shadow
	private void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta) {}
	 
	@Shadow 
	private void updateWorldIcon() {}

	@Shadow 
	public abstract void renderWorld(float tickDelta, long startTime);
	
}
