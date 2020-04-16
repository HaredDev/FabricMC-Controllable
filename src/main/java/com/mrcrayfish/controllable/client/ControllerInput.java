package com.mrcrayfish.controllable.client;

import com.hared.controllable.events.ControllerCallback;
import com.hared.controllable.util.UtilHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.Reference;
import com.mrcrayfish.controllable.client.gui.ControllerLayoutScreen;
import com.mrcrayfish.controllable.event.ControllerEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import static org.libsdl.SDL.SDL_CONTROLLER_BUTTON_DPAD_DOWN;
import static org.libsdl.SDL.SDL_CONTROLLER_BUTTON_DPAD_UP;

/**
 * Author: MrCrayfish
 * Modified by: Hared
 */
public class ControllerInput
{
    private static final Identifier CURSOR_TEXTURE = new Identifier(Reference.MOD_ID, "textures/gui/cursor.png");

    private final UtilHelper helper = new UtilHelper();
    
    private int lastUse = 0;
    private boolean keyboardSneaking = false;
    private boolean sneaking = false;
    private boolean isFlying = false;
    private boolean nearSlot = false;
    private double virtualMouseX;
    private double virtualMouseY;
    private float prevXAxis;
    private float prevYAxis;
    private int prevTargetMouseX;
    private int prevTargetMouseY;
    private int targetMouseX;
    private int targetMouseY;
    private double mouseSpeedX;
    private double mouseSpeedY;
    private boolean moved;
    private float targetPitch;
    private float targetYaw;

    private int dropCounter = -1;

    public double getVirtualMouseX()
    {
        return virtualMouseX;
    }

    public double getVirtualMouseY()
    {
        return virtualMouseY;
    }

