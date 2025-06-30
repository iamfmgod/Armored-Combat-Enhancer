package com.iamfmgod.armoredcombatenhancer.client.gui;

import com.iamfmgod.armoredcombatenhancer.ArmoredCombatEnhancer;
import com.iamfmgod.armoredcombatenhancer.modules.progression.IPlayerProgression;
import com.iamfmgod.armoredcombatenhancer.modules.progression.PlayerProgressionProvider;
import com.iamfmgod.armoredcombatenhancer.modules.progression.ProgressionUpgradeMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiSkillTree extends GuiScreen {
    private static final int BTN_DASH     = 0;
    private static final int BTN_BASH     = 1;
    private static final int BTN_COOLDOWN = 2;
    private static final int WIDTH  = 200;
    private static final int HEIGHT = 150;

    private int guiLeft, guiTop;
    private GuiButton btnDash, btnBash, btnCooldown;
    private IPlayerProgression prog;

    @Override
    public void initGui() {
        System.out.println("[ACE] GuiSkillTree.initGui() started");
        buttonList.clear();
        guiLeft = (width - WIDTH) / 2;
        guiTop = (height - HEIGHT) / 2;

        prog = Minecraft.getMinecraft().player.getCapability(
                PlayerProgressionProvider.CAP, null
        );

        // Temporary fallback to prevent silent close
        if (prog == null) {
            System.out.println("[ACE] Warning: Capability is null in GuiSkillTree");
            drawCenteredString(fontRenderer, "Progression data not available.", width/2, height/2, 0xFF0000);
            return;
        }

        btnDash     = new GuiButton(BTN_DASH,     guiLeft + 20, guiTop + 40, 160, 20, "");
        btnBash     = new GuiButton(BTN_BASH,     guiLeft + 20, guiTop + 70, 160, 20, "");
        btnCooldown = new GuiButton(BTN_COOLDOWN, guiLeft + 20, guiTop +100, 160, 20, "");

        buttonList.add(btnDash);
        buttonList.add(btnBash);
        buttonList.add(btnCooldown);

        updateButtons();
    }

    private void updateButtons() {
        if (prog == null) return;
        int pts = prog.getPoints();

        boolean canDash = prog.getDashTier() < 3 && pts > 0;
        btnDash.enabled = canDash;
        btnDash.displayString = TextFormatting.GOLD + "Dash [" + prog.getDashTier() + "/3]" +
                (canDash ? TextFormatting.GREEN + " (1pt)" : TextFormatting.RED + " ✖");

        boolean canBash = prog.getBashTier() < 3 && pts > 0;
        btnBash.enabled = canBash;
        btnBash.displayString = TextFormatting.GOLD + "Bash [" + prog.getBashTier() + "/3]" +
                (canBash ? TextFormatting.GREEN + " (1pt)" : TextFormatting.RED + " ✖");

        boolean canCd = prog.getCooldownTier() < 3 && pts > 0;
        btnCooldown.enabled = canCd;
        btnCooldown.displayString = TextFormatting.GOLD + "Cooldown [" + prog.getCooldownTier() + "/3]" +
                (canCd ? TextFormatting.GREEN + " (1pt)" : TextFormatting.RED + " ✖");
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (prog == null) return;

        switch (button.id) {
            case BTN_DASH:
                ArmoredCombatEnhancer.NETWORK.sendToServer(new ProgressionUpgradeMessage("dash"));
                break;
            case BTN_BASH:
                ArmoredCombatEnhancer.NETWORK.sendToServer(new ProgressionUpgradeMessage("bash"));
                break;
            case BTN_COOLDOWN:
                ArmoredCombatEnhancer.NETWORK.sendToServer(new ProgressionUpgradeMessage("cooldown"));
                break;
        }

        prog = Minecraft.getMinecraft().player.getCapability(
                PlayerProgressionProvider.CAP, null
        );
        updateButtons();
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();

        // ⬇ refresh capability each frame to avoid stale reference
        prog = Minecraft.getMinecraft().player.getCapability(
            PlayerProgressionProvider.CAP, null
        );

        drawCenteredString(fontRenderer,
                TextFormatting.UNDERLINE + "A.C.E. Skill Tree",
                width / 2, guiTop + 10, 0xFFFFFF);

        if (prog != null) {
            String ptsStr = TextFormatting.YELLOW + "Points: " +
                    TextFormatting.AQUA + prog.getPoints();
            drawCenteredString(fontRenderer, ptsStr, width / 2, guiTop + 25, 0xFFFFFF);
        } else {
            drawCenteredString(fontRenderer,
                TextFormatting.RED + "[No data]", width / 2, guiTop + 25, 0xFF6666);
        }

        super.drawScreen(mx, my, pt);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}