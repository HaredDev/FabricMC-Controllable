package com.mrcrayfish.controllable.client;

import com.mrcrayfish.controllable.ButtonStates;

import net.minecraft.client.resource.language.I18n;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;

import static org.libsdl.SDL.*;

/**
 *  A wrapper class that aims to reduce the exposure to the underlying controller library. This class
 *  provides simple and straight forward methods to retrieve values about the current state of the
 *  controller.
 */
public class Controller
{
    private String cachedName;
    private Mappings.Entry mapping;
    private SDL2Controller controller;
    private ButtonStates states;

    public Controller(SDL2Controller controller)
    {
        this.controller = controller;
        this.states = new ButtonStates();
        this.getName(); //cache the name straight away
    }

    /**
     * Gets the underlying {@link SDL2Controller} of this this controller instance.
     * This is gives you direct access to the controller state.
     *
     * @return the sdl2controller controller instance
     */
    public SDL2Controller getSDL2Controller()
    {
        return controller;
    }

    /**
     * Used internally to update button states
     */
    public ButtonStates getButtonsStates()
    {
        return states;
    }

    /**
     * Gets the name of this controller. sdl2gdx prefixes the name and this method removes it.
     *
     * @return the name of this controller
     */
    public String getName()
    {
        if(this.controller.isConnected())
        {
            if(this.cachedName == null)
            {
                this.cachedName = this.controller.getName().replace("SDL GameController ", "").replace("SDL Joystick ", "");
            }
            return this.cachedName;
        }
        return I18n.translate("controllable.toast.controller");
    }

    /**
     * Gets whether the specified button is pressed or not. It is recommended to use
     * {@link ButtonBinding} instead as this method is a raw approach.
     *
     * @param button the button to check
     *
     * @return if the specified button is pressed or not
     */
    public boolean isButtonPressed(int button)
    {
        return states.getState(button);
    }

    /**
     * Gets the value of the left trigger
     *
     * @return the left trigger value
     */
    public float getLTriggerValue()
    {
        return controller.getAxis(SDL_CONTROLLER_AXIS_TRIGGERLEFT);
    }

    /**
     * Gets the value of the right trigger
     *
     * @return the right trigger value
     */
    public float getRTriggerValue()
    {
        return controller.getAxis(SDL_CONTROLLER_AXIS_TRIGGERRIGHT);
    }

    /**
     * Gets the left thumb stick x value
     *
     * @return the left thumb stick x value
     */
    public float getLThumbStickXValue()
    {
        return controller.getAxis(SDL_CONTROLLER_AXIS_LEFTX);
    }

    /**
     * Gets the left thumb stick y value
     *
     * @return the left thumb stick y value
     */
    public float getLThumbStickYValue()
    {
        return controller.getAxis(SDL_CONTROLLER_AXIS_LEFTY);
    }

    /**
     * Gets the right thumb stick x value
     *
     * @return the right thumb stick x value
     */
    public float getRThumbStickXValue()
    {
        return controller.getAxis(SDL_CONTROLLER_AXIS_RIGHTX);
    }

    /**
     * Gets the right thumb stick y value
     *
     * @return the right thumb stick y value
     */
    public float getRThumbStickYValue()
    {
        return controller.getAxis(SDL_CONTROLLER_AXIS_RIGHTY);
    }

    /**
     * Sets the mapping for this controller
     *
     * @param mapping the mapping to assign
     */
    public void setMapping(Mappings.Entry mapping)
    {
        this.mapping = mapping;
    }

    /**
     * Gets the mapping of this controller
     *
     * @return the mapping of this controller or null if not present
     */
    public Mappings.Entry getMapping()
    {
        return mapping;
    }
}
