package com.iamfmgod.armoredcombatenhancer.client.gui;

import com.iamfmgod.armoredcombatenhancer.ModSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import java.io.IOException;

public class GuiArmoredCombatEnhancer extends GuiScreen {

    private GuiButton btnToggleStagger;
    private GuiButton btnClose;
    private String title = "Armored Combat Enhancer Options";

    @Override
    public void initGui() {
        this.buttonList.clear();

        btnToggleStagger = new GuiButton(0, this.width / 2 - 100, this.height / 2 - 10, 200, 20,
                "Stagger Effect: " + (ModSettings.isStaggerEnabled ? "ON" : "OFF"));
        this.buttonList.add(btnToggleStagger);

        btnClose = new GuiButton(1, this.width / 2 - 100, this.height / 2 + 20, 200, 20, "Close");
        this.buttonList.add(btnClose);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Current Armor Weight Multiplier: 1.0", this.width / 2, this.height / 2 - 30, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            ModSettings.isStaggerEnabled = !ModSettings.isStaggerEnabled;
            btnToggleStagger.displayString = "Stagger Effect: " + (ModSettings.isStaggerEnabled ? "ON" : "OFF");
            System.out.println("Stagger effect toggled! Now: " + (ModSettings.isStaggerEnabled ? "Enabled" : "Disabled"));
        } else if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}