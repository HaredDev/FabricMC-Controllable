package com.mrcrayfish.controllable.client;

import com.mrcrayfish.controllable.Controllable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class ControllerEvents
{
    private float prevHealth = -1;

    public void onPlayerUsingItem(ItemStack stack, int duration)
    {
        if(!Controllable.getOptions().useForceFeedback())
        {
            return;
        }

        /* Stops vibration from running because controller is not in use */
        if(Controllable.getInput().getLastUse() <= 0)
        {
            return;
        }

        Controller controller = Controllable.getController();
        if(controller != null)
        {
            float magnitudeFactor = 0.5F;
            UseAction action = stack.getUseAction();
            switch(action)
            {
                case BLOCK:
                    magnitudeFactor = 0.25F;
                    break;
                case SPEAR:
                    magnitudeFactor = MathHelper.clamp((stack.getMaxUseTime()- duration) / 20F, 0.0F, 0.25F) / 0.25F;
                    break;
                case BOW:
                    magnitudeFactor = MathHelper.clamp((stack.getMaxUseTime() - duration) / 20F, 0.0F, 1.0F) / 1.0F;
                    break;
                case CROSSBOW:
                    magnitudeFactor = MathHelper.clamp((stack.getMaxUseTime() - duration) / 20F, 0.0F, 1.5F) / 1.5F;
                    break;
            }
            controller.getSDL2Controller().rumble(0.5F * magnitudeFactor, 0.5F * magnitudeFactor, 50); //50ms is one tick
        }
    }

    public void onClientTick()
    {

        Controller controller = Controllable.getController();
        if(controller == null)
        {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.world != null && Controllable.getOptions().useForceFeedback())
        {
            if(prevHealth == -1)
            {
                prevHealth = mc.player.getHealth();
            }
            else if(prevHealth > mc.player.getHealth())
            {
                float difference = prevHealth - mc.player.getHealth();
                float magnitude = difference / mc.player.getMaximumHealth();
                controller.getSDL2Controller().rumble(1.0F, 1.0F, (int) (800 * magnitude));
                prevHealth = mc.player.getHealth();
            }
            else
            {
                prevHealth = mc.player.getHealth();
            }
        }
        else if(prevHealth != -1)
        {
            prevHealth = -1;
        }
    }
}
