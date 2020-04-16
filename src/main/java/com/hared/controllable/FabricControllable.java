package com.hared.controllable;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.ControllerEvents;
import com.mrcrayfish.controllable.client.GuiEvents;
import com.mrcrayfish.controllable.client.Hooks;
import com.mrcrayfish.controllable.client.RenderEvents;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

/**
 * Author: Hared 
 */
public class FabricControllable implements ModInitializer{
	
	private final Controllable mod = new Controllable();
	private final Hooks hooks = new Hooks();
	private final ControllerEvents controllerEvents = new ControllerEvents();
	private final RenderEvents renderEvents = new RenderEvents();
	private static FabricControllable INSTANCE;
	private GuiEvents guiEvent;
	
	public static FabricControllable getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void onInitialize() {
		INSTANCE = this;
		mod.onSetup(MinecraftClient.getInstance());
		guiEvent = new GuiEvents(Controllable.getManager());
	}
	
	public Controllable getInternalMod() {
		return this.mod;
	}
	
	public RenderEvents getRenderEvents() {
		return this.renderEvents;
	}
	
	public GuiEvents getGuiEvent() {
		return this.guiEvent;
	}
	
	public ControllerEvents getControllerEvents() {
		return this.controllerEvents;
	}

	public Hooks getHooks() {
		return hooks;
	}
	
}
