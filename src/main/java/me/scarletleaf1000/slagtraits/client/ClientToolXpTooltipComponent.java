package me.scarletleaf1000.slagtraits.client;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.leveling.ToolXpTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class ClientToolXpTooltipComponent implements ClientTooltipComponent {
    private static final ResourceLocation BAR_BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "textures/gui/tool_xp_background.png");
    private static final ResourceLocation BAR_PROGRESS =
            ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "textures/gui/tool_xp_progress.png");

    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 5;
    private static final int TEXT_GAP = 4;
    private static final int TEXT_COLOR = 0xFFAAAAAA;

    private final ToolXpTooltipComponent data;

    public ClientToolXpTooltipComponent(ToolXpTooltipComponent data) {
        this.data = data;
    }

    private String progressText() {
        return "(" + data.xpIntoLevel() + "/" + data.xpForNextLevel() + ")";
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getWidth(Font font) {
        return BAR_WIDTH + TEXT_GAP + font.width(progressText());
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int barY = y + (getHeight() - BAR_HEIGHT) / 2;
        graphics.blit(BAR_BACKGROUND, x, barY, 0, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        long needed = data.xpForNextLevel();
        if (needed > 0) {
            int fill = (int) Math.min(BAR_WIDTH, Math.round(BAR_WIDTH * (data.xpIntoLevel() / (double) needed)));
            if (fill > 0) {
                graphics.blit(BAR_PROGRESS, x, barY, 0, 0, 0, fill, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            }
        }

        graphics.drawString(font, progressText(), x + BAR_WIDTH + TEXT_GAP, y + 1, TEXT_COLOR, false);
    }
}
