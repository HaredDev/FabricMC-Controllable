package com.hared.controllable.events;

import com.mrcrayfish.controllable.event.AvailableActionsEvent;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface AvailableActionsCallback {
	
	Event<AvailableActionsCallback> EVENT = EventFactory.createArrayBacked(AvailableActionsCallback.class, (listeners) -> (eventObj) -> {
		
			for(AvailableActionsCallback listener : listeners)
				listener.onAvailableActionsEvent(eventObj);
			
		});
	
	void onAvailableActionsEvent(AvailableActionsEvent eventObj);
	
}
