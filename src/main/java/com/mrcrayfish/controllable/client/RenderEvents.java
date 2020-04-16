package com.mrcrayfish.controllable.client;

import com.hared.controllable.events.AvailableActionsCallback;
import com.hared.controllable.events.RenderAvailableActionCallback;
import com.hared.controllable.events.RenderPlayerPreviewCallback;
import com.hared.controllable.util.UtilHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.Reference;
import com.mrcrayfish.controllable.event.AvailableActionsEvent;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class RenderEvents
{
	private final UtilHelper helper = new UtilHelper(); 
    private static final Identifier CONTROLLER_BUTTONS = new Identifier(Reference.MOD_ID, "textures/gui/buttons.png");

    private Map<Integer, Action> actions = new HashMap<>();

    public void onClientTick()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player != null && !mc.options.hudHidden)
        {
            actions = new HashMap<>();

            if(mc.currentScreen instanceof ContainerScreen)
            {
                if(mc.player.inventory.getCursorStack().isEmpty())
                {
                    ContainerScreen<?> container = (ContainerScreen<?>) mc.currentScreen;
                    if(helper.getFocusedSlot(container) != null)
                    {
                        Slot slot = helper.getFocusedSlot(container);
                        if(slot.hasStack())
                        {
                            actions.put(Buttons.A, new Action(I18n.translate("controllable.action.pickup_stack"), Action.Side.LEFT));
                            actions.put(Buttons.X, new Action(I18n.translate("controllable.action.pickup_item"), Action.Side.LEFT));
                            actions.put(Buttons.B, new Action(I18n.translate("controllable.action.quick_move"), Action.Side.LEFT));
                        }
                    }
                }
                else
                {
                    actions.put(Buttons.A, new Action(I18n.translate("controllable.action.place_stack"), Action.Side.LEFT));
                    actions.put(Buttons.X, new Action(I18n.translate("controllable.action.place_item"), Action.Side.LEFT));
                }

                actions.put(Buttons.Y, new Action(I18n.translate("controllable.action.close_inventory"), Action.Side.RIGHT));
            }
            else if(mc.currentScreen == null)
            {
                boolean blockHit = mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK;
                boolean canOpenBlock = false;
                if(blockHit)
                {
                	BlockHitResult blockRayTraceResult = (BlockHitResult) mc.crosshairTarget;
                    canOpenBlock = mc.world.getBlockState(blockRayTraceResult.getBlockPos()).getBlock() instanceof BlockWithEntity;
                }

                if(!mc.player.isUsingItem())
                {
                    if(blockHit)
                    {
                        actions.put(Buttons.RIGHT_TRIGGER, new Action(I18n.translate("controllable.action.break"), Action.Side.RIGHT));
                    }
                    else
                    {
                        actions.put(Buttons.RIGHT_TRIGGER, new Action(I18n.translate("controllable.action.attack"), Action.Side.RIGHT));
                    }
                }

                ItemStack offHandStack = mc.player.getOffHandStack();
                if(offHandStack.getUseAction() != UseAction.NONE)
                {
                    switch(offHandStack.getUseAction())
                    {
                        case EAT:
                            if(mc.player.getHungerManager().isNotFull())
                            {
                                actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.eat"), Action.Side.RIGHT));
                            }
                            break;
                        case DRINK:
                            actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.drink"), Action.Side.RIGHT));
                            break;
                        case BLOCK:
                            actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.block"), Action.Side.RIGHT));
                            break;
                        case BOW:
                            actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.pull_bow"), Action.Side.RIGHT));
                            break;
					default:
						break;
                    }
                }

                ItemStack currentItem = mc.player.inventory.getMainHandStack();
                if(currentItem.getUseAction() != UseAction.NONE)
                {
                    switch(currentItem.getUseAction())
                    {
                        case EAT:
                            if(mc.player.getHungerManager().isNotFull())
                            {
                                actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.eat"), Action.Side.RIGHT));
                            }
                            break;
                        case DRINK:
                            actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.drink"), Action.Side.RIGHT));
                            break;
                        case BLOCK:
                            actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.block"), Action.Side.RIGHT));
                            break;
                        case BOW:
                            actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.pull_bow"), Action.Side.RIGHT));
                            break;
                    }
                }
                else if(currentItem.getItem() instanceof BlockItem)
                {
                    if(blockHit)
                    {
                        //TODO figure out logic to determine if block can be placed.
                        /*BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) mc.objectMouseOver;
                        BlockItem item = (BlockItem) currentItem.getItem();
                        ItemUseContext itemUseContext = new ItemUseContext(mc.player, Hand.MAIN_HAND, blockRayTraceResult);
                        BlockItemUseContext blockItemUseContext = new BlockItemUseContext(itemUseContext);
                        blockItemUseContext = item.getBlockItemUseContext(blockItemUseContext);
                        if(blockItemUseContext != null)
                        {
                            BlockState state = item.getStateForPlacement(blockItemUseContext);
                            if(state != null)
                            {
                                actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.place_block"), Action.Side.RIGHT));
                            }
                        }*/
                        actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.place_block"), Action.Side.RIGHT));
                    }
                }
                else if(!currentItem.isEmpty() && !mc.player.isUsingItem())
                {
                    actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.use_item"), Action.Side.RIGHT));
                }

                if(!mc.player.isSneaking() && blockHit && canOpenBlock && !mc.player.isUsingItem())
                {
                    actions.put(Buttons.LEFT_TRIGGER, new Action(I18n.translate("controllable.action.interact"), Action.Side.RIGHT));
                }

                //actions.put(Buttons.A, new Action(I18n.translate("controllable.action.jump"), Action.Side.LEFT)); //TODO make a verbose action config option

                actions.put(Buttons.Y, new Action(I18n.translate("controllable.action.inventory"), Action.Side.LEFT));

                if(!mc.player.getOffHandStack().isEmpty() || !mc.player.inventory.getMainHandStack().isEmpty())
                {
                    //actions.put(Buttons.X, new Action(I18n.translate("controllable.action.swap_hands"), Action.Side.LEFT));  //TODO make a verbose action config option
                }

                if(mc.player.isRiding())
                {
                    actions.put(Buttons.LEFT_THUMB_STICK, new Action(I18n.translate("controllable.action.dismount"), Action.Side.RIGHT));
                }
                else
                {
                    //actions.put(Buttons.LEFT_THUMB_STICK, new translate(I18n.format("controllable.action.sneak"), Action.Side.RIGHT));  //TODO make a verbose action config option
                }

                if(!mc.player.inventory.getMainHandStack().isEmpty())
                {
                    actions.put(Buttons.DPAD_DOWN, new Action(I18n.translate("controllable.action.drop_item"), Action.Side.LEFT));
                }
            }

            AvailableActionsCallback.EVENT.invoker().onAvailableActionsEvent(new AvailableActionsEvent(actions));
        }
    }

    public void onRenderScreen()
    {
        if(Controllable.getInput().getLastUse() <= 0)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.options.hudHidden)
            return;

        if(Controllable.getController() == null)
            return;

        GlStateManager.pushMatrix();
        {
            if(!RenderAvailableActionCallback.EVENT.invoker().onRenderAvailableActionEvent())
            {
            	InGameHud guiIngame = mc.inGameHud;
                boolean isChatVisible = mc.currentScreen == null && helper.getVisibleMessages(mc.inGameHud.getChatHud()).stream().anyMatch(chatLine -> guiIngame.getTicks() - chatLine.getCreationTick() < 200);

                int leftIndex = 0;
                int rightIndex = 0;
                for(Integer button : actions.keySet())
                {
                    Action action = actions.get(button);
                    Action.Side side = action.getSide();

                    int remappedButton = button;
                    Controller controller = Controllable.getController();
                    Mappings.Entry mapping = controller.getMapping();
                    if(mapping != null)
                    {
                        remappedButton = mapping.remap(button);
                    }

                    int texU = remappedButton * 13;
                    int texV = Controllable.getOptions().getControllerType().ordinal() * 13;
                    int size = 13;

                    int x = side == Action.Side.LEFT ? 5 : mc.window.getScaledWidth() - 5 - size;
                    int y = mc.window.getScaledHeight() + (side == Action.Side.LEFT ? leftIndex : rightIndex) * -15 - size - 5;

                    mc.getTextureManager().bindTexture(CONTROLLER_BUTTONS);
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableLighting();

                    if(isChatVisible && side == Action.Side.LEFT && leftIndex >= 2)
                        continue;

                    /* Draw buttons icon */
                    DrawableHelper.blit(x, y, texU, texV, size, size, 256, 256);

                    /* Draw description text */
                    if(side == Action.Side.LEFT)
                    {
                        mc.textRenderer.draw(action.getDescription(), x + 18, y + 3, Color.WHITE.getRGB());
                        leftIndex++;
                    }
                    else
                    {
                        int width = mc.textRenderer.getStringWidth(action.getDescription());
                        mc.textRenderer.draw(action.getDescription(), x - 5 - width, y + 3, Color.WHITE.getRGB());
                        rightIndex++;
                    }
                }
            }

            if(mc.player != null && mc.currentScreen == null && Controllable.getOptions().isRenderMiniPlayer())
            {
                if(!RenderPlayerPreviewCallback.EVENT.invoker().onRenderPlayerPreviewEvent())
                {
                    InventoryScreen.drawEntity(20, 45, 20, 0, 0, mc.player);
                }
            }
        }
        GlStateManager.popMatrix();
    }
}
