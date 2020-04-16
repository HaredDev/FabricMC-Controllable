package com.hared.controllable.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemGroup;

//TODO getFiled/getMethod once for efficiency
public class UtilHelper {

	@SuppressWarnings("unchecked")
	public List<ChatHudLine> getVisibleMessages(ChatHud chatHud){
		Field f = this.findField(ChatHud.class, "visibleMessages", "field_2064");
		f.setAccessible(true);
		try {
			return (List<ChatHudLine>) f.get(chatHud);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Slot getFocusedSlot(ContainerScreen<?> container) {
		Field f = this.findField(ContainerScreen.class, "focusedSlot", "field_2787");
		f.setAccessible(true);
		try {
			return (Slot) f.get(container);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void doAttack() {
    	Method m = findMethod(MinecraftClient.class, "doAttack", "method_1536", new Class[] {});
    	m.setAccessible(true);
    	try {
			m.invoke(MinecraftClient.getInstance(), new Object[] {});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
    }
    
    public void doItemPick() {
    	Method m = findMethod(MinecraftClient.class, "doItemPick", "method_1511", new Class[] {});
    	m.setAccessible(true);
    	try {
			m.invoke(MinecraftClient.getInstance(), new Object[] {});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
    }
    
    public double getMouseTime(Mouse mouse) {
    	Field f = findField(Mouse.class, "glfwTime", "field_1792");
    	f.setAccessible(true);
		try {
			return f.getDouble(mouse);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	return 0;
	}

    public void setMouseTime(Mouse mouse, double value) {
    	Field f = findField(Mouse.class, "glfwTime", "field_1792");
    	f.setAccessible(true);
		try {
			f.setDouble(mouse, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

    
	public int getActiveMouseButton(Mouse mouse) {
    	Field f = findField(Mouse.class, "activeButton", "field_1780");
    	f.setAccessible(true);
		try {
			return f.getInt(mouse);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void setActiveMouseButton(Mouse mouse, int value) {
    	Field f = findField(Mouse.class, "activeButton", "field_1780");
    	f.setAccessible(true);
		try {
			f.setInt(mouse, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void doItemUse() {
    	Method m = findMethod(MinecraftClient.class, "doItemUse", "method_1583", new Class[] {});
    	m.setAccessible(true);
    	try {
			m.invoke(MinecraftClient.getInstance(), new Object[] {});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
    }
    
    public int getItemCooldownTimer() {
    	Field f = findField(MinecraftClient.class, "itemUseCooldown", "field_1752");
    	f.setAccessible(true);
    	try {
			return f.getInt(MinecraftClient.getInstance());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    public void setSelectedTab(CreativeInventoryScreen screen, ItemGroup itemGroup) {
    	Method m = findMethod(screen.getClass(), "setSelectedTab", "method_2466", new Class[] {ItemGroup.class});
    	m.setAccessible(true);
    	try {
			m.invoke(screen, new Object[] {itemGroup});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
    }
    
    public int getContainerWidth(ContainerScreen<?> screen) {
    	Field f = findField(ContainerScreen.class, "containerWidth", "field_2792");
    	f.setAccessible(true);
    	try {
			return f.getInt(screen);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    public int getContainerHeight(ContainerScreen<?> screen) {
    	Field f = findField(ContainerScreen.class, "containerHeight", "field_2779");
    	f.setAccessible(true);
    	try {
			return f.getInt(screen);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    public float getScrollPosition(CreativeInventoryScreen screen) {
    	Field f = findField(screen.getClass(), "scrollPosition", "field_2890");
    	f.setAccessible(true);
    	try {
			return f.getFloat(screen);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    public void setScrollPosition(CreativeInventoryScreen screen, float value) {
    	Field f = findField(screen.getClass(), "scrollPosition", "field_2890");
    	f.setAccessible(true);
    	try {
			f.setFloat(screen, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    }
	
	private Field findField(Class<?> clazz, String deobf, String obf) {
		Field f = null;
		try {
			f = findFieldDecleared(clazz, deobf, obf);
		} catch(Exception e) {
			try {
				f = findFieldPublic(clazz, deobf, obf);
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		return f;
	}
	
	private Field findFieldDecleared(Class<?> clazz, String deobf, String obf) throws NoSuchFieldException, SecurityException {
		Field f = null;
		try {
			f = clazz.getDeclaredField(deobf);
		} catch(Exception e) {
			f = clazz.getDeclaredField(obf);
		}
		return f;
	}
	
	private Field findFieldPublic(Class<?> clazz, String deobf, String obf) throws NoSuchFieldException, SecurityException {
		Field f = null;
		try {
			f = clazz.getField(deobf);
		} catch(Exception e) {
			f = clazz.getField(obf);
		}
		return f;
	}

	private Method findMethod(Class<?> clazz, String deobf, String obf, Class<?>[] clazzes) {
		Method m = null;
		try {
			m = findMethodDecleared(clazz, deobf, obf, clazzes);
		} catch(Exception e) {
			try {
				m = findMethodPublic(clazz, deobf, obf, clazzes);
			} catch (NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		return m;
	}
	
	private Method findMethodDecleared(Class<?> clazz, String deobf, String obf, Class<?>[] clazzes) throws SecurityException, NoSuchMethodException {
		Method m = null;
		try {
			m = clazz.getDeclaredMethod(deobf, clazzes);
		} catch(Exception e) {
			m = clazz.getDeclaredMethod(obf, clazzes);
		}
		return m;
	}
	
	private Method findMethodPublic(Class<?> clazz, String deobf, String obf, Class<?>[] clazzes) throws SecurityException, NoSuchMethodException {
		Method m = null;
		try {
			m = clazz.getMethod(deobf, clazzes);
		} catch(Exception e) {
			m = clazz.getMethod(obf, clazzes);
		}
		return m;
	}

	
}
