package com.mrcrayfish.controllable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

/**
 * Author: MrCrayfish
 */
public class Hooks
{
    /**
     * Used in order to fix block breaking progress. This method is linked via ASM.
     */
    public boolean isLeftClicking()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean isLeftClicking = false;
        Controller controller = Controllable.getController();
        if(controller != null)
        {
            if(ButtonBindings.ATTACK.isButtonDown())
            {
                isLeftClicking = true;
            }
        }
        boolean usingVirtualMouse = (Controllable.getOptions().isVirtualMouse() && Controllable.getInput().getLastUse() > 0);
        return isLeftClicking && (mc.mouse.isCursorLocked() || usingVirtualMouse);
    }

    /**
     * Used in order to fix actions like eating or pulling bow back. This method is linked via ASM.
     */
    public boolean isRightClicking()
    {
        boolean isRightClicking = false;
        Controller controller = Controllable.getController();
        if(controller != null)
        {
            if(ButtonBindings.USE_ITEM.isButtonDown())
            {
                isRightClicking = true;
            }
        }
        return isRightClicking;
    }

    /**
     * Used in order to fix the quick move check in inventories. This method is linked via ASM.
     */
    public boolean canQuickMove()
    {
        boolean canQuickMove = false;
        Controller controller = Controllable.getController();
        if(controller != null)
        {
            if(ButtonBindings.QUICK_MOVE.isButtonDown())
            {
                canQuickMove = true;
            }
        }
        return canQuickMove;
    }

    /**
     * Allows the player list to be shown. This method is linked via ASM.
     */
    public boolean canShowPlayerList()
    {
        boolean canShowPlayerList = false;
        Controller controller = Controllable.getController();
        if(controller != null)
        {
            if(ButtonBindings.PLAYER_LIST.isButtonDown())
            {
                canShowPlayerList = true;
            }
        }
        return canShowPlayerList;
    }

    /**
     * Fixes the mouse position when virtual mouse is turned on for controllers. This method is linked via ASM.
     */
    public int[] drawScreen(Screen screen, int mouseX, int mouseY, float partialTicks)
    {
        ControllerInput input = Controllable.getInput();
        if(Controllable.getController() != null && Controllable.getOptions().isVirtualMouse() && input.getLastUse() > 0)
        {
        	MinecraftClient minecraft = MinecraftClient.getInstance();
            mouseX = (int) (input.getVirtualMouseX() * (double) minecraft.getWindow().getScaledWidth() / (double) minecraft.getWindow().getWidth());
            mouseY = (int) (input.getVirtualMouseY() * (double) minecraft.getWindow().getScaledHeight() / (double) minecraft.getWindow().getHeight());
        }
        return new int[] {mouseX, mouseY};
    }

    /**
     * Fixes selected item name rendering not being offset by console hotbar
     */
    public void applyHotbarOffset()
    {
        if(Controllable.getOptions().useConsoleHotbar())
        {
            RenderSystem.translated(0, -20, 0);
        }
    }
}
