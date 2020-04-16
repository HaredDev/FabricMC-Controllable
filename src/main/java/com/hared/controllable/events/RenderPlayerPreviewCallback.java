package com.hared.controllable.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RenderPlayerPreviewCallback {

	Event<RenderPlayerPreviewCallback> EVENT = EventFactory.createArrayBacked(RenderPlayerPreviewCallback.class, (listeners) -> () -> {
		
		for(RenderPlayerPreviewCallback listener : listeners) {
			boolean result = listener.onRenderPlayerPreviewEvent();
			if(!result) {
				return !result;
			}
		}
		return false;
		
	});

	/**
	 * @return false if event is canceled else true
	 */
	boolean onRenderPlayerPreviewEvent();
	
}
