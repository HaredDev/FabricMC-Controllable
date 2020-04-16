package com.hared.controllable.util;

import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

public abstract class BasicFabricEventAdapter {
	
	
	public static class InitGuiAdapter extends BasicFabricEventAdapter{
		
		private final Object obj;
		private final List<AbstractButtonWidget> button;
		private List<Element> children;

		public InitGuiAdapter(Object obj, List<AbstractButtonWidget> buttons, List<Element> children) {
			this.obj = obj;
			this.button = buttons;
			this.children = children;
		}

		public Screen getScreen() {
			return (Screen)this.obj;
		}
		
		public void addButton(AbstractButtonWidget button){
			this.button.add(button);
			this.children.add(button);
		}
		
	}
	
}