    public int getLastUse()
    {
        return lastUse;
    }

    
    public void onClientTick()
    {
            prevTargetMouseX = targetMouseX;
            prevTargetMouseY = targetMouseY;

            if(lastUse > 0)
            {
                lastUse--;
            }

            Controller controller = Controllable.getController();
            if(controller == null)
                return;

            if(Math.abs(controller.getLTriggerValue()) >= 0.1F || Math.abs(controller.getRTriggerValue()) >= 0.1F)
            {
                lastUse = 100;
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            if(mc.mouse.isCursorLocked())
                return;

            if(mc.currentScreen == null || mc.currentScreen instanceof ControllerLayoutScreen)
                return;
            
            float deadZone = (float) Controllable.getOptions().getDeadZone();

            /* Only need to run code if left thumb stick has input */
            boolean moving = Math.abs(controller.getLThumbStickXValue()) >= deadZone || Math.abs(controller.getLThumbStickYValue()) >= deadZone;
            if(moving)
            {
                /* Updates the target mouse position when the initial thumb stick movement is
                 * detected. This fixes an issue when the user moves the cursor with the mouse then
                 * switching back to controller, the cursor would jump to old target mouse position. */
                if(Math.abs(prevXAxis) < deadZone && Math.abs(prevYAxis) < deadZone)
                {
                    double mouseX = mc.mouse.getX();
                    double mouseY = mc.mouse.getY();
                    if(Controllable.getController() != null && Controllable.getOptions().isVirtualMouse())
                    {
                        mouseX = virtualMouseX;
                        mouseY = virtualMouseY;
                    }
                    prevTargetMouseX = targetMouseX = (int) mouseX;
                    prevTargetMouseY = targetMouseY = (int) mouseY;
                }

                float xAxis = (controller.getLThumbStickXValue() > 0.0F ? 1 : -1) * Math.abs(controller.getLThumbStickXValue());
                if(Math.abs(xAxis) >= deadZone)
                {
                    mouseSpeedX = xAxis;
                }
                else
                {
                    mouseSpeedX = 0.0F;
                }

                float yAxis = (controller.getLThumbStickYValue() > 0.0F ? 1 : -1) * Math.abs(controller.getLThumbStickYValue());
                if(Math.abs(yAxis) >= deadZone)
                {
                    mouseSpeedY = yAxis;
                }
                else
                {
                    mouseSpeedY = 0.0F;
                }
            }

            if(Math.abs(mouseSpeedX) > 0.05F || Math.abs(mouseSpeedY) > 0.05F)
            {
                double mouseSpeed = Controllable.getOptions().getMouseSpeed() * mc.window.getScaleFactor();
                targetMouseX += mouseSpeed * mouseSpeedX;
                targetMouseX = MathHelper.clamp(targetMouseX, 0, mc.window.getWidth());
                targetMouseY += mouseSpeed * mouseSpeedY;
                targetMouseY = MathHelper.clamp(targetMouseY, 0, mc.window.getHeight());
                lastUse = 100;
                moved = true;
            }

            prevXAxis = controller.getLThumbStickXValue();
            prevYAxis = controller.getLThumbStickYValue();

            this.moveMouseToClosestSlot(moving, mc.currentScreen);

            if(mc.currentScreen instanceof CreativeInventoryScreen)
            {
                this.handleCreativeScrolling((CreativeInventoryScreen) mc.currentScreen, controller);
            }
            
            if(Controllable.getController() != null && Controllable.getOptions().isVirtualMouse())
            {
                Screen screen = mc.currentScreen;
                if(screen != null && (targetMouseX != prevTargetMouseX || targetMouseY != prevTargetMouseY))
                {
                    if(mc.overlay == null)
                    {
                        double mouseX = virtualMouseX * (double) mc.window.getScaledWidth() / (double) mc.window.getWidth();
                        double mouseY = virtualMouseY * (double) mc.window.getScaledHeight() / (double) mc.window.getHeight();
                        Screen.wrapScreenError(() -> screen.mouseMoved(mouseX, mouseY), "mouseMoved event handler", ((Element) screen).getClass().getCanonicalName());
                        if(helper.getActiveMouseButton(mc.mouse) != -1 && helper.getMouseTime(mc.mouse) > 0.0D)
                        {
                            double dragX = (targetMouseX - prevTargetMouseX) * (double) mc.window.getScaledWidth() / (double) mc.window.getWidth();
                            double dragY = (targetMouseY - prevTargetMouseY) * (double) mc.window.getScaledHeight() / (double) mc.window.getHeight();
                            Screen.wrapScreenError(() ->
                            {
                                if(((Element) screen).mouseDragged(mouseX, mouseY, helper.getActiveMouseButton(mc.mouse), dragX, dragY))
                                {
                                    return;
                                }                        
                            }, "mouseDragged", ((Element) screen).getClass().getCanonicalName());
                        }
                    }
                }
            }
    }
	
    public void onScreenInit()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.currentScreen == null)
        {
            nearSlot = false;
            moved = false;
            mouseSpeedX = 0.0;
            mouseSpeedY = 0.0;
            virtualMouseX = targetMouseX = prevTargetMouseX = (int) (mc.window.getWidth() / 2F);
            virtualMouseY = targetMouseY = prevTargetMouseY = (int) (mc.window.getHeight() / 2F);
        }
    }
    
    public void onPreRenderScreen()
    {
        /* Makes the cursor movement appear smooth between ticks. This will only run if the target
         * mouse position is different to the previous tick's position. This allows for the mouse
         * to still be used as input. */
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.currentScreen != null && (targetMouseX != prevTargetMouseX || targetMouseY != prevTargetMouseY))
        {
            if(!(mc.currentScreen instanceof ControllerLayoutScreen))
            {
                float partialTicks = MinecraftClient.getInstance().getTickDelta();
                double mouseX = (prevTargetMouseX + (targetMouseX - prevTargetMouseX) * partialTicks + 0.5);
                double mouseY = (prevTargetMouseY + (targetMouseY - prevTargetMouseY) * partialTicks + 0.5);
                if(Controllable.getOptions().isVirtualMouse())
                {
                    virtualMouseX = mouseX;
                    virtualMouseY = mouseY;
                }
                else
                {
                    GLFW.glfwSetCursorPos(mc.window.getHandle(), mouseX, mouseY);
                }
            }
        }
    }

    public void onPostRenderScreen()
    {
        if(Controllable.getController() != null && Controllable.getOptions().isVirtualMouse() && lastUse > 0)
        {
        	GlStateManager.pushMatrix();
            {
                CursorType type = Controllable.getOptions().getCursorType();
                MinecraftClient minecraft = MinecraftClient.getInstance();
                if(minecraft.player == null || (minecraft.player.inventory.getCursorStack().isEmpty() || type == CursorType.CONSOLE))
                {
                    double mouseX = (prevTargetMouseX + (targetMouseX - prevTargetMouseX) * MinecraftClient.getInstance().getTickDelta());
                    double mouseY = (prevTargetMouseY + (targetMouseY - prevTargetMouseY) * MinecraftClient.getInstance().getTickDelta());
                    GlStateManager.translated(mouseX / minecraft.window.getScaleFactor(), mouseY / minecraft.window.getScaleFactor(), 500);
                    GlStateManager.color3f(1.0F, 1.0F, 1.0F);
                    GlStateManager.disableLighting();
                    MinecraftClient.getInstance().getTextureManager().bindTexture(CURSOR_TEXTURE);
                    if(type == CursorType.CONSOLE)
                    {
                    	GlStateManager.scaled(0.5, 0.5, 0.5);
                    }
                    Screen.blit(-8, -8, 16, 16, nearSlot ? 16 : 0, type.ordinal() * 16, 16, 16, 32, CursorType.values().length * 16);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    public void onRenderRide()
    {
        Controller controller = Controllable.getController();
        if(controller == null)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if(player == null)
            return;

        if(mc.currentScreen == null && (targetYaw != 0F || targetPitch != 0F))
        {
            float elapsedTicks = MinecraftClient.getInstance().getLastFrameDuration();
            player.changeLookDirection((targetYaw / 0.15) * elapsedTicks, (targetPitch / 0.15) * (Controllable.getOptions().isInvertLook() ? -1 : 1) * elapsedTicks);
            if(player.getVehicle() != null)
            {
                player.getVehicle().onPassengerLookAround(player);
            }
        }
    }

    public void onRenderClient()
    {
        targetYaw = 0F;
        targetPitch = 0F;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if(player == null)
            return;

        Controller controller = Controllable.getController();
        if(controller == null)
            return;

        if(mc.currentScreen == null)
        {
            float deadZone = (float) Controllable.getOptions().getDeadZone();

            /* Handles rotating the yaw of player */
            if(Math.abs(controller.getRThumbStickXValue()) >= deadZone)
            {
                lastUse = 100;
                double rotationSpeed = Controllable.getOptions().getRotationSpeed();
                ControllerEvent.Turn turnEvent = new ControllerEvent.Turn(controller, (float) rotationSpeed, (float) rotationSpeed * 0.75F);
                if(!ControllerCallback.EVENT.invoker().onControllerEvent(ControllerCallback.Type.Turn, turnEvent))
                {
                    float deadZoneTrim = (controller.getRThumbStickXValue() > 0 ? 1 : -1) * deadZone;
                    float rotationYaw = (turnEvent.getYawSpeed() * (controller.getRThumbStickXValue() - deadZoneTrim) / (1.0F - deadZone)) * 0.33F;
                    targetYaw = rotationYaw;
                }
            }
            if(Math.abs(controller.getRThumbStickYValue()) >= deadZone)
            {
                lastUse = 100;
                double rotationSpeed = Controllable.getOptions().getRotationSpeed();
                ControllerEvent.Turn turnEvent = new ControllerEvent.Turn(controller, (float) rotationSpeed, (float) rotationSpeed * 0.75F);
                if(!ControllerCallback.EVENT.invoker().onControllerEvent(ControllerCallback.Type.Turn, turnEvent))
                {
                    float deadZoneTrim = (controller.getRThumbStickYValue() > 0 ? 1 : -1) * deadZone;
                    float rotationPitch = (turnEvent.getPitchSpeed() * (controller.getRThumbStickYValue() - deadZoneTrim) / (1.0F - deadZone)) * 0.33F;
                    targetPitch = rotationPitch;
                }
            }
        }

        if(mc.currentScreen == null)
        {
            if(ButtonBindings.DROP_ITEM.isButtonDown())
            {
                lastUse = 100;
                dropCounter++;
            }
        }

        if(dropCounter > 20)
        {
            if (!mc.player.isSpectator())
            {
                mc.player.dropSelectedItem(true);
            }
            dropCounter = 0;
        }
        else if(dropCounter > 0 && !ButtonBindings.DROP_ITEM.isButtonDown())
        {
            if (!mc.player.isSpectator())
            {
                mc.player.dropSelectedItem(false);
            }
            dropCounter = 0;
        }
    }

    public void onInputUpdate(Input input)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null)
            return;

        Controller controller = Controllable.getController();
        if(controller == null)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();

        if(keyboardSneaking && !mc.options.keySneak.isPressed())
        {
            sneaking = false;
            keyboardSneaking = false;
        }

        if(mc.options.keySneak.isPressed())
        {
            sneaking = true;
            keyboardSneaking = true;
        }

        if(mc.player.abilities.flying || mc.player.isRiding())
        {
            lastUse = 100;
            sneaking = mc.options.keySneak.isPressed();
            sneaking |= ButtonBindings.SNEAK.isButtonDown();
            isFlying = true;
        }
        else if(isFlying)
        {
            sneaking = false;
            isFlying = false;
        }

        input.sneaking = sneaking;

        if(mc.currentScreen == null)
        {
            if(!ControllerCallback.EVENT.invoker().onControllerEvent(ControllerCallback.Type.Move, new ControllerEvent.Move(controller)))
            {
                float deadZone = (float) Controllable.getOptions().getDeadZone();

                if(Math.abs(controller.getLThumbStickYValue()) >= deadZone)
                {
                    lastUse = 100;
                    int dir = controller.getLThumbStickYValue() > 0.0F ? -1 : 1;
                    input.pressingForward = dir > 0;
                    input.pressingBack = dir < 0;
                    input.movementForward = dir * MathHelper.clamp((Math.abs(controller.getLThumbStickYValue()) - deadZone) / (1.0F - deadZone), 0.0F, 1.0F);

                    if(input.sneaking)
                    {
                    	input.movementForward *= 0.3D;
                    }
                }

                if(Math.abs(controller.getLThumbStickXValue()) >= deadZone)
                {
                    lastUse = 100;
                    int dir = controller.getLThumbStickXValue() > 0.0F ? -1 : 1;
                    input.pressingRight = dir < 0;
                    input.pressingLeft = dir > 0;
                    input.movementSideways = dir * MathHelper.clamp((Math.abs(controller.getLThumbStickXValue()) - deadZone) / (1.0F - deadZone), 0.0F, 1.0F);

                    if(input.sneaking)
                    {
                        input.movementSideways *= 0.3D;
                    }
                }
            }

            if(ButtonBindings.JUMP.isButtonDown())
            {
                input.jumping = true;
            }
        }

        if(ButtonBindings.USE_ITEM.isButtonDown() && helper.getItemCooldownTimer() == 0 && !mc.player.isUsingItem())
        {
            helper.doItemUse();
        }
    }

    public void handleButtonInput(Controller controller, int button, boolean state)
    {
        if(MinecraftClient.getInstance().currentScreen instanceof ControllerLayoutScreen)
        {
            return;
        }

        lastUse = 100;

        ControllerEvent.ButtonInput eventInput = new ControllerEvent.ButtonInput(controller, button, state);
        if(ControllerCallback.EVENT.invoker().onControllerEvent(ControllerCallback.Type.ButtonInput, eventInput))
            return;

        button = eventInput.getModifiedButton();
        ButtonBinding.setButtonState(button, state);

        ControllerEvent.Button event = new ControllerEvent.Button(controller);
        if(ControllerCallback.EVENT.invoker().onControllerEvent(ControllerCallback.Type.Button, event))
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if(state)
        {
            if(mc.currentScreen == null)
            {
                if(ButtonBindings.INVENTORY.isButtonPressed())
                {
                    if(mc.interactionManager.hasRidingInventory())
                    {
                        mc.player.openRidingInventory();
                    }
                    else
                    {
                        mc.getTutorialManager().onInventoryOpened();
                        mc.openScreen(new InventoryScreen(mc.player));
                    }
                }
                else if(ButtonBindings.SNEAK.isButtonPressed())
                {
                    if(mc.player != null && !mc.player.abilities.flying && !mc.player.isRiding())
                    {
                        sneaking = !sneaking;
                    }
                }
                else if(ButtonBindings.SCROLL_RIGHT.isButtonPressed())
                {
                    if(mc.player != null)
                    {
                        mc.player.inventory.scrollInHotbar(-1);
                    }
                }
                else if(ButtonBindings.SCROLL_LEFT.isButtonPressed())
                {
                    if(mc.player != null)
                    {
                        mc.player.inventory.scrollInHotbar(1);
                    }
                }
                else if(ButtonBindings.SWAP_HANDS.isButtonPressed())
                {
                    if(mc.player != null && !mc.player.isSpectator() && mc.getNetworkHandler() != null)
                    {
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, new BlockPos(BlockPos.ZERO), Direction.DOWN));
                    }
                }
                else if(ButtonBindings.TOGGLE_PERSPECTIVE.isButtonPressed() && mc.mouse.isCursorLocked())
                {
                    cycleThirdPersonView();
                }
                else if(ButtonBindings.PAUSE_GAME.isButtonPressed())
                {
                    if(mc.player != null)
                    {
                        mc.openPauseMenu(false);
                    }
                }
                else if(mc.player != null && !mc.player.isUsingItem())
                {
                    if(ButtonBindings.ATTACK.isButtonPressed())
                    {
                        helper.doAttack();
                    }
                    else if(ButtonBindings.USE_ITEM.isButtonPressed())
                    {
                        helper.doItemUse();
                    }
                    else if(ButtonBindings.PICK_BLOCK.isButtonPressed())
                    {
                        helper.doItemPick();
                    }
                }
            }
            else
            {
                if(ButtonBindings.INVENTORY.isButtonPressed())
                {
                    if(mc.player != null)
                    {
                        mc.player.closeScreen();
                    }
                }
                else if(ButtonBindings.SCROLL_RIGHT.isButtonPressed())
                {
                    if(mc.currentScreen instanceof CreativeInventoryScreen)
                    {
                        scrollCreativeTabs((CreativeInventoryScreen) mc.currentScreen, 1);
                    }
                }
                else if(ButtonBindings.SCROLL_LEFT.isButtonPressed())
                {
                    if(mc.currentScreen instanceof CreativeInventoryScreen)
                    {
                        scrollCreativeTabs((CreativeInventoryScreen) mc.currentScreen, -1);
                    }
                }
                else if(ButtonBindings.PAUSE_GAME.isButtonPressed())
                {
                    if(mc.currentScreen instanceof GameMenuScreen)
                    {
                        mc.openScreen(null);
                    }
                }
                else if(button == Buttons.A)
                {
                    invokeMouseClick(mc.currentScreen, 0);
                }
                else if(button == Buttons.X)
                {
                    invokeMouseClick(mc.currentScreen, 1);
                }
                else if(button == Buttons.B && mc.player != null && mc.player.inventory.getCursorStack().isEmpty())
                {
                    invokeMouseClick(mc.currentScreen, 0);
                }
            }
        }
        else
        {
            if(mc.currentScreen == null)
            {

            }
            else
            {
                if(button == Buttons.A)
                {
                    invokeMouseReleased(mc.currentScreen, 0);
                }
                else if(button == Buttons.X)
                {
                    invokeMouseReleased(mc.currentScreen, 1);
                }
            }
        }
    }

    /**
     * Cycles the third person view. Minecraft doesn't have this code in a convenient method.
     */
    private void cycleThirdPersonView()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        mc.options.perspective++;
        if(mc.options.perspective > 2)
        {
        	mc.options.perspective = 0;
        }

        if(mc.options.perspective == 0)
        {
            mc.gameRenderer.onCameraEntitySet(mc.getCameraEntity());
        }
        else if(mc.options.perspective == 1)
        {
            mc.gameRenderer.onCameraEntitySet(null);
        }
    }

    private void scrollCreativeTabs(CreativeInventoryScreen creative, int dir)
    {
        lastUse = 100;
        
            if(dir > 0)
            {
                if(creative.method_2469() < ItemGroup.GROUPS.length - 1)
                {
                    helper.setSelectedTab(creative, ItemGroup.GROUPS[creative.method_2469() + 1]);
                }
            }
            else if(dir < 0)
            {
                if(creative.method_2469() > 0)
                {
                    helper.setSelectedTab(creative, ItemGroup.GROUPS[creative.method_2469() - 1]);
                }
            }
        }

    private void moveMouseToClosestSlot(boolean moving, Screen screen)
    {
        nearSlot = false;

        /* Makes the mouse attracted to slots. This helps with selecting items when using
         * a controller. */
        if(screen instanceof CreativeInventoryScreen)
        {
            /* Prevents cursor from moving until at least some input is detected */
            if(!moved) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            CreativeInventoryScreen guiContainer = (CreativeInventoryScreen) screen;
            int guiLeft = (guiContainer.width - helper.getContainerWidth(guiContainer)) / 2;
            int guiTop = (guiContainer.height - helper.getContainerHeight(guiContainer)) / 2;
            int mouseX = (int) (targetMouseX * (double) mc.window.getScaledWidth() / (double) mc.window.getWidth());
            int mouseY = (int) (targetMouseY * (double) mc.window.getScaledHeight() / (double) mc.window.getHeight());

            //Slot closestSlot = guiContainer.getSlotUnderMouse();

            /* Finds the closest slot in the GUI within 14 pixels (inclusive) */
            Slot closestSlot = null;
            double closestDistance = -1.0;
            for(Slot slot : guiContainer.getContainer().slots)
            {
                int posX = guiLeft + slot.xPosition + 8;
                int posY = guiTop + slot.yPosition + 8;

                double distance = Math.sqrt(Math.pow(posX - mouseX, 2) + Math.pow(posY - mouseY, 2));
                if((closestDistance == -1.0 || distance < closestDistance) && distance <= 14.0)
                {
                    closestSlot = slot;
                    closestDistance = distance;
                }
            }

            if(closestSlot != null && (closestSlot.hasStack() || !mc.player.inventory.getCursorStack().isEmpty()))
            {
                nearSlot = true;
                int slotCenterXScaled = guiLeft + closestSlot.xPosition + 8;
                int slotCenterYScaled = guiTop + closestSlot.yPosition + 8;
                int slotCenterX = (int) (slotCenterXScaled / ((double) mc.window.getScaledWidth() / (double) mc.window.getWidth()));
                int slotCenterY = (int) (slotCenterYScaled / ((double) mc.window.getScaledHeight() / (double) mc.window.getHeight()));
                double deltaX = slotCenterX - targetMouseX;
                double deltaY = slotCenterY - targetMouseY;

                if(!moving)
                {
                    if(mouseX != slotCenterXScaled || mouseY != slotCenterYScaled)
                    {
                        targetMouseX += deltaX * 0.75;
                        targetMouseY += deltaY * 0.75;
                    }
                    else
                    {
                        mouseSpeedX = 0.0F;
                        mouseSpeedY = 0.0F;
                    }
                }

                mouseSpeedX *= 0.75F;
                mouseSpeedY *= 0.75F;
            }
            else
            {
                mouseSpeedX *= 0.1F;
                mouseSpeedY *= 0.1F;
            }
        }
        else
        {
            mouseSpeedX = 0.0F;
            mouseSpeedY = 0.0F;
        }
    }

    private void handleCreativeScrolling(CreativeInventoryScreen creative, Controller controller)
    {
            int i = (creative.getContainer().itemList.size() + 9 - 1) / 9 - 5;
            int dir = 0;

            if(controller.getSDL2Controller().getButton(SDL_CONTROLLER_BUTTON_DPAD_UP) || controller.getRThumbStickYValue() <= -0.8F)
            {
                dir = 1;
            }
            else if(controller.getSDL2Controller().getButton(SDL_CONTROLLER_BUTTON_DPAD_DOWN) || controller.getRThumbStickYValue() >= 0.8F)
            {
                dir = -1;
            }

            float currentScroll = helper.getScrollPosition(creative);
            currentScroll = (float) ((double) currentScroll - (double) dir / (double) i);
            currentScroll = MathHelper.clamp(currentScroll, 0.0F, 1.0F);
            helper.setScrollPosition(creative, currentScroll);
            creative.getContainer().method_2473(currentScroll);
    }

    /**
     * Invokes a mouse click in a GUI. This is modified version that is designed for controllers.
     * Upon clicking, mouse released is called straight away to make sure dragging doesn't happen.
     *
     * @param screen the screen instance
     * @param button the button to click with
     */
    private void invokeMouseClick(Screen screen, int button)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(screen != null)
        {
            double mouseX = mc.mouse.getX();
            double mouseY = mc.mouse.getY();
            if(Controllable.getController() != null && Controllable.getOptions().isVirtualMouse() && lastUse > 0)
            {
                mouseX = virtualMouseX;
                mouseY = virtualMouseY;
            }
            mouseX = mouseX * (double) mc.window.getScaledWidth() / (double) mc.window.getWidth();
            mouseY = mouseY * (double) mc.window.getScaledHeight() / (double) mc.window.getHeight();

            helper.setActiveMouseButton(mc.mouse, button);
            helper.setMouseTime(mc.mouse, GlfwUtil.getTime());

            double finalMouseX = mouseX;
            double finalMouseY = mouseY;
            
            screen.mouseClicked(finalMouseX, finalMouseY, button);
                
        }
    }

    /**
     * Invokes a mouse released in a GUI. This is modified version that is designed for controllers.
     * Upon clicking, mouse released is called straight away to make sure dragging doesn't happen.
     *
     * @param screen the screen instance
     * @param button the button to click with
     */
    private void invokeMouseReleased(Screen screen, int button)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(screen != null)
        {
            double mouseX = mc.mouse.getX();
            double mouseY = mc.mouse.getY();
            if(Controllable.getController() != null && Controllable.getOptions().isVirtualMouse() && lastUse > 0)
            {
                mouseX = virtualMouseX;
                mouseY = virtualMouseY;
            }
            mouseX = mouseX * (double) mc.window.getScaledWidth() / (double) mc.window.getWidth();
            mouseY = mouseY * (double) mc.window.getScaledHeight() / (double) mc.window.getHeight();

            helper.setActiveMouseButton(mc.mouse, -1);

            double finalMouseX = mouseX;
            double finalMouseY = mouseY;
            
            screen.mouseReleased(finalMouseX, finalMouseY, button);
         }           
        }
    }

