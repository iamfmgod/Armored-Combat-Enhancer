package com.iamfmgod.armoredcombatenhancer.client.gui;

import com.iamfmgod.armoredcombatenhancer.ModSettings;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.ui.UIModule;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiArmoredCombatEnhancer extends GuiScreen {
    private GuiButton btnStaggerToggle;
    private GuiButton btnWeightMinus, btnWeightPlus;
    private GuiButton btnStaggerMinus, btnStaggerPlus;
    private GuiButton btnDamageMinus, btnDamagePlus;
    private GuiButton btnKnockMinus, btnKnockPlus;
    private GuiButton btnClose;

    private final String title = "Armored Combat Enhancer Options";

    @Override
    public void initGui() {
        this.buttonList.clear();

        int cx = this.width / 2;
        int cy = this.height / 2;
        int y  = cy - 60;

        // Stagger toggle
        btnStaggerToggle = new GuiButton(0, cx - 100, y, 200, 20,
                "Stagger: " + (ModSettings.isStaggerEnabled ? "ON" : "OFF"));
        buttonList.add(btnStaggerToggle);

        // Armor weight row
        y += 25;
        btnWeightMinus = new GuiButton(1, cx - 100, y,  98, 20, "- Armor Weight");
        btnWeightPlus  = new GuiButton(2, cx +   2, y,  98, 20, "+ Armor Weight");
        buttonList.add(btnWeightMinus);
        buttonList.add(btnWeightPlus);

        // Stagger time row
        y += 25;
        btnStaggerMinus = new GuiButton(3, cx - 100, y, 98, 20, "- Stagger Time");
        btnStaggerPlus  = new GuiButton(4, cx +   2, y, 98, 20, "+ Stagger Time");
        buttonList.add(btnStaggerMinus);
        buttonList.add(btnStaggerPlus);

        // Bash damage row
        y += 25;
        btnDamageMinus = new GuiButton(5, cx - 100, y, 98, 20, "- Bash Damage");
        btnDamagePlus  = new GuiButton(6, cx +   2, y, 98, 20, "+ Bash Damage");
        buttonList.add(btnDamageMinus);
        buttonList.add(btnDamagePlus);

        // Bash knockback row
        y += 25;
        btnKnockMinus = new GuiButton(7, cx - 100, y, 98, 20, "- Bash Knockback");
        btnKnockPlus  = new GuiButton(8, cx +   2, y, 98, 20, "+ Bash Knockback");
        buttonList.add(btnKnockMinus);
        buttonList.add(btnKnockPlus);

        // Close button
        y += 30;
        btnClose = new GuiButton(99, cx - 100, y, 200, 20, "Close");
        buttonList.add(btnClose);

        UIModule.resetOverlayTimer();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int cx = this.width / 2;
        int cyTop = this.height / 2 - 80;
        drawCenteredString(this.fontRenderer, title, cx, cyTop, 0xFFFFFF);

        // Draw all buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Prepare value strings
        String armorVal   = String.format("%.2fx", ModSettings.armorWeightMultiplier);
        String stagVal    = String.format("%d ticks", ModSettings.staggerDurationTicks);
        String dmgVal     = String.format("%.1f", ConfigModule.shieldBashDamage);
        String knockVal   = String.format("%.1f", ConfigModule.shieldBashKnockback);

        // Draw value labels to right of "+" buttons
        drawValueLabel(btnWeightPlus,  armorVal);
        drawValueLabel(btnStaggerPlus, stagVal);
        drawValueLabel(btnDamagePlus,  dmgVal);
        drawValueLabel(btnKnockPlus,   knockVal);

        // Draw bars to the right of those labels
        drawBarRightOfValue(btnWeightPlus,  armorVal,  ModSettings.armorWeightMultiplier,  0.1,  3.0,  80, 4, 0x5500FFFF);
        drawBarRightOfValue(btnStaggerPlus, stagVal,   ModSettings.staggerDurationTicks,   1.0, 100.0, 80, 4, 0x55FF00FF);
        drawBarRightOfValue(btnDamagePlus,  dmgVal,    ConfigModule.shieldBashDamage,      0.5,  20.0, 80, 4, 0x55FF8800);
        drawBarRightOfValue(btnKnockPlus,   knockVal,  ConfigModule.shieldBashKnockback,   1.0,  20.0, 80, 4, 0x55FF0000);
    }

    /** Draw a value string immediately to the right of a "+" button */
    private void drawValueLabel(GuiButton btn, String text) {
        int x = btn.x + btn.width + 5;
        int y = btn.y + (btn.height - fontRenderer.FONT_HEIGHT) / 2;
        fontRenderer.drawString(text, x, y, 0xAAAAAA);
    }

    /**
     * Draws a small horizontal bar immediately to the right of the value text.
     *
     * @param btn    The "+" button on that row
     * @param text   The text you just drew next to that button
     * @param val    The current numeric value
     * @param min    The minimum of its range
     * @param max    The maximum of its range
     * @param barW   Width of the preview bar
     * @param barH   Height of the preview bar
     * @param colorA ARGB color for the filled portion
     */
    private void drawBarRightOfValue(GuiButton btn,
                                     String text,
                                     double val,
                                     double min,
                                     double max,
                                     int barW,
                                     int barH,
                                     int colorA) {
        // Starting point of the text
        int textX = btn.x + btn.width + 5;
        int textY = btn.y + (btn.height - fontRenderer.FONT_HEIGHT) / 2;
        int textW = fontRenderer.getStringWidth(text);

        // Bar origin just after text + 5px gap
        int barX0 = textX + textW + 5;
        int barY0 = btn.y + (btn.height - barH) / 2;

        // Draw background
        drawRect(barX0, barY0, barX0 + barW, barY0 + barH, 0xFF333333);

        // Compute fill width
        double norm = (val - min) / (max - min);
        norm = Math.max(0.0, Math.min(1.0, norm));
        int fillW = (int) (barW * norm);

        // Draw filled portion
        drawRect(barX0, barY0, barX0 + fillW, barY0 + barH, colorA);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                ModSettings.isStaggerEnabled = !ModSettings.isStaggerEnabled;
                btnStaggerToggle.displayString =
                        "Stagger: " + (ModSettings.isStaggerEnabled ? "ON" : "OFF");
                break;

            case 1:
                ModSettings.armorWeightMultiplier =
                        Math.max(0.1F, ModSettings.armorWeightMultiplier - 0.1F);
                break;
            case 2:
                ModSettings.armorWeightMultiplier =
                        Math.min(3.0F, ModSettings.armorWeightMultiplier + 0.1F);
                break;

            case 3:
                ModSettings.staggerDurationTicks =
                        Math.max(1, ModSettings.staggerDurationTicks - 5);
                break;
            case 4:
                ModSettings.staggerDurationTicks =
                        Math.min(100, ModSettings.staggerDurationTicks + 5);
                break;

            case 5:
                ConfigModule.shieldBashDamage =
                        Math.max(0.5F, ConfigModule.shieldBashDamage - 0.5F);
                break;
            case 6:
                ConfigModule.shieldBashDamage =
                        Math.min(20F, ConfigModule.shieldBashDamage + 0.5F);
                break;

            case 7:
                ConfigModule.shieldBashKnockback =
                        Math.max(1F, ConfigModule.shieldBashKnockback - 0.5F);
                break;
            case 8:
                ConfigModule.shieldBashKnockback =
                        Math.min(20F, ConfigModule.shieldBashKnockback + 0.5F);
                break;

            case 99:
                mc.displayGuiScreen(null);
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}