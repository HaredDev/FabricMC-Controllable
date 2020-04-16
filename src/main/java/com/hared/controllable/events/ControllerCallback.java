package com.hared.controllable.events;

import com.mrcrayfish.controllable.event.ControllerEvent;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ControllerCallback {

	Event<ControllerCallback> EVENT = EventFactory.createArrayBacked(ControllerCallback.class, (listeners) -> (type, eventObj) -> {
		
		for(ControllerCallback listener : listeners) {
			boolean result = listener.onControllerEvent(type, eventObj);
			if(!result) {
				return !result;
			}
		}
		return false;
	});
	
	/**
	 * 
	 * @param type Event type
	 * @param eventObj Event object
	 * @return false if event is canceled else true
	 */
	 boolean onControllerEvent(Type type, ControllerEvent eventObj);
	
	public static enum Type{
		ButtonInput,
		Button,
		Move,
		Turn;
	}
	
}
