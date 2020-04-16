package com.mrcrayfish.controllable.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mrcrayfish.controllable.Reference;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

/**
 * Author: MrCrayfish
 */
public class ControllerButton extends ButtonWidget
{
    private static final Identifier TEXTURE = new Identifier(Reference.MOD_ID, "textures/gui/controller.png");

    public ControllerButton(int x, int y, PressAction pressable)
    {
        super(x, y, 20, 20, "", pressable);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            boolean mouseOver = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int textureV = 43;
            if (mouseOver)
            {
                textureV += this.height;
            }
            this.blit(this.x, this.y, 0, textureV, this.width, this.height);
        }
    }
}
