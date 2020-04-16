package com.hared.controllable.mixin;

import java.util.Locale;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hared.controllable.FabricControllable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
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
	         int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
	         int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
	         MatrixStack matrixStack = new MatrixStack();
	         RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
	         if (tick && this.client.world != null) {
	            this.client.getProfiler().push("level");
	            this.renderWorld(tickDelta, startTime, matrixStack);
	            if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < Util.getMeasuringTimeMs() - 1000L) {
	               this.lastWorldIconUpdate = Util.getMeasuringTimeMs();
	               if (!this.client.getServer().hasIconFile()) {
	                  this.updateWorldIcon();
	               }
	            }

	            this.client.worldRenderer.drawEntityOutlinesFramebuffer();
	            if (this.shader != null && this.shadersEnabled) {
	               RenderSystem.disableBlend();
	               RenderSystem.disableDepthTest();
	               RenderSystem.disableAlphaTest();
	               RenderSystem.enableTexture();
	               RenderSystem.matrixMode(5890);
	               RenderSystem.pushMatrix();
	               RenderSystem.loadIdentity();
	               this.shader.render(tickDelta);
	               RenderSystem.popMatrix();
	            }

	            this.client.getFramebuffer().beginWrite(true);
	         }

	         Window window = this.client.getWindow();
	         RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
	         RenderSystem.matrixMode(5889);
	         RenderSystem.loadIdentity();
	         RenderSystem.ortho(0.0D, (double)window.getFramebufferWidth() / window.getScaleFactor(), (double)window.getFramebufferHeight() / window.getScaleFactor(), 0.0D, 1000.0D, 3000.0D);
	         RenderSystem.matrixMode(5888);
	         RenderSystem.loadIdentity();
	         RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
	         DiffuseLighting.enableGuiDepthLighting();
	         if (tick && this.client.world != null) {
	            this.client.getProfiler().swap("gui");
	            if (!this.client.options.hudHidden || this.client.currentScreen != null) {
	               RenderSystem.defaultAlphaFunc();
	               this.renderFloatingItem(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), tickDelta);
	               this.client.inGameHud.render(tickDelta);
	               RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
	            }

	            this.client.getProfiler().pop();
	         }

	         CrashReport crashReport2;
	         CrashReportSection crashReportSection2;
	         if (this.client.overlay != null) {
	            try {
	               this.client.overlay.render(i, j, this.client.getLastFrameDuration());
	            } catch (Throwable var13) {
	               crashReport2 = CrashReport.create(var13, "Rendering overlay");
	               crashReportSection2 = crashReport2.addElement("Overlay render details");
	               crashReportSection2.add("Overlay name", () -> {
	                  return this.client.overlay.getClass().getCanonicalName();
	               });
	               throw new CrashException(crashReport2);
	            }
	         } else if (this.client.currentScreen != null) {
	            try {
	               float delta = this.client.getLastFrameDuration();
	               int[] mouse = FabricControllable.getInstance().getHooks().drawScreen(this.client.currentScreen, i, j, delta);
	        	   Controllable.getInput().onPreRenderScreen();
	               this.client.currentScreen.render(mouse[0], mouse[1], delta);
	               Controllable.getInput().onPostRenderScreen();
	            } catch (Throwable var12) {
	               crashReport2 = CrashReport.create(var12, "Rendering screen");
	               crashReportSection2 = crashReport2.addElement("Screen render details");
	               crashReportSection2.add("Screen name", () -> {
	                  return this.client.currentScreen.getClass().getCanonicalName();
	               });
	               crashReportSection2.add("Mouse location", () -> {
	                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY());
	               });
	               crashReportSection2.add("Screen size", () -> {
	                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getWindow().getScaleFactor());
	               });
	               throw new CrashException(crashReport2);
	            }
	         }

	      }
	   }

	private void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta) {}
	 
	@Shadow 
	private void updateWorldIcon() {}

	@Shadow 
	public abstract void renderWorld(float tickDelta, long startTime, MatrixStack matrixStack);
	
}
