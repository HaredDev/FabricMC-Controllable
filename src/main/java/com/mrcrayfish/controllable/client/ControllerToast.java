package com.mrcrayfish.controllable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.controllable.client.gui.ControllerLayoutScreen;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.TextFormat;

/**
 * Author: MrCrayfish
 */
public class ControllerToast implements Toast
{
    private boolean connected;
    private String controllerName;

    public ControllerToast(boolean connected, String controllerName)
    {
        this.connected = connected;
        this.controllerName = controllerName;
    }

    @Override
    public Visibility draw(ToastManager toastGui, long delta)
    {
        toastGui.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        toastGui.blit(0, 0, 0, 32, 160, 32);

        toastGui.getGame().getTextureManager().bindTexture(ControllerLayoutScreen.TEXTURE);
        toastGui.blit(8, 8, 20, 43, 20, 16);

        String title = toastGui.getGame().textRenderer.trimToWidth(controllerName, 120);
        toastGui.getGame().textRenderer.draw(TextFormat.DARK_GRAY + title, 35, 7, 0);

        String message = connected ?
        		TextFormat.DARK_GREEN.toString() + TextFormat.BOLD.toString() + I18n.translate("controllable.toast.connected") :
        			TextFormat.RED.toString() + TextFormat.BOLD.toString() + I18n.translate("controllable.toast.disconnected");
        toastGui.getGame().textRenderer.draw(TextFormat.BOLD + message, 35, 18, 0);

        return delta >= 3000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
