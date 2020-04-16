package com.mrcrayfish.controllable.client.gui;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.settings.ControllerOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

/**
 * Author: MrCrayfish
 */
public class SettingsScreen extends Screen
{
    private static final Option[] OPTIONS = new Option[]{ControllerOptions.FORCE_FEEDBACK, ControllerOptions.AUTO_SELECT, ControllerOptions.RENDER_MINI_PLAYER, ControllerOptions.VIRTUAL_MOUSE, ControllerOptions.CONSOLE_HOTBAR, ControllerOptions.CONTROLLER_TYPE, ControllerOptions.CURSOR_TYPE, ControllerOptions.INVERT_LOOK, ControllerOptions.DEAD_ZONE, ControllerOptions.ROTATION_SPEED, ControllerOptions.MOUSE_SPEED};
    private final Screen parentScreen;

    protected SettingsScreen(Screen parentScreen)
    {
        super(new TranslatableText("controllable.gui.title.settings"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init()
    {
        for(int i = 0; i < OPTIONS.length; i++)
        {
            Option option = OPTIONS[i];
            int x = this.width / 2 - 155 + i % 2 * 160;
            int y = this.height / 6 + 24 * (i >> 1);
            this.addButton(option.createButton(this.minecraft.options, x, y, 150));
        }

        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (OPTIONS.length + 1) / 2, 200, 20, I18n.translate("gui.done"), (button) -> {
            this.minecraft.openScreen(this.parentScreen);
        }));
    }

    @Override
    public void removed()
    {
        Controllable.getOptions().saveOptions();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, partialTicks);
    }
}
