package com.mrcrayfish.controllable;

import com.badlogic.gdx.controllers.ControllerAdapter;
import com.mrcrayfish.controllable.client.*;
import com.mrcrayfish.controllable.client.gui.ControllerLayoutScreen;
import com.mrcrayfish.controllable.client.settings.ControllerOptions;

import net.minecraft.client.MinecraftClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libsdl.SDL_Error;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;

import java.io.File;

import static org.libsdl.SDL.*;

/**
 * Author: MrCrayfish
 * 
 * Modified by: Hared
 */
public class Controllable extends ControllerAdapter
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    private static ControllerOptions options;
    private static SDL2ControllerManager manager;
    private static Controller controller;
    private static ControllerInput input = new ControllerInput();

    public static Controller getController()
    {
        return controller;
    }

    public static ControllerOptions getOptions()
    {
        return options;
    }

    public static ControllerInput getInput()
    {
        return input;
    }

    public static SDL2ControllerManager getManager() {
    	return manager;
    }
    
    public void onSetup(MinecraftClient mc)
    {
        File configFolder = new File(mc.runDirectory, "config");

        ControllerProperties.load(configFolder);
        Controllable.options = new ControllerOptions(mc.runDirectory);

        /* Loads up the controller manager and setup shutdown cleanup */
        Controllable.manager = new SDL2ControllerManager();
        Controllable.manager.addListenerAndRunForConnectedControllers(this);

        /* Attempts to load the first controller connected if auto select is enabled */
        if(options.isAutoSelect() && manager.getControllers().size > 0)
        {
            com.badlogic.gdx.controllers.Controller controller = manager.getControllers().get(0);
            if(controller instanceof SDL2Controller)
            {
                setController((SDL2Controller) controller);
            }
        }

        Mappings.load(configFolder);
    }

    @Override
    public void connected(com.badlogic.gdx.controllers.Controller sdlController)
    {
        MinecraftClient.getInstance().submit(() ->
        {
            if(sdlController instanceof SDL2Controller)
            {
                if(Controllable.controller == null)
                {
                    if(options.isAutoSelect())
                    {
                        setController((SDL2Controller) sdlController);
                    }

                    MinecraftClient mc = MinecraftClient.getInstance();
                    if(mc.player != null)
                    {
                    	MinecraftClient.getInstance().getToastManager().add(new ControllerToast(true, Controllable.controller.getName()));
                    }
                }
            }
        });
    }

    @Override
    public void disconnected(com.badlogic.gdx.controllers.Controller sdlController)
    {
    	MinecraftClient.getInstance().submit(() ->
        {
            if(Controllable.controller != null)
            {
                if(Controllable.controller.getSDL2Controller() == sdlController)
                {
                    Controller oldController = Controllable.controller;

                    setController(null);

                    if(options.isAutoSelect() && manager.getControllers().size > 0)
                    {
                        setController((SDL2Controller) manager.getControllers().get(0));
                    }

                    MinecraftClient mc = MinecraftClient.getInstance();
                    if(mc.player != null)
                    {
                    	MinecraftClient.getInstance().getToastManager().add(new ControllerToast(false, oldController.getName()));
                    }
                }
            }
        });
    }

    public static void setController(SDL2Controller sdl2Controller)
    {
        if(sdl2Controller != null)
        {
            Controller controller = new Controller(sdl2Controller);
            Controllable.controller = controller;
            Mappings.updateControllerMappings(controller);
        }
        else
        {
            Controllable.controller = null;
        }
    }

    public void handleButtonInput()
    {

        try
        {
            manager.pollState();
        }
        catch(SDL_Error e)
        {
            e.printStackTrace();
        }

        if(controller == null)
            return;

        ButtonBinding.tick();

        Controller currentController = controller;
        this.processButton(Buttons.A, this.getButtonState(SDL_CONTROLLER_BUTTON_A));
        this.processButton(Buttons.B, this.getButtonState(SDL_CONTROLLER_BUTTON_B));
        this.processButton(Buttons.X, this.getButtonState(SDL_CONTROLLER_BUTTON_X));
        this.processButton(Buttons.Y, this.getButtonState(SDL_CONTROLLER_BUTTON_Y));
        this.processButton(Buttons.SELECT, this.getButtonState(SDL_CONTROLLER_BUTTON_BACK));
        this.processButton(Buttons.HOME, this.getButtonState(SDL_CONTROLLER_BUTTON_GUIDE));
        this.processButton(Buttons.START, this.getButtonState(SDL_CONTROLLER_BUTTON_START));
        this.processButton(Buttons.LEFT_THUMB_STICK, this.getButtonState(SDL_CONTROLLER_BUTTON_LEFTSTICK));
        this.processButton(Buttons.RIGHT_THUMB_STICK, this.getButtonState(SDL_CONTROLLER_BUTTON_RIGHTSTICK));
        this.processButton(Buttons.LEFT_BUMPER, this.getButtonState(SDL_CONTROLLER_BUTTON_LEFTSHOULDER));
        this.processButton(Buttons.RIGHT_BUMPER, this.getButtonState(SDL_CONTROLLER_BUTTON_RIGHTSHOULDER));
        this.processButton(Buttons.LEFT_TRIGGER, Math.abs(currentController.getLTriggerValue()) >= 0.1F);
        this.processButton(Buttons.RIGHT_TRIGGER, Math.abs(currentController.getRTriggerValue()) >= 0.1F);
        this.processButton(Buttons.DPAD_UP, this.getButtonState(SDL_CONTROLLER_BUTTON_DPAD_UP));
        this.processButton(Buttons.DPAD_DOWN, this.getButtonState(SDL_CONTROLLER_BUTTON_DPAD_DOWN));
        this.processButton(Buttons.DPAD_LEFT, this.getButtonState(SDL_CONTROLLER_BUTTON_DPAD_LEFT));
        this.processButton(Buttons.DPAD_RIGHT, this.getButtonState(SDL_CONTROLLER_BUTTON_DPAD_RIGHT));
    }

    private void processButton(int index, boolean state)
    {
        if(MinecraftClient.getInstance().currentScreen instanceof ControllerLayoutScreen && state)
        {
            if(((ControllerLayoutScreen) MinecraftClient.getInstance().currentScreen).onButtonInput(index))
            {
                return;
            }
        }

        if (controller == null)
        {
            return;
        }

        if(controller.getMapping() != null)
        {
            index = controller.getMapping().remap(index);
        }

        //No binding so don't perform any action
        if(index == -1)
        {
            return;
        }

        ButtonStates states = controller.getButtonsStates();

        if(state)
        {
            if(!states.getState(index))
            {
                states.setState(index, true);
                input.handleButtonInput(controller, index, true);
            }
        }
        else if(states.getState(index))
        {
            states.setState(index, false);
            input.handleButtonInput(controller, index, false);
        }
    }

    /**
     * Returns whether a button on the controller is pressed or not. This is a raw approach to
     * getting whether a button is pressed or not. You should use a {@link ButtonBinding} instead.
     *
     * @param button the button to check if pressed
     * @return
     */
    public static boolean isButtonPressed(int button)
    {
        return controller != null && controller.getButtonsStates().getState(button);
    }

    private boolean getButtonState(int buttonCode)
    {
        return controller != null && controller.getSDL2Controller().getButton(buttonCode);
    }
}
