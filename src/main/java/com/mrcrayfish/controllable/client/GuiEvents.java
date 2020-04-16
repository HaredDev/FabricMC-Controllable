package com.mrcrayfish.controllable.client;

import com.hared.controllable.util.BasicFabricEventAdapter.InitGuiAdapter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.gui.ControllerSelectionScreen;
import com.mrcrayfish.controllable.client.gui.widget.ControllerButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SettingsScreen;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;

/**
 * Author: MrCrayfish
 */
public class GuiEvents
{

    private SDL2ControllerManager manager;

    public GuiEvents(SDL2ControllerManager manager)
    {
        this.manager = manager;
    }

    public void onOpenGui(InitGuiAdapter adapter)
    {
        /* Resets the controller button states */
        ButtonBinding.resetButtonStates();
        if(adapter.getScreen() instanceof SettingsScreen)
        {
            int y = adapter.getScreen().height / 6 + 72 - 6;
            adapter.addButton(new ControllerButton((adapter.getScreen().width / 2) + 5 + 150 + 4, y, button -> MinecraftClient.getInstance().openScreen(new ControllerSelectionScreen(manager, adapter.getScreen()))));
        }
    }

    public void onPreRenderOverlay()
    {
        if(Controllable.getOptions().useConsoleHotbar())
        {      
            GlStateManager.translated(0, -20, 0);
        }
    }

    public void onPostRenderOverlay()
    {
        if(Controllable.getOptions().useConsoleHotbar())
        {
        	GlStateManager.translated(0, 20, 0);
        }
    }
}
