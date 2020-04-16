package com.hared.controllable.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hared.controllable.FabricControllable;
import com.hared.controllable.util.BasicFabricEventAdapter.InitGuiAdapter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	
	@Shadow @Final protected List<AbstractButtonWidget> buttons;
	@Shadow @Final protected List<Element> children;
	
	@Inject(at = @At("RETURN"), method = ("init"))
	public void init(MinecraftClient client, int width, int height, CallbackInfo info) {
		FabricControllable.getInstance().getGuiEvent().onOpenGui(new InitGuiAdapter(this, this.buttons, this.children));
	}

}
