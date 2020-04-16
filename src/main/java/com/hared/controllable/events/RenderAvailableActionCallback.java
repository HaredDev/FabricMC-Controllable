package com.hared.controllable.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RenderAvailableActionCallback {
	
	Event<RenderAvailableActionCallback> EVENT = EventFactory.createArrayBacked(RenderAvailableActionCallback.class, (listeners) -> () -> {
		
		for(RenderAvailableActionCallback listener : listeners) {
			boolean result = listener.onRenderAvailableActionEvent();
			if(!result) {
				return !result;
			}
		}
		return false;
		
	});

	/**
	 * @return false if event is canceled else true
	 */
	boolean onRenderAvailableActionEvent();
	
}
